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

    /**
     * Tạo tài khoản ngân hàng (gọi sau khi đăng ký)
     */
    @PostMapping
    public ResponseEntity<?> createBankAccount(
        @RequestHeader(name = "uid", defaultValue = "") String uid,
        @RequestBody BankAccountDTO dto)
        {
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
    @GetMapping("/{accountId}/balance")
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
     * Chuyển tiền nội bộ giữa các tài khoản
     */
    @PostMapping("/transfer-between-accounts")
    public ResponseEntity<?> transferBetweenAccounts(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam(required = false) String description) {
        try {
            BankAccountDTO account = bankAccountService.transferBetweenAccounts(uid, fromAccountId, toAccountId, amount, description);
            return ResponseUtils.handlerSuccess(account);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    /**
     * Rút tiền / Nạp tiền
     */
    @PostMapping("/deposit-withdraw")
    public ResponseEntity<?> depositOrWithdraw(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestBody DepositWithdrawDTO dto) {
        try {
            BankAccountDTO account = bankAccountService.depositOrWithdraw(uid, dto);
            return ResponseUtils.handlerSuccess(account);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    /**
     * Cập nhật thông tin tài khoản
     */
    @PutMapping("/{accountId}")
    public ResponseEntity<?> updateAccount(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long accountId,
            @RequestParam(required = false) String accountType) {
        try {
            BankAccountDTO account = bankAccountService.updateAccount(uid, accountId, accountType);
            return ResponseUtils.handlerSuccess(account);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    /**
     * Khóa/Mở khóa tài khoản
     */
//    @PutMapping("/{accountId}/status")
//    public ResponseEntity<?> updateAccountStatus(
//            @RequestHeader(name = "uid", defaultValue = "") String uid,
//            @PathVariable Long accountId,
//            @RequestParam String status) {
//        try {
//            BankAccountDTO account = bankAccountService.updateAccountStatus(uid, accountId, status);
//            return ResponseUtils.handlerSuccess(account);
//        } catch (Exception e) {
//            return ResponseUtils.handlerException(e);
//        }
//    }

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

    /**
     * Lịch sử giao dịch
     */
    @GetMapping("/transactions/history")
    public ResponseEntity<?> getTransactionHistory(
            @RequestHeader(name = "uid", defaultValue = "") String uid) {
        try {
            AccountStatisticsDTO stats = bankAccountService.getAccountStatistics(uid);
            return ResponseUtils.handlerSuccess(stats);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
