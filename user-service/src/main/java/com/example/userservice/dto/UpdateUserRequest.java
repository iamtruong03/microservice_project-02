package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
  @NotBlank(message = "name không được để trống")
  @Size(max = 100, message = "name tối đa 100 ký tự")
  private String name;

  @NotBlank(message = "email không được để trống")
  @Email(message = "email không hợp lệ")
  @Size(max = 150, message = "email tối đa 150 ký tự")
  private String email;
}


