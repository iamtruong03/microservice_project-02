package com.example.transactionservice.service;

import com.example.transactionservice.dto.TransactionDTO;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
// import org.springframework.kafka.core.KafkaTemplate; // Tắt Kafka
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    // private final KafkaTemplate<String, Object> kafkaTemplate; // Tắt Kafka

    /**
     * Tạo giao dịch chuyển tiền giữa hai user
     */
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(transactionDTO.fromAccountId());
        transaction.setToAccountId(transactionDTO.toAccountId());
        transaction.setAmount(transactionDTO.amount());
        transaction.setCurrency(transactionDTO.currency() != null ? transactionDTO.currency() : "USD");
        transaction.setTransactionType(transactionDTO.transactionType());
        transaction.setStatus("PENDING");
        transaction.setReferenceCode(transactionDTO.referenceCode());
        transaction.setDescription(transactionDTO.description());

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Publish transaction created event
        // kafkaTemplate.send("transaction-events", "transaction_created", savedTransaction); // Tắt Kafka

        return convertToDTO(savedTransaction);
    }

    /**
     * Xem chi tiết giao dịch
     */
    public TransactionDTO getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify userId has access to this transaction
        if (!transaction.getFromAccountId().equals(userId) && !transaction.getToAccountId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to transaction");
        }
        
        return convertToDTO(transaction);
    }

    /**
     * Xem tất cả giao dịch của user (gửi hoặc nhận)
     */
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(userId, userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Xem giao dịch gửi của user
     */
    public List<TransactionDTO> getSentTransactions(Long userId) {
        return transactionRepository.findByFromAccountId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Xem giao dịch nhận của user
     */
    public List<TransactionDTO> getReceivedTransactions(Long userId) {
        return transactionRepository.findByToAccountId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Xem tất cả giao dịch theo trạng thái
     */
    public List<TransactionDTO> getTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật trạng thái giao dịch
     */
    @Transactional
    public TransactionDTO updateTransactionStatus(Long userId, Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Verify userId has access to update this transaction
        if (!transaction.getFromAccountId().equals(userId) && !transaction.getToAccountId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to transaction");
        }

        transaction.setStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Publish transaction status updated event
        // kafkaTemplate.send("transaction-events", "transaction_" + status.toLowerCase(), updatedTransaction); // Tắt Kafka

        return convertToDTO(updatedTransaction);
    }

    /**
     * Hủy giao dịch (chỉ có thể hủy giao dịch ở trạng thái PENDING)
     */
    @Transactional
    public void cancelTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Verify userId is the one who initiated the transaction
        if (!transaction.getFromAccountId().equals(userId)) {
            throw new RuntimeException("Only the transaction initiator can cancel");
        }

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new RuntimeException("Cannot cancel completed or failed transaction");
        }

        transaction.setStatus("CANCELLED");
        transactionRepository.save(transaction);

        // Publish transaction cancelled event
        // kafkaTemplate.send("transaction-events", "transaction_cancelled", transaction); // Tắt Kafka
    }

    /**
     * Helper: Convert Transaction to DTO
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getReferenceCode(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }
}
