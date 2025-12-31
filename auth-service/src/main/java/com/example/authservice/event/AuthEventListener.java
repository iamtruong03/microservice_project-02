package com.example.authservice.event;

import com.example.authservice.domain.User;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthEventListener {

    private final UserRepository userRepository;

    /**
     * Lắng nghe event "user:profile_updated" từ User Service
     * Khi user cập nhật email hoặc thông tin, Auth Service được thông báo
     */
    // @KafkaListener(topics = "user-events", groupId = "auth-service-group") // Tắt Kafka
    public void handleUserProfileUpdatedEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            if (!"USER_PROFILE_UPDATED".equals(eventType)) {
                return;
            }

            log.info("Received USER_PROFILE_UPDATED event in Auth Service: {}", event);

            Long userId = ((Number) event.get("userId")).longValue();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Cập nhật email nếu được thay đổi
            if (event.containsKey("email")) {
                String newEmail = (String) event.get("email");
                if (!newEmail.equals(user.getEmail())) {
                    // Kiểm tra email mới có trùng lặp không
                    if (!userRepository.existsByEmail(newEmail)) {
                        user.setEmail(newEmail);
                    }
                }
            }

            // Cập nhật fullName nếu được thay đổi
            if (event.containsKey("fullName")) {
                user.setFullName((String) event.get("fullName"));
            }

            user.setUpdatedAt(LocalDateTime.now().toString());
            userRepository.save(user);

            log.info("Successfully synced user profile update in Auth Service: {}", userId);

        } catch (Exception e) {
            log.error("Error processing USER_PROFILE_UPDATED event in Auth Service", e);
        }
    }

    /**
     * Lắng nghe event "user:password_changed" từ User Service
     * Khi user đổi mật khẩu ở User Service, cập nhật lại ở Auth Service
     */
    // @KafkaListener(topics = "user-events", groupId = "auth-service-group") // Tắt Kafka
    public void handleUserPasswordChangedEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            if (!"USER_PASSWORD_CHANGED".equals(eventType)) {
                return;
            }

            log.info("Received USER_PASSWORD_CHANGED event in Auth Service: {}", event);

            Long userId = ((Number) event.get("userId")).longValue();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Cập nhật password (đã mã hóa)
            if (event.containsKey("hashedPassword")) {
                String hashedPassword = (String) event.get("hashedPassword");
                user.setPassword(hashedPassword);
                user.setUpdatedAt(LocalDateTime.now().toString());
                userRepository.save(user);

                log.info("Successfully updated password for user: {}", userId);
            }

        } catch (Exception e) {
            log.error("Error processing USER_PASSWORD_CHANGED event in Auth Service", e);
        }
    }
}
