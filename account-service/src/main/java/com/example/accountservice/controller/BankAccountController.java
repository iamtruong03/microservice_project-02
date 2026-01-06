package com.example.accountservice.controller;

import com.example.accountservice.dto.*;
import com.example.accountservice.service.BankAccountService;
import com.example.accountservice.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

  @Autowired
  private BankAccountService bankAccountService;

  @PostMapping("/create")
  public ResponseEntity<?> createBankAccount(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @RequestBody BankAccountDTO dto) {
    try {
      BankAccountDTO account = bankAccountService.createBankAccount(uid, dto);
      return ResponseUtils.handlerCreated(account);
    } catch (Exception e) {
      return ResponseUtils.handlerException(e);
    }
  }

  /**
   * Xem số dư tài khoản
   */
  @GetMapping("/balance/{accountId}")
  public ResponseEntity<?> getAccountBalance(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long accountId) {
    try {
      BankAccountDTO account = bankAccountService.getAccountBalance(uid, accountId);
      return ResponseUtils.handlerSuccess(account);
    } catch (Exception e) {
      return ResponseUtils.handlerException(e);
    }
  }

  /**
   * Xem tất cả tài khoản của user
   */
  @GetMapping
  public ResponseEntity<?> getUserAccounts(
      @RequestHeader(name = "uid", defaultValue = "") String uid) {
    try {
      List<BankAccountDTO> accounts = bankAccountService.getUserAccounts(uid);
      return ResponseUtils.handlerSuccess(accounts);
    } catch (Exception e) {
      return ResponseUtils.handlerException(e);
    }
  }

  /**
   * Cập nhật thông tin tài khoản
   */
  @PutMapping("/update")
  public ResponseEntity<?> updateAccount(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @RequestBody BankAccountDTO dto) {
    try {
      BankAccountDTO account = bankAccountService.updateAccount(uid, dto);
      return ResponseUtils.handlerSuccess(account);
    } catch (Exception e) {
      return ResponseUtils.handlerException(e);
    }
  }

  /**
   * Thống kê tổng số dư và Transaction
   */
  @GetMapping("/statistics")
  public ResponseEntity<?> getAccountStatistics(
      @RequestHeader(name = "uid", defaultValue = "") String uid) {
    try {
      AccountStatisticsDTO stats = bankAccountService.getAccountStatistics(uid);
      return ResponseUtils.handlerSuccess(stats);
    } catch (Exception e) {
      return ResponseUtils.handlerException(e);
    }
  }

  @PostMapping("/lock/{accountId}")
  public ResponseEntity<?> unActiveAccount(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long accountId) {
    try {
      BankAccountDTO account = bankAccountService.unActiveAccount(uid, accountId);
      return ResponseUtils.handlerSuccess(account);
    } catch (Exception e) {
      return ResponseUtils.handlerException(e);
    }
  }

  @PostMapping("/un-lock/{accountId}")
  public ResponseEntity<?> activeAccount(
      @RequestHeader(name = "uid", defaultValue = "") String uid,
      @PathVariable Long accountId) {
    try {
      BankAccountDTO account = bankAccountService.activeAccount(uid, accountId);
      return ResponseUtils.handlerSuccess(account);
    } catch (Exception e) {
      return ResponseUtils.handlerException(e);
    }
  }
}
