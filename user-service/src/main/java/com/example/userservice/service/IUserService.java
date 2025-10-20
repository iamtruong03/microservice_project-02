package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    
    List<User> getAllUsers();
    
    PageResponse<User> getAllUsers(Pageable pageable);
    
    User getUserById(Long id);
    
    User createUser(CreateUserRequest request);
    
    User updateUser(Long id, UpdateUserRequest request);
    
    void deleteUser(Long id);
    
    PageResponse<User> searchUsers(String keyword, Pageable pageable);
    
    PageResponse<User> searchByName(String name, Pageable pageable);
    
    PageResponse<User> searchByEmail(String email, Pageable pageable);
    
    PageResponse<User> advancedSearch(String name, String email, String city, String occupation, Pageable pageable);
}
