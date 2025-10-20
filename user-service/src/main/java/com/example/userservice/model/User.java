package com.example.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name must not exceed 50 characters")
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name must not exceed 50 characters")
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 150, message = "Email must not exceed 150 characters")
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  // Personal Information
  @NotNull(message = "Date of birth is required")
  @Past(message = "Date of birth must be in the past")
  @Column(name = "date_of_birth", nullable = false)
  private LocalDate dateOfBirth;

  @NotBlank(message = "Gender is required")
  @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
  @Column(name = "gender", nullable = false)
  private String gender;

  @NotBlank(message = "National ID is required")
  @Size(min = 9, max = 12, message = "National ID must be between 9 and 12 characters")
  @Column(name = "national_id", nullable = false, unique = true)
  private String nationalId;

  // Address Information
  @NotBlank(message = "Address is required")
  @Size(max = 255, message = "Address must not exceed 255 characters")
  @Column(name = "address", nullable = false)
  private String address;

  @NotBlank(message = "City is required")
  @Size(max = 100, message = "City must not exceed 100 characters")
  @Column(name = "city", nullable = false)
  private String city;

  @NotBlank(message = "State is required")
  @Size(max = 100, message = "State must not exceed 100 characters")
  @Column(name = "state", nullable = false)
  private String state;

  @NotBlank(message = "Postal code is required")
  @Pattern(regexp = "^\\d{5,10}$", message = "Postal code should be valid")
  @Column(name = "postal_code", nullable = false)
  private String postalCode;

  @NotBlank(message = "Country is required")
  @Size(max = 100, message = "Country must not exceed 100 characters")
  @Column(name = "country", nullable = false)
  private String country;

  // Employment Information
  @Size(max = 100, message = "Occupation must not exceed 100 characters")
  @Column(name = "occupation")
  private String occupation;

  @Size(max = 150, message = "Employer name must not exceed 150 characters")
  @Column(name = "employer_name")
  private String employerName;

  @DecimalMin(value = "0.0", inclusive = true, message = "Monthly income must be non-negative")
  @Digits(integer = 10, fraction = 2, message = "Monthly income must be a valid amount")
  @Column(name = "monthly_income", precision = 12, scale = 2)
  private BigDecimal monthlyIncome;

  // Account Status
  @NotNull(message = "Account status is required")
  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Builder.Default
  @Column(name = "is_verified", nullable = false)
  private Boolean isVerified = false;

  @Builder.Default
  @Column(name = "is_locked", nullable = false)
  private Boolean isLocked = false;

  // KYC Information
  @Pattern(regexp = "^(PENDING|IN_PROGRESS|APPROVED|REJECTED)$", message = "KYC status must be PENDING, IN_PROGRESS, APPROVED, or REJECTED")
  @Builder.Default
  @Column(name = "kyc_status", nullable = false)
  private String kycStatus = "PENDING";

  @Column(name = "kyc_completed_date")
  private LocalDateTime kycCompletedDate;

  // Risk Assessment
  @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Risk level must be LOW, MEDIUM, or HIGH")
  @Builder.Default
  @Column(name = "risk_level", nullable = false)
  private String riskLevel = "MEDIUM";

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
  @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
  @Column(name = "emergency_contact_name")
  private String emergencyContactName;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Emergency contact phone should be valid")
  @Column(name = "emergency_contact_phone")
  private String emergencyContactPhone;

  @Size(max = 100, message = "Emergency contact relationship must not exceed 100 characters")
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

  @Size(max = 45, message = "IP address must not exceed 45 characters")
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


