package com.example.userservice.service.impl;

import com.example.userservice.exception.DuplicateEmailException;
import com.example.userservice.model.User;
import com.example.userservice.repo.UserRepository;
import com.example.userservice.service.IUserService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User create(User user) {
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new DuplicateEmailException("Email đã tồn tại");
    }
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public Optional<User> update(Long id, User update) {
    return userRepository.findById(id).map(existing -> {
      if (userRepository.existsByEmailAndIdNot(update.getEmail(), id)) {
        throw new DuplicateEmailException("Email đã tồn tại");
      }
      existing.setName(update.getName());
      existing.setEmail(update.getEmail());
      return userRepository.save(existing);
    });
  }

  @Override
  public boolean delete(Long id) {
    if (!userRepository.existsById(id)) {
      return false;
    }
    userRepository.deleteById(id);
    return true;
  }
}


