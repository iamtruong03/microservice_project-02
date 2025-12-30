package com.example.transactionservice.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtils {

    public static ResponseEntity<?> handlerSuccess(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<?> handlerCreated(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static ResponseEntity<?> handlerException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", e.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
