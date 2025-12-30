package com.example.transactionservice.repository;

import com.example.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromUserId(Long fromUserId);
    List<Transaction> findByToUserId(Long toUserId);
    List<Transaction> findByFromUserIdOrToUserId(Long fromUserId, Long toUserId);
    List<Transaction> findByStatus(String status);
}
