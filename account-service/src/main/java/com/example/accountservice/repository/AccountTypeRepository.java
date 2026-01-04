package com.example.accountservice.repository;

import com.example.accountservice.model.AccountType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
  List<AccountType> findAllByIsActiveTrue();
}
