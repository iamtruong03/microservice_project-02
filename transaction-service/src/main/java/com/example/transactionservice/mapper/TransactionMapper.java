package com.example.transactionservice.mapper;

import com.example.transactionservice.dto.TransactionDTO;
import com.example.transactionservice.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    public TransactionDTO transactionToTransactionDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionDTO(
            transaction.getId(),
            transaction.getFromAccountId(),
            transaction.getToAccountId(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getAccountSequence(),
            transaction.getTransactionTypeId(),
            transaction.getState(),
            transaction.getReferenceCode(),
            transaction.getDescription(),
            transaction.getCreatedBy(),
            transaction.getCreatedAt(),
            transaction.getCompletedAt()
        );
    }
    
    public Transaction transactionDTOToTransaction(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setId(transactionDTO.id());
        transaction.setFromAccountId(transactionDTO.fromAccountId());
        transaction.setToAccountId(transactionDTO.toAccountId());
        transaction.setAmount(transactionDTO.amount());
        transaction.setCurrency(transactionDTO.currency());
        transaction.setAccountSequence(transactionDTO.accountSequence());
        transaction.setTransactionTypeId(transactionDTO.transactionTypeId());
        transaction.setState(transactionDTO.state());
        transaction.setReferenceCode(transactionDTO.referenceCode());
        transaction.setDescription(transactionDTO.description());
        transaction.setCreatedBy(transactionDTO.createdBy());
        transaction.setCreatedAt(transactionDTO.createdAt());
        transaction.setCompletedAt(transactionDTO.completedAt());
        return transaction;
    }
}
