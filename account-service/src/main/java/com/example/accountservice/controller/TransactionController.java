package com.example.accountservice.controller;

import com.example.accountservice.dto.TransactionDTO;
import com.example.accountservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounting")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(transactionDTO));
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(uid, id));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(
            @RequestHeader(name = "uid", required = true) Long uid) {
        return ResponseEntity.ok(transactionService.getAllTransactions(uid));
    }

    @GetMapping("/orders/{orderId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByOrder(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(transactionService.getTransactionsByOrderId(uid, orderId));
    }

    @GetMapping("/customers/{customerId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCustomer(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long customerId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomerId(uid, customerId));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long id,
            @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.updateTransaction(uid, id, transactionDTO));
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @RequestHeader(name = "uid", required = true) Long uid,
            @PathVariable Long id) {
        transactionService.deleteTransaction(uid, id);
        return ResponseEntity.noContent().build();
    }
}
