package com.example.accountservice.service;

import com.example.accountservice.dto.AccountStatisticsDTO;
import com.example.accountservice.dto.BankAccountDTO;
import com.example.accountservice.exception.AccessDeniedException;
import com.example.accountservice.exception.ResourceNotFoundException;
import com.example.accountservice.model.BankAccount;
import com.example.accountservice.repository.BankAccountRepository;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
// import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountService {

  private final BankAccountRepository bankAccountRepository;
  // private final KafkaTemplate<String, Object> kafkaTemplate;

  @Transactional
  public BankAccountDTO createBankAccount(String userId, BankAccountDTO dto) {

    BankAccount bankAccount = new BankAccount();
    bankAccount.setUserId(Long.valueOf(userId));
    bankAccount.setCreatedBy(userId);
    bankAccount.setAccountTypeId(dto.getAccountTypeId());

    String stk = dto.getAccountNumber();

    if (stk != null && !stk.isEmpty()) {
      if (bankAccountRepository.existsBankAccountByAccountNumber(stk)) {
        throw new IllegalArgumentException("Account number already exists: " + stk);
      }
      bankAccount.setAccountNumber(stk);
    } else {
      bankAccount.setAccountNumber(generateAccountNumber());
    }

    BankAccount saved = bankAccountRepository.save(bankAccount);

    // kafkaTemplate.send("account-events", "account_created", saved);

    return convertToDTO(saved);
  }

  /**
   * Xem số dư tài khoản
   */
  public BankAccountDTO getAccountBalance(String uid, Long accountId) {
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
  public List<BankAccountDTO> getUserAccounts(String uid) {
    return bankAccountRepository.findAllByUserIdAndIsActiveTrue(Long.valueOf(uid))
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  /**
   * Cập nhật thông tin tài khoản
   */
  @Transactional
  public BankAccountDTO updateAccount(String uid, BankAccountDTO dto) {
    BankAccount account = bankAccountRepository.findById(dto.getId())
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
   * khóa tài khoản
   */
  @Transactional
  public BankAccountDTO unActiveAccount(String uid, Long accountId) {
    BankAccount account = bankAccountRepository.findById(accountId)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    Long userId = parseUserId(uid);

    if (!account.getUserId().equals(userId)) {
      throw new AccessDeniedException("Access denied");
    }

    if (Boolean.FALSE.equals(account.getIsActive())) {
      throw new IllegalStateException("Account already inactive");
    }

    account.setIsActive(false);

    return convertToDTO(bankAccountRepository.save(account));
  }

  @Transactional
  public BankAccountDTO activeAccount(String uid, Long accountId) {
    BankAccount account = bankAccountRepository.findById(accountId)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    Long userId = parseUserId(uid);

    if (!account.getUserId().equals(userId)) {
      throw new AccessDeniedException("Access denied");
    }

    if (Boolean.TRUE.equals(account.getIsActive())) {
      throw new IllegalStateException("Account already active");
    }

    account.setIsActive(true);

    return convertToDTO(bankAccountRepository.save(account));
  }

  /**
   * Thống kê tài khoản
   */
  public AccountStatisticsDTO getAccountStatistics(String uid) {
    List<BankAccount> accounts = bankAccountRepository.findAllByUserIdAndIsActiveTrue(
        Long.valueOf(uid));

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
        account.getIsActive(),
        account.getCreatedAt(),
        account.getUpdatedAt()
    );
  }

  /**
   * Helper: Generate unique account number
   */
  private String generateAccountNumber() {
    StringBuilder sb = new StringBuilder();
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int i = 0; i < 12; i++) {
      sb.append(random.nextInt(0, 10));
    }
    return sb.toString();
  }

  private Long parseUserId(String uid) {
    try {
      return Long.valueOf(uid);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid user id");
    }
  }


}
