package com.example.userservice.service.impl;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.exception.DuplicateEmailException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.repo.UserRepository;
import com.example.userservice.service.IUserService;
import com.example.userservice.util.UserMapper;
import com.example.userservice.util.UserQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserQueryBuilder queryBuilder;

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
        User savedUser = userRepository.save(user);
        log.info("Created user with id: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User existingUser = getUserById(id);
        validateUniqueFieldsForUpdate(request, id);
        userMapper.updateEntity(existingUser, request);
        User updatedUser = userRepository.save(existingUser);
        log.info("Updated user with id: {}", updatedUser.getId());
        return updatedUser;
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
}