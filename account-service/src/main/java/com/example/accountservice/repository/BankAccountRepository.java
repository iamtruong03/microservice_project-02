package com.example.accountservice.repository;

import com.example.accountservice.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByUserId(Long userId);
    List<BankAccount> findAllByUserIdAndIsActiveTrue(Long userId);
    boolean existsBankAccountByAccountNumber(String accountNumber);
}
