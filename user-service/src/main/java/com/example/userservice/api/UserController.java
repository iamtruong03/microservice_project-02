package com.example.userservice.api;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.model.User;
import com.example.userservice.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .phoneNumber(request.getPhoneNumber())
        .dateOfBirth(request.getDateOfBirth())
        .gender(request.getGender())
        .nationalId(request.getNationalId())
        .address(request.getAddress())
        .city(request.getCity())
        .state(request.getState())
        .postalCode(request.getPostalCode())
        .country(request.getCountry())
        .occupation(request.getOccupation())
        .employerName(request.getEmployerName())
        .monthlyIncome(request.getMonthlyIncome())
        .preferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "en")
        .preferredCurrency(request.getPreferredCurrency() != null ? request.getPreferredCurrency() : "USD")
        .notificationEnabled(request.getNotificationEnabled() != null ? request.getNotificationEnabled() : true)
        .emergencyContactName(request.getEmergencyContactName())
        .emergencyContactPhone(request.getEmergencyContactPhone())
        .emergencyContactRelationship(request.getEmergencyContactRelationship())
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

  @GetMapping("/paged")
  public ResponseEntity<PageResponse<User>> listWithPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    
    Sort sort = sortDir.equalsIgnoreCase("desc") ? 
        Sort.by(sortBy).descending() : 
        Sort.by(sortBy).ascending();
    
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(userService.findAllWithPaging(pageable));
  }

  @GetMapping("/search")
  public ResponseEntity<PageResponse<User>> search(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    
    Sort sort = sortDir.equalsIgnoreCase("desc") ? 
        Sort.by(sortBy).descending() : 
        Sort.by(sortBy).ascending();
    
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(userService.searchUsers(keyword, pageable));
  }

  @GetMapping("/search/name")
  public ResponseEntity<PageResponse<User>> searchByName(
      @RequestParam String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    
    Sort sort = sortDir.equalsIgnoreCase("desc") ? 
        Sort.by(sortBy).descending() : 
        Sort.by(sortBy).ascending();
    
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(userService.findByName(name, pageable));
  }

  @GetMapping("/search/email")
  public ResponseEntity<PageResponse<User>> searchByEmail(
      @RequestParam String email,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    
    Sort sort = sortDir.equalsIgnoreCase("desc") ? 
        Sort.by(sortBy).descending() : 
        Sort.by(sortBy).ascending();
    
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(userService.findByEmail(email, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> get(@PathVariable Long id) {
    return userService.findById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    return userService.findById(id)
        .map(existingUser -> {
          // Only update non-null fields
          if (request.getFirstName() != null) existingUser.setFirstName(request.getFirstName());
          if (request.getLastName() != null) existingUser.setLastName(request.getLastName());
          if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
          if (request.getPhoneNumber() != null) existingUser.setPhoneNumber(request.getPhoneNumber());
          if (request.getDateOfBirth() != null) existingUser.setDateOfBirth(request.getDateOfBirth());
          if (request.getGender() != null) existingUser.setGender(request.getGender());
          if (request.getAddress() != null) existingUser.setAddress(request.getAddress());
          if (request.getCity() != null) existingUser.setCity(request.getCity());
          if (request.getState() != null) existingUser.setState(request.getState());
          if (request.getPostalCode() != null) existingUser.setPostalCode(request.getPostalCode());
          if (request.getCountry() != null) existingUser.setCountry(request.getCountry());
          if (request.getOccupation() != null) existingUser.setOccupation(request.getOccupation());
          if (request.getEmployerName() != null) existingUser.setEmployerName(request.getEmployerName());
          if (request.getMonthlyIncome() != null) existingUser.setMonthlyIncome(request.getMonthlyIncome());
          if (request.getPreferredLanguage() != null) existingUser.setPreferredLanguage(request.getPreferredLanguage());
          if (request.getPreferredCurrency() != null) existingUser.setPreferredCurrency(request.getPreferredCurrency());
          if (request.getNotificationEnabled() != null) existingUser.setNotificationEnabled(request.getNotificationEnabled());
          if (request.getEmergencyContactName() != null) existingUser.setEmergencyContactName(request.getEmergencyContactName());
          if (request.getEmergencyContactPhone() != null) existingUser.setEmergencyContactPhone(request.getEmergencyContactPhone());
          if (request.getEmergencyContactRelationship() != null) existingUser.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
          
          return userService.update(id, existingUser)
              .map(ResponseEntity::ok)
              .orElseGet(() -> ResponseEntity.notFound().build());
        })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean deleted = userService.delete(id);
    return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}


