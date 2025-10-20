package com.example.userservice.api;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.model.User;
import com.example.userservice.service.IUserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final IUserService userService;

  public UserController(IUserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest request) {
    User toCreate = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .build();
    User created = userService.create(toCreate);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.getId())
        .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public ResponseEntity<List<User>> list() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> get(@PathVariable Long id) {
    return userService.findById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    User toUpdate = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .build();
    return userService.update(id, toUpdate)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean deleted = userService.delete(id);
    return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}


