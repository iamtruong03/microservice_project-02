package com.example.userservice.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserContext {
  private Long userId;
  private String username;
  private String email;

  public UserContext(Long userId, String username, String email) {
    this.userId = userId;
    this.username = username;
    this.email = email;
  }
}
