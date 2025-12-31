package com.example.authservice.security;

import com.example.authservice.client.UserServiceClient;
import com.example.authservice.domain.User;
import com.example.authservice.dto.UserDetail;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            
            
            Optional<User> existingUser = userRepository.findByEmail(email);
            
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                user.setFullName(name);
                user.setUpdatedAt(LocalDateTime.now().toString());
                userRepository.save(user);
            } else {
                user = new User();
                user.setEmail(email);
                user.setUsername(email.split("@")[0]);
                user.setFullName(name);
                user.setEnabled(true);
                user.setCreatedAt(LocalDateTime.now().toString());
                user.setUpdatedAt(LocalDateTime.now().toString());
                userRepository.save(user);
            }
            
            String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
            String redirectUrl = "http://localhost:3000/login?token=" + token + "&email=" + email;
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            log.error("Error during OAuth2 authentication: ", e);
            response.sendRedirect("http://localhost:3000/login?error=authentication_failed");
        }
    }
}
