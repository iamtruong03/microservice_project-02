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

    public TransactionDTO getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByOrderId(Long orderId) {
        return transactionRepository.findByOrderId(orderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(transactionDTO.getStatus());
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Send event to Kafka
        kafkaTemplate.send("accounting-events", "transaction_updated", updatedTransaction);

        return convertToDTO(updatedTransaction);
    }

    public void deleteTransaction(Long id) {
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
