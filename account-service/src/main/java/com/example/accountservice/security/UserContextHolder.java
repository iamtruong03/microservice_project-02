package com.example.accountservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserContextHolder {

  public static Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof Map) {
      Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
      Object userId = claims.get("userId");
      if (userId != null) {
        return Long.valueOf(userId.toString());
      }
    }
    return null;
  }

  public static String getCurrentUsername() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      return auth.getName();
    }
    return null;
  }

  public static boolean hasAccess(Long resourceUserId) {
    Long currentUserId = getCurrentUserId();
    return currentUserId != null && currentUserId.equals(resourceUserId);
  }
}
