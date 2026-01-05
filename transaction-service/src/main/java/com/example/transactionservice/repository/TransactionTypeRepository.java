package com.example.transactionservice.repository;

import com.example.transactionservice.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
    List<TransactionType> findAllByIsActiveTrue();
}
