package com.example.userservice.service.impl;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.exception.DuplicateEmailException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.model.Role;
import com.example.userservice.model.Permission;
import com.example.userservice.repo.UserRepository;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.service.IUserService;
import com.example.userservice.util.UserMapper;
import com.example.userservice.util.UserQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserQueryBuilder queryBuilder;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<User> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        validateUniqueFields(request);
        User user = userMapper.toEntity(request);
        // Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("Created user with id: {}", savedUser.getId());
        
        // Publish user created event
        publishUserCreatedEvent(savedUser);
        
        return savedUser;
    }

    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User existingUser = getUserById(id);
        validateUniqueFieldsForUpdate(request, id);
        
        // Lưu email cũ để kiểm tra thay đổi
        String oldEmail = existingUser.getEmail();
        
        userMapper.updateEntity(existingUser, request);
        User updatedUser = userRepository.save(existingUser);
        log.info("Updated user with id: {}", updatedUser.getId());

        // 📤 Phát event nếu email hoặc fullName thay đổi
        publishUserProfileUpdatedEvent(updatedUser, oldEmail);

        return updatedUser;
    }

    // 📤 Phát event khi user profile được cập nhật
    private void publishUserProfileUpdatedEvent(User user, String oldEmail) {
        try {
            // Chỉ phát event nếu email hoặc fullName thay đổi
            if (!oldEmail.equals(user.getEmail()) || true) {
                Map<String, Object> event = new HashMap<>();
                event.put("eventType", "USER_PROFILE_UPDATED");
                event.put("userId", user.getId());
                event.put("email", user.getEmail());
                event.put("fullName", user.getFullName());
                event.put("timestamp", System.currentTimeMillis());

                kafkaTemplate.send("user-events", "user_profile_updated", event);
                log.info("Published USER_PROFILE_UPDATED event for user: {}", user.getId());
            }
        } catch (Exception e) {
            log.error("Failed to publish user profile updated event", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<User> searchUsers(String keyword, Pageable pageable) {
        if (isBlankString(keyword)) {
            return getAllUsers(pageable);
        }
        
        Page<User> page = userRepository.findByKeyword(keyword, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<User> searchByName(String name, Pageable pageable) {
        Page<User> page = userRepository.findByNameContainingIgnoreCase(name, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<User> searchByEmail(String email, Pageable pageable) {
        Page<User> page = userRepository.findByEmailContainingIgnoreCase(email, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<User> advancedSearch(String name, String email, String city, 
                                           String occupation, Pageable pageable) {
        if (isEmptySearch(name, email, city, occupation)) {
            return getAllUsers(pageable);
        }
        
        Specification<User> spec = queryBuilder.buildSearchSpecification(name, email, city, occupation);
        Page<User> page = userRepository.findAll(spec, pageable);
        return buildPageResponse(page);
    }

    private void validateUniqueFields(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại: " + request.getEmail());
        }
        if (request.getNationalId() != null && userRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateEmailException("National ID đã tồn tại: " + request.getNationalId());
        }
    }

    private void validateUniqueFieldsForUpdate(UpdateUserRequest request, Long userId) {
        if (request.getEmail() != null && userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new DuplicateEmailException("Email đã tồn tại: " + request.getEmail());
        }
        if (request.getNationalId() != null && userRepository.existsByNationalIdAndIdNot(request.getNationalId(), userId)) {
            throw new DuplicateEmailException("National ID đã tồn tại: " + request.getNationalId());
        }
    }

    private boolean isBlankString(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isEmptySearch(String... params) {
        for (String param : params) {
            if (!isBlankString(param)) {
                return false;
            }
        }
        return true;
    }

    private PageResponse<User> buildPageResponse(Page<User> page) {
        return PageResponse.<User>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        User user = getUserById(userId);
        user.setRoleId(roleId);
        User updated = userRepository.save(user);
        log.info("Assigned role {} to user {}", roleId, userId);
        
        // Publish role assigned event
        publishUserRoleUpdatedEvent(updated);
        
        return updated;
    }

    // ========== Kafka Events ==========
    
    private void publishUserCreatedEvent(User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "USER_CREATED");
            event.put("userId", user.getId());
            event.put("email", user.getEmail());
            event.put("firstName", user.getFirstName());
            event.put("lastName", user.getLastName());
            event.put("phoneNumber", user.getPhoneNumber());
            event.put("roleId", user.getRoleId());
            event.put("timestamp", System.currentTimeMillis());
            
            kafkaTemplate.send("user-events", "user_created", event);
            log.info("Published USER_CREATED event for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish USER_CREATED event", e);
        }
    }

    private void publishUserRoleUpdatedEvent(User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "USER_ROLE_UPDATED");
            event.put("userId", user.getId());
            event.put("email", user.getEmail());
            event.put("roleId", user.getRoleId());
            event.put("timestamp", System.currentTimeMillis());
            
            kafkaTemplate.send("user-events", "user_role_updated", event);
            log.info("Published USER_ROLE_UPDATED event for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish USER_ROLE_UPDATED event", e);
        }
    }

    private void publishUserProfileUpdatedEvent(User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "USER_PROFILE_UPDATED");
            event.put("userId", user.getId());
            event.put("email", user.getEmail());
            event.put("firstName", user.getFirstName());
            event.put("lastName", user.getLastName());
            event.put("kycStatus", user.getKycStatus());
            event.put("isVerified", user.getIsVerified());
            event.put("timestamp", System.currentTimeMillis());
            
            kafkaTemplate.send("user-events", "user_profile_updated", event);
            log.info("Published USER_PROFILE_UPDATED event for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish USER_PROFILE_UPDATED event", e);
        }
    }
}