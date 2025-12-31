package com.example.authservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetail {
    private Long id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean isVerified;
    private Boolean isActive;
    private Boolean isAdmin;
    private Boolean isLocked;
    private String kycStatus;
}
