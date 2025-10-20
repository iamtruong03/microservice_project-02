package com.example.userservice.service.impl;

import com.example.userservice.dto.PageResponse;
import com.example.userservice.exception.DuplicateEmailException;
import com.example.userservice.model.User;
import com.example.userservice.repo.UserRepository;
import com.example.userservice.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

  @Override
  @Transactional(readOnly = true)
  public PageResponse<User> findAllWithPaging(Pageable pageable) {
    Page<User> page = userRepository.findAll(pageable);
    return buildPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<User> searchUsers(String keyword, Pageable pageable) {
    Page<User> page = userRepository.findByKeyword(keyword, pageable);
    return buildPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<User> findByName(String name, Pageable pageable) {
    Page<User> page = userRepository.findByNameContainingIgnoreCase(name, pageable);
    return buildPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<User> findByEmail(String email, Pageable pageable) {
    Page<User> page = userRepository.findByEmailContainingIgnoreCase(email, pageable);
    return buildPageResponse(page);
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


