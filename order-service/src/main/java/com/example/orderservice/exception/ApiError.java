package com.example.orderservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;
    private String path;
}
