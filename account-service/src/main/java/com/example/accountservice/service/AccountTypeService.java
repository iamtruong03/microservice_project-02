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
        return accountTypeRepository.findAllByIsActiveTrue();
    }

    public Optional<AccountType> getById(Long id) {
        return accountTypeRepository.findById(id);
    }

    public AccountType create(String uid, AccountType accountType) {
        accountType.setCreatedBy(uid);
        accountType.setIsActive(true);
        return accountTypeRepository.save(accountType);
    }

    public AccountType update(String uid, AccountType accountType) {
        accountType.setUpdatedBy(uid);
        accountType.setIsActive(true);
        return accountTypeRepository.save(accountType);
    }

    public void delete(Long id) {
        accountTypeRepository.deleteById(id);
    }

    public AccountType softDelete(String uid, Long id) {
        Optional<AccountType> optionalAccountType = accountTypeRepository.findById(id);
        if (optionalAccountType.isPresent()) {
            AccountType accountType = optionalAccountType.get();
            accountType.setIsActive(false);
            accountType.setUpdatedBy(uid);
            return accountTypeRepository.save(accountType);
        }
        return null;
    }
}
