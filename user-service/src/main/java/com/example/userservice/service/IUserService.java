package com.example.userservice.service;

import com.example.userservice.dto.PageResponse;
import com.example.userservice.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {
  User create(User user);
  List<User> findAll();
  Optional<User> findById(Long id);
  Optional<User> update(Long id, User update);
  boolean delete(Long id);
  
  // Phân trang và tìm kiếm
  PageResponse<User> findAllWithPaging(Pageable pageable);
  PageResponse<User> searchUsers(String keyword, Pageable pageable);
  PageResponse<User> findByName(String name, Pageable pageable);
  PageResponse<User> findByEmail(String email, Pageable pageable);
}


