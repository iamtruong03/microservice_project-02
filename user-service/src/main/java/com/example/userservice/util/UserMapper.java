package com.example.userservice.util;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .nationalId(request.getNationalId())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .occupation(request.getOccupation())
                .employerName(request.getEmployerName())
                .monthlyIncome(request.getMonthlyIncome())
                .isActive(true)
                .isVerified(false)
                .isLocked(false)
                .roleId(request.getRoleId())
                .kycStatus("PENDING")
                .preferredLanguage(getOrDefault(request.getPreferredLanguage(), "en"))
                .preferredCurrency(getOrDefault(request.getPreferredCurrency(), "USD"))
                .notificationEnabled(getOrDefault(request.getNotificationEnabled(), true))
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelationship(request.getEmergencyContactRelationship())
                .build();
    }

    public void updateEntity(User existingUser, UpdateUserRequest request) {
        if (request.getFirstName() != null) {
            existingUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existingUser.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            existingUser.setGender(request.getGender());
        }
        if (request.getNationalId() != null) {
            existingUser.setNationalId(request.getNationalId());
        }
        if (request.getAddress() != null) {
            existingUser.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            existingUser.setCity(request.getCity());
        }
        if (request.getState() != null) {
            existingUser.setState(request.getState());
        }
        if (request.getPostalCode() != null) {
            existingUser.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            existingUser.setCountry(request.getCountry());
        }
        if (request.getOccupation() != null) {
            existingUser.setOccupation(request.getOccupation());
        }
        if (request.getEmployerName() != null) {
            existingUser.setEmployerName(request.getEmployerName());
        }
        if (request.getMonthlyIncome() != null) {
            existingUser.setMonthlyIncome(request.getMonthlyIncome());
        }
        if (request.getPreferredLanguage() != null) {
            existingUser.setPreferredLanguage(request.getPreferredLanguage());
        }
        if (request.getPreferredCurrency() != null) {
            existingUser.setPreferredCurrency(request.getPreferredCurrency());
        }
        if (request.getNotificationEnabled() != null) {
            existingUser.setNotificationEnabled(request.getNotificationEnabled());
        }
        if (request.getEmergencyContactName() != null) {
            existingUser.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            existingUser.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }
        if (request.getEmergencyContactRelationship() != null) {
            existingUser.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        }
    }

    private <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
