package com.example.accountservice.service;

import com.example.accountservice.dto.*;
import com.example.accountservice.exception.ResourceNotFoundException;
import com.example.accountservice.model.BankAccount;
import com.example.accountservice.model.Transaction;
import com.example.accountservice.repository.BankAccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Tạo tài khoản ngân hàng sau khi đăng ký
     */
    @Transactional
    public BankAccountDTO createBankAccount(Long userId, String accountType) {
        // Kiểm tra xem user đã có tài khoản hay chưa
        if (bankAccountRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User already has a bank account");
        }

        BankAccount bankAccount = new BankAccount();
        bankAccount.setUserId(userId);
        bankAccount.setAccountNumber(generateAccountNumber());
        bankAccount.setAccountType(accountType != null ? accountType : "SAVINGS");
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setStatus("ACTIVE");

        BankAccount saved = bankAccountRepository.save(bankAccount);
        return convertToDTO(saved);
    }

    /**
     * Xem số dư tài khoản
     */
    public BankAccountDTO getAccountBalance(Long uid, Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Kiểm tra quyền truy cập
        if (!account.getUserId().equals(uid)) {
            throw new RuntimeException("Access denied");
        }

        return convertToDTO(account);
    }

    /**
     * Xem tất cả tài khoản của user
     */
    public List<BankAccountDTO> getUserAccounts(Long uid) {
        return bankAccountRepository.findAllByUserId(uid)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển tiền nội bộ giữa các tài khoản
     */
    @Transactional
    public TransactionDTO transferMoney(Long uid, TransferDTO transferDTO) {
        BankAccount fromAccount = bankAccountRepository.findById(transferDTO.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("From account not found"));

        BankAccount toAccount = bankAccountRepository.findById(transferDTO.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("To account not found"));

        // Kiểm tra quyền truy cập
        if (!fromAccount.getUserId().equals(uid)) {
            throw new RuntimeException("Access denied");
        }

        // Kiểm tra số dư
        if (fromAccount.getBalance().compareTo(transferDTO.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Thực hiện chuyển tiền
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferDTO.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transferDTO.getAmount()));

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        // Tạo transaction record
        Transaction transaction = new Transaction();
        transaction.setCustomerId(uid);
        transaction.setAmount(transferDTO.getAmount());
        transaction.setTransactionType("TRANSFER");
        transaction.setStatus("COMPLETED");
        transaction.setDescription(transferDTO.getDescription() != null ? 
                transferDTO.getDescription() : "Transfer between accounts");

        Transaction saved = transactionRepository.save(transaction);
        return convertTransactionToDTO(saved);
    }

    /**
     * Rút tiền / Nạp tiền
     */
    @Transactional
    public TransactionDTO depositOrWithdraw(Long uid, DepositWithdrawDTO dto) {
        BankAccount account = bankAccountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Kiểm tra quyền truy cập
        if (!account.getUserId().equals(uid)) {
            throw new RuntimeException("Access denied");
        }

        // Kiểm tra loại giao dịch
        if (!"DEPOSIT".equals(dto.getType()) && !"WITHDRAW".equals(dto.getType())) {
            throw new RuntimeException("Invalid transaction type");
        }

        // Kiểm tra số dư khi rút tiền
        if ("WITHDRAW".equals(dto.getType()) && 
            account.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Thực hiện giao dịch
        if ("DEPOSIT".equals(dto.getType())) {
            account.setBalance(account.getBalance().add(dto.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(dto.getAmount()));
        }

        bankAccountRepository.save(account);

        // Tạo transaction record
        Transaction transaction = new Transaction();
        transaction.setCustomerId(uid);
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionType(dto.getType());
        transaction.setStatus("COMPLETED");
        transaction.setDescription(dto.getDescription() != null ? 
                dto.getDescription() : dto.getType().toLowerCase());

        Transaction saved = transactionRepository.save(transaction);
        return convertTransactionToDTO(saved);
    }

    /**
     * Lịch sử giao dịch
     */
    public List<TransactionDTO> getTransactionHistory(Long uid) {
        return transactionRepository.findByCustomerId(uid)
                .stream()
                .map(this::convertTransactionToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Thống kê tổng số dư và Transaction
     */
    public AccountStatisticsDTO getAccountStatistics(Long uid) {
        List<BankAccount> accounts = bankAccountRepository.findAllByUserId(uid);
        List<Transaction> transactions = transactionRepository.findByCustomerId(uid);

        BigDecimal totalBalance = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AccountStatisticsDTO(
                uid,
                totalBalance,
                (long) accounts.size(),
                (long) transactions.size()
        );
    }

    /**
     * Helper: Convert BankAccount to DTO
     */
    private BankAccountDTO convertToDTO(BankAccount account) {
        return new BankAccountDTO(
                account.getId(),
                account.getUserId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    /**
     * Helper: Convert Transaction to DTO
     */
    private TransactionDTO convertTransactionToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getOrderId(),
                transaction.getCustomerId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    /**
     * Helper: Generate unique account number
     */
    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
