package com.example.transactionservice.service;

import com.example.transactionservice.dto.TransactionDTO;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Tạo giao dịch chuyển tiền giữa hai user
     */
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setFromUserId(transactionDTO.fromUserId());
        transaction.setToUserId(transactionDTO.toUserId());
        transaction.setFromAccountId(transactionDTO.fromAccountId());
        transaction.setToAccountId(transactionDTO.toAccountId());
        transaction.setAmount(transactionDTO.amount());
        transaction.setStatus("PENDING");
        transaction.setDescription(transactionDTO.description());

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Publish transaction created event
        kafkaTemplate.send("user-transfer-events", "transfer_initiated", savedTransaction);

        return convertToDTO(savedTransaction);
    }

    /**
     * Xem chi tiết giao dịch
     */
    public TransactionDTO getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Kiểm tra quyền truy cập
        if (!transaction.getFromUserId().equals(userId) && !transaction.getToUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(transaction);
    }

    /**
     * Xem tất cả giao dịch của user (gửi hoặc nhận)
     */
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByFromUserIdOrToUserId(userId, userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Xem tất cả giao dịch gửi của user
     */
    public List<TransactionDTO> getSentTransactions(Long userId) {
        return transactionRepository.findByFromUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Xem tất cả giao dịch nhận của user
     */
    public List<TransactionDTO> getReceivedTransactions(Long userId) {
        return transactionRepository.findByToUserId(userId)
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

        if (!transaction.getFromUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        transaction.setStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Publish transaction status updated event
        String eventType = status.equals("COMPLETED") ? "transfer_completed" : "transfer_" + status.toLowerCase();
        kafkaTemplate.send("user-transfer-events", eventType, updatedTransaction);

        return convertToDTO(updatedTransaction);
    }

    /**
     * Hủy giao dịch (chỉ có thể hủy giao dịch ở trạng thái PENDING)
     */
    @Transactional
    public void cancelTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getFromUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (!transaction.getStatus().equals("PENDING")) {
            throw new RuntimeException("Cannot cancel completed or failed transaction");
        }

        transaction.setStatus("CANCELLED");
        transactionRepository.save(transaction);

        // Publish transaction cancelled event
        kafkaTemplate.send("user-transfer-events", "transfer_cancelled", transaction);
    }

    /**
     * Helper: Convert Transaction to DTO
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getFromUserId(),
                transaction.getToUserId(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
