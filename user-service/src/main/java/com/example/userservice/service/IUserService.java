package com.example.userservice.service;

import com.example.userservice.model.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {
  User create(User user);
  List<User> findAll();
  Optional<User> findById(Long id);
  Optional<User> update(Long id, User update);
  boolean delete(Long id);
}


