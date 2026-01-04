package com.example.accountservice.service;

import com.example.accountservice.model.AccountType;
import com.example.accountservice.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountTypeService {
    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public List<AccountType> getAll() {
        return accountTypeRepository.findAll();
    }

    public Optional<AccountType> getById(Long id) {
        return accountTypeRepository.findById(id);
    }

    public AccountType create(AccountType accountType) {
        return accountTypeRepository.save(accountType);
    }

    public AccountType update(Long id, AccountType accountType) {
        accountType.setId(id);
        return accountTypeRepository.save(accountType);
    }

    public void delete(Long id) {
        accountTypeRepository.deleteById(id);
    }
}
