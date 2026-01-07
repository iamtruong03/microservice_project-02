package com.example.transactionservice.service;

import com.example.transactionservice.dto.TransactionDTO;
import com.example.transactionservice.mapper.TransactionMapper;
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
    private final TransactionMapper transactionMapper;
    // private final KafkaTemplate<String, Object> kafkaTemplate; // Tắt Kafka

    @Transactional
    public TransactionDTO createTransaction(String uid, TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.transactionDTOToTransaction(transactionDTO);
        transaction.setCurrency(transactionDTO.currency() != null ? transactionDTO.currency() : "USD");
        transaction.setCreatedBy(uid);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Publish transaction created event
        // kafkaTemplate.send("transaction-events", "transaction_created", savedTransaction); // Tắt Kafka

        return convertToDTO(savedTransaction);
    }

    public TransactionDTO getTransactionById(String uid, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        return convertToDTO(transaction);
    }

//    public List<TransactionDTO> getUserTransactions(String userId) {
//        return transactionRepository.findByFromAccountIdOrToAccountId(userId, userId)
//                .stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

    public List<TransactionDTO> getSentTransactions(String userId) {
        return transactionRepository.findByFromAccountId(Long.valueOf(userId))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getReceivedTransactions(String userId) {
        return transactionRepository.findByToAccountId(Long.valueOf(userId))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelTransaction(String userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Verify userId is the one who initiated the transaction
        if (!transaction.getFromAccountId().equals(userId)) {
            throw new RuntimeException("Only the transaction initiator can cancel");
        }

        transaction.setState("CANCELLED");
        transactionRepository.save(transaction);

        // Publish transaction cancelled event
        // kafkaTemplate.send("transaction-events", "transaction_cancelled", transaction); // Tắt Kafka
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return transactionMapper.transactionToTransactionDTO(transaction);
    }
}
