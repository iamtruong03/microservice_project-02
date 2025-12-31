package com.example.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateUserRequest {
  
  // Basic Information
  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name must not exceed 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name must not exceed 50 characters")
  private String lastName;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String userName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 150, message = "Email must not exceed 150 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
  private String password;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
  private String phoneNumber;

  // Personal Information
  @NotNull(message = "Date of birth is required")
  @Past(message = "Date of birth must be in the past")
  private LocalDate dateOfBirth;

  @NotBlank(message = "Gender is required")
  @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
  private String gender;

  @NotBlank(message = "National ID is required")
  @Size(min = 9, max = 12, message = "National ID must be between 9 and 12 characters")
  private String nationalId;

  // Address Information
  @NotBlank(message = "Address is required")
  @Size(max = 255, message = "Address must not exceed 255 characters")
  private String address;

  @NotBlank(message = "City is required")
  @Size(max = 100, message = "City must not exceed 100 characters")
  private String city;

  @NotBlank(message = "State is required")
  @Size(max = 100, message = "State must not exceed 100 characters")
  private String state;

  @NotBlank(message = "Postal code is required")
  @Pattern(regexp = "^\\d{5,10}$", message = "Postal code should be valid")
  private String postalCode;

  @NotBlank(message = "Country is required")
  @Size(max = 100, message = "Country must not exceed 100 characters")
  private String country;

  // Employment Information (optional)
  @Size(max = 100, message = "Occupation must not exceed 100 characters")
  private String occupation;

  @Size(max = 150, message = "Employer name must not exceed 150 characters")
  private String employerName;

  @DecimalMin(value = "0.0", inclusive = true, message = "Monthly income must be non-negative")
  @Digits(integer = 10, fraction = 2, message = "Monthly income must be a valid amount")
  private BigDecimal monthlyIncome;

  // Banking Preferences (optional)
  private String preferredLanguage = "en";
  private String preferredCurrency = "USD";
  private Boolean notificationEnabled = true;

  // Role (optional - default null)
  private Long roleId;

  // Emergency Contact (optional)
  @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
  private String emergencyContactName;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Emergency contact phone should be valid")
  private String emergencyContactPhone;

  @Size(max = 100, message = "Emergency contact relationship must not exceed 100 characters")
  private String emergencyContactRelationship;
}


