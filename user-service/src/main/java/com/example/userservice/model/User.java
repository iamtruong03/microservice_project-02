package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Basic Information
  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "user_name", nullable = false, unique = true)
  private String userName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "phone_number")
  private String phoneNumber;

  // Personal Information
  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Column(name = "gender")
  private String gender;

  @Column(name = "national_id", unique = true)
  private String nationalId;

  // Address Information
  @Column(name = "address")
  private String address;

  @Column(name = "city")
  private String city;

  @Column(name = "state")
  private String state;

  @Column(name = "postal_code")
  private String postalCode;

  @Column(name = "country")
  private String country;

  // Employment Information
  @Column(name = "occupation")
  private String occupation;

  @Column(name = "employer_name")
  private String employerName;

  @Column(name = "monthly_income", precision = 12, scale = 2)
  private BigDecimal monthlyIncome;

  // Account Status
  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Builder.Default
  @Column(name = "is_verified", nullable = false)
  private Boolean isVerified = false;

  @Builder.Default
  @Column(name = "is_locked", nullable = false)
  private Boolean isLocked = false;

  // Roles (distributed architecture - store role ID instead of relationship)
  @Column(name = "role_id")
  private Long roleId;

  // KYC Information
  @Builder.Default
  @Column(name = "kyc_status", nullable = false)
  private String kycStatus = "PENDING";

  @Column(name = "kyc_completed_date")
  private LocalDateTime kycCompletedDate;

  // Banking Preferences
  @Builder.Default
  @Column(name = "preferred_language", nullable = false)
  private String preferredLanguage = "en";

  @Builder.Default
  @Column(name = "preferred_currency", nullable = false)
  private String preferredCurrency = "USD";

  @Builder.Default
  @Column(name = "notification_enabled", nullable = false)
  private Boolean notificationEnabled = true;

  // Emergency Contact
  @Column(name = "emergency_contact_name")
  private String emergencyContactName;

  @Column(name = "emergency_contact_phone")
  private String emergencyContactPhone;

  @Column(name = "emergency_contact_relationship")
  private String emergencyContactRelationship;

  // Audit Fields
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "last_login_ip")
  private String lastLoginIp;

  // Helper methods
  public String getFullName() {
    return firstName + " " + lastName;
  }

  public boolean isAdult() {
    return dateOfBirth != null && 
           dateOfBirth.isBefore(LocalDate.now().minusYears(18));
  }

  public boolean isKycCompleted() {
    return "APPROVED".equals(kycStatus);
  }

  public boolean canPerformTransactions() {
    return isActive && !isLocked && isVerified && isKycCompleted();
  }
}


