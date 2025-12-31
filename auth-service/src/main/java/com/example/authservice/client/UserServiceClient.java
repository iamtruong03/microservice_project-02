package com.example.authservice.client;

import com.example.authservice.dto.ApiResponse;
import com.example.authservice.dto.AuthenticationRequest;
import com.example.authservice.dto.UserDetail;
import com.example.authservice.exception.AccountInactiveException;
import com.example.authservice.exception.AccountLockedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

            
            // Use ParameterizedTypeReference to properly parse ApiResponse<UserDetail>
            ResponseEntity<ApiResponse<UserDetail>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<UserDetail>>() {}
            );
            
            ApiResponse<UserDetail> apiResponse = response.getBody();
            if (apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null) {
                UserDetail userDetail = apiResponse.getData();
                return userDetail;
            } else {
                throw new RuntimeException("Invalid response from User-Service");
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RuntimeException("Invalid username or password", e);
        } catch (HttpClientErrorException.Forbidden e) {
            // Parse the response body to extract the error message
            String responseBody = e.getResponseBodyAsString();
            if (responseBody.contains("inactive")) {
                throw new AccountInactiveException("User account is inactive");
            } else if (responseBody.contains("locked")) {
                throw new AccountLockedException("User account is locked");
            } else {
                throw new RuntimeException("Access forbidden: " + responseBody, e);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("User not found: " + userName, e);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Authentication failed: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("User-Service error: " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Unable to connect to User-Service: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during authentication: " + e.getMessage(), e);
        }
    }
}
