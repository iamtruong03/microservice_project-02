package com.example.transactionservice.mapper;

import com.example.transactionservice.dto.TransactionDTO;
import com.example.transactionservice.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDTO transactionToTransactionDTO(Transaction transaction);
    Transaction transactionDTOToTransaction(TransactionDTO transactionDTO);
}
