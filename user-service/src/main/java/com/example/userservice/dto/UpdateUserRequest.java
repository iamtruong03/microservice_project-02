package com.example.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {
  
  // Basic Information
  @Size(max = 50, message = "First name must not exceed 50 characters")
  private String firstName;

  @Size(max = 50, message = "Last name must not exceed 50 characters")
  private String lastName;

  @Email(message = "Email should be valid")
  @Size(max = 150, message = "Email must not exceed 150 characters")
  private String email;

  private String phoneNumber;

  // Personal Information
  @Past(message = "Date of birth must be in the past")
  private LocalDate dateOfBirth;

  private String gender;

  private String nationalId;

  // Address Information
  @Size(max = 255, message = "Address must not exceed 255 characters")
  private String address;

  @Size(max = 100, message = "City must not exceed 100 characters")
  private String city;

  @Size(max = 100, message = "State must not exceed 100 characters")
  private String state;

  @Pattern(regexp = "^\\d{5,10}$", message = "Postal code should be valid")
  private String postalCode;

  @Size(max = 100, message = "Country must not exceed 100 characters")
  private String country;

  // Employment Information
  @Size(max = 100, message = "Occupation must not exceed 100 characters")
  private String occupation;

  @Size(max = 150, message = "Employer name must not exceed 150 characters")
  private String employerName;

  @DecimalMin(value = "0.0", inclusive = true, message = "Monthly income must be non-negative")
  @Digits(integer = 10, fraction = 2, message = "Monthly income must be a valid amount")
  private BigDecimal monthlyIncome;

  // Banking Preferences
  private String preferredLanguage;
  private String preferredCurrency;
  private Boolean notificationEnabled;

  // Emergency Contact
  @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
  private String emergencyContactName;

  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Emergency contact phone should be valid")
  private String emergencyContactPhone;

  @Size(max = 100, message = "Emergency contact relationship must not exceed 100 characters")
  private String emergencyContactRelationship;
}


