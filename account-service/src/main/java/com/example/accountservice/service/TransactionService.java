package com.example.accountservice.service;

import com.example.accountservice.dto.TransactionDTO;
import com.example.accountservice.model.Transaction;
import com.example.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setOrderId(transactionDTO.getOrderId());
        transaction.setCustomerId(transactionDTO.getCustomerId());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setDescription(transactionDTO.getDescription());

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Send event to Kafka
        kafkaTemplate.send("accounting-events", "transaction_created", savedTransaction);

        return convertToDTO(savedTransaction);
    }

    public TransactionDTO getTransactionById(Long uid, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!uid.equals(transaction.getCustomerId())) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(transaction);
    }

    public List<TransactionDTO> getAllTransactions(Long uid) {
        if (uid == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        return transactionRepository.findByCustomerId(uid).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByOrderId(Long uid, Long orderId) {
        List<TransactionDTO> transactions = transactionRepository.findByOrderId(orderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // Verify user has access to at least one transaction
        if (!transactions.isEmpty()) {
            Transaction firstTx = transactionRepository.findByOrderId(orderId).get(0);
            if (!uid.equals(firstTx.getCustomerId())) {
                throw new RuntimeException("Access denied");
            }
        }
        
        return transactions;
    }

    public List<TransactionDTO> getTransactionsByCustomerId(Long uid, Long customerId) {
        if (!uid.equals(customerId)) {
            throw new RuntimeException("Access denied");
        }
        
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO updateTransaction(Long uid, Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!uid.equals(transaction.getCustomerId())) {
            throw new RuntimeException("Access denied");
        }

        transaction.setStatus(transactionDTO.getStatus());
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Send event to Kafka
        kafkaTemplate.send("accounting-events", "transaction_updated", updatedTransaction);

        return convertToDTO(updatedTransaction);
    }

    public void deleteTransaction(Long uid, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!uid.equals(transaction.getCustomerId())) {
            throw new RuntimeException("Access denied");
        }
        
        transactionRepository.deleteById(id);
    }

    @KafkaListener(topics = "order-events", groupId = "accounting-service-group")
    public void handleOrderEvents(String message) {
        System.out.println("Received order event: " + message);
        // Create corresponding accounting transaction
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getOrderId(),
                transaction.getCustomerId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
