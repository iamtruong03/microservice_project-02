package com.example.authservice.client;

import com.example.authservice.dto.AuthenticationRequest;
import com.example.authservice.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.user-service.url:http://localhost:8082}")
    private String userServiceUrl;

    public UserDetail authenticate(String userName, String password) {
        try {
            String url = userServiceUrl + "/api/users/authenticate";
            AuthenticationRequest request = AuthenticationRequest.builder()
                    .userName(userName)
                    .password(password)
                    .build();

            log.info("Calling User-Service for authentication: {}", url);
            UserDetail userDetail = restTemplate.postForObject(url, request, UserDetail.class);
            log.info("User authenticated successfully: {}", userDetail.getUserName());
            return userDetail;
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Authentication failed for user: {} - Invalid credentials or user not found", userName);
            throw new RuntimeException("Invalid username or password", e);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User not found: {}", userName);
            throw new RuntimeException("User not found: " + userName, e);
        } catch (HttpClientErrorException e) {
            log.error("HTTP Client error during authentication - status: {}, message: {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("HTTP Server error in User-Service - status: {}, message: {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("User-Service error: " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            log.error("Failed to connect to User-Service: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to connect to User-Service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during authentication - username: {}, error: {}", userName, e.getMessage(), e);
            throw new RuntimeException("Unexpected error during authentication: " + e.getMessage(), e);
        }
    }
}
