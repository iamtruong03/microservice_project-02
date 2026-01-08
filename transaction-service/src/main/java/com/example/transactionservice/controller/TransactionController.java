package com.example.transactionservice.controller;

import com.example.transactionservice.dto.TransactionDTO;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/create")
  public ResponseEntity<TransactionDTO> createTransaction(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @RequestBody TransactionDTO transactionDTO) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(transactionService.createTransaction(uid, transactionDTO));
  }

  @GetMapping("/{transactionId}")
  public ResponseEntity<TransactionDTO> getTransaction(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long transactionId) {
    return ResponseEntity.ok(transactionService.getTransactionById(uid, transactionId));
  }

  /**
   * Xem tất cả giao dịch của user (gửi hoặc nhận)
   */
//  @GetMapping
//  public ResponseEntity<List<TransactionDTO>> getUserTransactions(
//      @RequestHeader(name = "uid", defaultValue = "") String uid)
//      {
//    return ResponseEntity.ok(transactionService.getUserTransactions(uid));
//  }

  @GetMapping("/sent")
  public ResponseEntity<List<TransactionDTO>> getSentTransactions(
      @RequestHeader(name = "uid", defaultValue = "") String uid) {
    return ResponseEntity.ok(transactionService.getSentTransactions(uid));
  }

  @GetMapping("/received")
  public ResponseEntity<List<TransactionDTO>> getReceivedTransactions(
      @RequestHeader(name = "uid", defaultValue = "") String uid) {
    return ResponseEntity.ok(transactionService.getReceivedTransactions(uid));
  }

  @DeleteMapping("/{transactionId}")
  public ResponseEntity<Void> cancelTransaction(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long transactionId) {
    transactionService.cancelTransaction(uid, transactionId);
    return ResponseEntity.noContent().build();
  }
}
