package com.example.accountservice.controller;

import com.example.accountservice.dto.*;
import com.example.accountservice.service.BankAccountService;
import com.example.accountservice.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    /**
     * Tạo tài khoản ngân hàng (gọi sau khi đăng ký)
     */
    @PostMapping
    public ResponseEntity<?> createBankAccount(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam(required = false) String accountType) {
        try {
            Long uidLong = Long.parseLong(uid);
            BankAccountDTO account = bankAccountService.createBankAccount(uidLong, accountType);
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
            Long uidLong = Long.parseLong(uid);
            BankAccountDTO account = bankAccountService.getAccountBalance(uidLong, accountId);
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
            Long uidLong = Long.parseLong(uid);
            List<BankAccountDTO> accounts = bankAccountService.getUserAccounts(uidLong);
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
            Long uidLong = Long.parseLong(uid);
            BankAccountDTO account = bankAccountService.transferBetweenAccounts(uidLong, fromAccountId, toAccountId, amount, description);
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
            Long uidLong = Long.parseLong(uid);
            BankAccountDTO account = bankAccountService.depositOrWithdraw(uidLong, dto);
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
            Long uidLong = Long.parseLong(uid);
            BankAccountDTO account = bankAccountService.updateAccount(uidLong, accountId, accountType);
            return ResponseUtils.handlerSuccess(account);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    /**
     * Khóa/Mở khóa tài khoản
     */
    @PutMapping("/{accountId}/status")
    public ResponseEntity<?> updateAccountStatus(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long accountId,
            @RequestParam String status) {
        try {
            Long uidLong = Long.parseLong(uid);
            BankAccountDTO account = bankAccountService.updateAccountStatus(uidLong, accountId, status);
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
            Long uidLong = Long.parseLong(uid);
            AccountStatisticsDTO stats = bankAccountService.getAccountStatistics(uidLong);
            return ResponseUtils.handlerSuccess(stats);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
