package com.example.transactionservice.controller;

import com.example.transactionservice.dto.TransactionDTO;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Tạo giao dịch chuyển tiền mới
     */
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestHeader(name = "uid", required = true) Long userId,
            @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(transactionDTO));
    }

    /**
     * Xem chi tiết giao dịch
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransaction(
            @RequestHeader(name = "uid", required = true) Long userId,
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionById(userId, transactionId));
    }

    /**
     * Xem tất cả giao dịch của user (gửi hoặc nhận)
     */
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(
            @RequestHeader(name = "uid", required = true) Long userId) {
        return ResponseEntity.ok(transactionService.getUserTransactions(userId));
    }

    /**
     * Xem giao dịch gửi của user
     */
    @GetMapping("/sent")
    public ResponseEntity<List<TransactionDTO>> getSentTransactions(
            @RequestHeader(name = "uid", required = true) Long userId) {
        return ResponseEntity.ok(transactionService.getSentTransactions(userId));
    }

    /**
     * Xem giao dịch nhận của user
     */
    @GetMapping("/received")
    public ResponseEntity<List<TransactionDTO>> getReceivedTransactions(
            @RequestHeader(name = "uid", required = true) Long userId) {
        return ResponseEntity.ok(transactionService.getReceivedTransactions(userId));
    }

    /**
     * Cập nhật trạng thái giao dịch
     */
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<TransactionDTO> updateTransactionStatus(
            @RequestHeader(name = "uid", required = true) Long userId,
            @PathVariable Long transactionId,
            @RequestParam String status) {
        return ResponseEntity.ok(transactionService.updateTransactionStatus(userId, transactionId, status));
    }

    /**
     * Hủy giao dịch
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> cancelTransaction(
            @RequestHeader(name = "uid", required = true) Long userId,
            @PathVariable Long transactionId) {
        transactionService.cancelTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
