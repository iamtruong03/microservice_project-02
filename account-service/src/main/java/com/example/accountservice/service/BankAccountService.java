package com.example.accountservice.service;

import com.example.accountservice.dto.AccountStatisticsDTO;
import com.example.accountservice.dto.BankAccountDTO;
import com.example.accountservice.dto.DepositWithdrawDTO;
import com.example.accountservice.exception.ResourceNotFoundException;
import com.example.accountservice.model.BankAccount;
import com.example.accountservice.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
// import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    // private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Tạo tài khoản ngân hàng sau khi đăng ký
     */
    @Transactional
    public BankAccountDTO createBankAccount(Long userId) {

        BankAccount bankAccount = new BankAccount();
        bankAccount.setUserId(userId);
        bankAccount.setAccountNumber(generateAccountNumber());
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setIsActive(true);

        BankAccount saved = bankAccountRepository.save(bankAccount);
        // Publish account created event
        // kafkaTemplate.send("account-events", "account_created", saved);
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
    public BankAccountDTO transferBetweenAccounts(Long uid, Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        BankAccount fromAccount = bankAccountRepository.findById(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("From account not found"));

        BankAccount toAccount = bankAccountRepository.findById(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("To account not found"));

        // Kiểm tra quyền truy cập
        if (!fromAccount.getUserId().equals(uid)) {
            throw new RuntimeException("Access denied");
        }

        // Kiểm tra số dư
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Thực hiện chuyển tiền
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        bankAccountRepository.save(fromAccount);
        BankAccount updatedToAccount = bankAccountRepository.save(toAccount);

        // Publish account updated events
        // kafkaTemplate.send("account-events", "account_updated", fromAccount);
        // kafkaTemplate.send("account-events", "account_updated", toAccount);

        return convertToDTO(updatedToAccount);
    }

    /**
     * Rút tiền / Nạp tiền
     */
    @Transactional
    public BankAccountDTO depositOrWithdraw(Long uid, DepositWithdrawDTO dto) {
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

        BankAccount updated = bankAccountRepository.save(account);
        
        // Publish account updated event
        // kafkaTemplate.send("account-events", "account_updated", updated);

        return convertToDTO(updated);
    }

    /**
     * Cập nhật thông tin tài khoản
     */
    @Transactional
    public BankAccountDTO updateAccount(Long uid, Long accountId, String accountType) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Kiểm tra quyền truy cập
        if (!account.getUserId().equals(uid)) {
            throw new RuntimeException("Access denied");
        }

        BankAccount updated = bankAccountRepository.save(account);
        
        // Publish account updated event
        // kafkaTemplate.send("account-events", "account_updated", updated);

        return convertToDTO(updated);
    }

    /**
     * Khóa/Mở khóa tài khoản
     */
    @Transactional
    public BankAccountDTO updateAccountStatus(Long uid, Long accountId, String status) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Kiểm tra quyền truy cập
        if (!account.getUserId().equals(uid)) {
            throw new RuntimeException("Access denied");
        }

        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status) && !"CLOSED".equals(status)) {
            throw new RuntimeException("Invalid account status");
        }

        // Nếu status là "ACTIVE" thì setIsActive(true), ngược lại setIsActive(false)
        account.setIsActive("ACTIVE".equalsIgnoreCase(status));
        BankAccount updated = bankAccountRepository.save(account);
        // Publish account updated event
        // kafkaTemplate.send("account-events", "account_updated", updated);
        return convertToDTO(updated);
    }

    /**
     * Thống kê tài khoản
     */
    public AccountStatisticsDTO getAccountStatistics(Long uid) {
        List<BankAccount> accounts = bankAccountRepository.findAllByUserId(uid);

        BigDecimal totalBalance = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AccountStatisticsDTO(
                uid,
                totalBalance,
                (long) accounts.size(),
                0L // Transaction count moved to transaction-service
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
            account.getAccountTypeId(),
            account.getBalance(),
            (account.getIsActive() != null && account.getIsActive()) ? "ACTIVE" : "INACTIVE",
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }

    /**
     * Helper: Generate unique account number
     */
    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
