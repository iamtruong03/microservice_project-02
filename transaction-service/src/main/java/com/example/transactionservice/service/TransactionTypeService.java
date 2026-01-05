package com.example.transactionservice.service;

import com.example.transactionservice.model.TransactionType;
import com.example.transactionservice.repository.TransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionTypeService {
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    public List<TransactionType> getAll() {
        return transactionTypeRepository.findAllByIsActiveTrue();
    }

    public Optional<TransactionType> getById(Long id) {
        return transactionTypeRepository.findById(id);
    }

    public TransactionType create(String uid, TransactionType transactionType) {
        transactionType.setCreatedBy(uid);
        transactionType.setIsActive(true);
        return transactionTypeRepository.save(transactionType);
    }

    public TransactionType update(String uid, TransactionType transactionType) {
        transactionType.setUpdatedBy(uid);
        transactionType.setIsActive(true);
        return transactionTypeRepository.save(transactionType);
    }

    public void delete(Long id) {
        transactionTypeRepository.deleteById(id);
    }

    public TransactionType softDelete(String uid, Long id) {
        Optional<TransactionType> optional = transactionTypeRepository.findById(id);
        if (optional.isPresent()) {
            TransactionType transactionType = optional.get();
            transactionType.setIsActive(false);
            transactionType.setUpdatedBy(uid);
            return transactionTypeRepository.save(transactionType);
        }
        return null;
    }
}
