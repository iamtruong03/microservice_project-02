package com.example.authservice.client;

import com.example.authservice.dto.AuthenticationRequest;
import com.example.authservice.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.user-service.url:http://localhost:8083}")
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
        } catch (RestClientException e) {
            log.error("Failed to authenticate user from User-Service: {}", userName, e);
            throw new RuntimeException("Unable to authenticate user. Service unavailable.");
        }
    }
}
