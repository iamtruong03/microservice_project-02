package com.example.userservice.event;

import com.example.userservice.model.User;
import com.example.userservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepository userRepository;

    /**
     * Lắng nghe event "user:registered" từ Auth Service
     * Khi user đăng ký qua Auth Service, event được phát tới Kafka
     * User Service sẽ nhận và tạo user profile chi tiết
     */
    @KafkaListener(topics = "user-events", groupId = "user-service-group")
    public void handleUserRegisteredEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            if (!"USER_REGISTERED".equals(eventType)) {
                return;
            }

            log.info("Received USER_REGISTERED event: {}", event);

            Long userId = ((Number) event.get("userId")).longValue();
            String username = (String) event.get("username");
            String email = (String) event.get("email");
            String fullName = (String) event.get("fullName");

            // Kiểm tra user đã tồn tại chưa
            if (userRepository.existsById(userId)) {
                log.warn("User {} already exists in User Service", userId);
                return;
            }

            // Tạo user profile chi tiết từ dữ liệu cơ bản
            User user = User.builder()
                    .id(userId)  // Sử dụng cùng ID từ Auth Service
                    .firstName(extractFirstName(fullName))
                    .lastName(extractLastName(fullName))
                    .email(email)
                    .password("")  // Mật khẩu đã được mã hóa ở Auth Service
                    .phoneNumber("")  // Sẽ được cập nhật sau
                    .dateOfBirth(LocalDate.now().minusYears(18))  // Default
                    .gender("OTHER")
                    .nationalId("")
                    .address("")
                    .city("")
                    .state("")
                    .postalCode("")
                    .country("")
                    .occupation("")
                    .employerName("")
                    .monthlyIncome(BigDecimal.ZERO)
                    .isActive(true)
                    .isVerified(false)
                    .isLocked(false)
                    .kycStatus("PENDING")
                    .riskLevel("MEDIUM")
                    .preferredLanguage("en")
                    .preferredCurrency("USD")
                    .notificationEnabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            log.info("Successfully created user profile for user: {}", username);

        } catch (Exception e) {
            log.error("Error processing USER_REGISTERED event", e);
        }
    }

    /**
     * Lắng nghe event "user:updated" từ User Service hoặc Auth Service
     * Khi có cập nhật thông tin, các service khác sẽ được thông báo
     */
    @KafkaListener(topics = "user-events", groupId = "user-service-group")
    public void handleUserUpdatedEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            if (!"USER_UPDATED".equals(eventType)) {
                return;
            }

            log.info("Received USER_UPDATED event: {}", event);

            Long userId = ((Number) event.get("userId")).longValue();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Cập nhật thông tin nếu có
            if (event.containsKey("email")) {
                user.setEmail((String) event.get("email"));
            }
            if (event.containsKey("fullName")) {
                String fullName = (String) event.get("fullName");
                user.setFirstName(extractFirstName(fullName));
                user.setLastName(extractLastName(fullName));
            }

            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Successfully updated user: {}", userId);

        } catch (Exception e) {
            log.error("Error processing USER_UPDATED event", e);
        }
    }

    /**
     * Lắng nghe event "user:deleted" từ Auth Service
     * Khi user bị xóa, cập nhật trạng thái trong User Service
     */
    @KafkaListener(topics = "user-events", groupId = "user-service-group")
    public void handleUserDeletedEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            if (!"USER_DELETED".equals(eventType)) {
                return;
            }

            log.info("Received USER_DELETED event: {}", event);

            Long userId = ((Number) event.get("userId")).longValue();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Không xóa vĩnh viễn, chỉ vô hiệu hóa tài khoản
            user.setIsActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Successfully deactivated user: {}", userId);

        } catch (Exception e) {
            log.error("Error processing USER_DELETED event", e);
        }
    }

    // Helper method để tách first name từ full name
    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "User";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    // Helper method để tách last name từ full name
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Profile";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        }
        return "";
    }
}
