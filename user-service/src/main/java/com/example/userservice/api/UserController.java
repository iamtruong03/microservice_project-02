package com.example.userservice.api;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.model.User;
import com.example.userservice.service.IUserService;
import com.example.userservice.util.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @Valid @RequestBody CreateUserRequest request) {
        try {
            User created = userService.createUser(request);
            return ResponseUtils.handlerCreated(created);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(name = "uid", defaultValue = "") String uid) {
        try {
            return ResponseUtils.handlerSuccess(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getAllUsersWithPaging(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = createPageable(page, size, sortBy, sortDir);
            return ResponseUtils.handlerSuccess(userService.getAllUsers(pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseUtils.handlerSuccess(user);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            User updated = userService.updateUser(id, request);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseUtils.handlerNoContent();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = createPageable(page, size, sortBy, sortDir);
            return ResponseUtils.handlerSuccess(userService.searchUsers(keyword, pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/search/name")
    public ResponseEntity<?> searchByName(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = createPageable(page, size, sortBy, sortDir);
            return ResponseUtils.handlerSuccess(userService.searchByName(name, pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/search/email")
    public ResponseEntity<?> searchByEmail(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = createPageable(page, size, sortBy, sortDir);
            return ResponseUtils.handlerSuccess(userService.searchByEmail(email, pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<?> advancedSearch(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String occupation,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = createPageable(page, size, sortBy, sortDir);
            return ResponseUtils.handlerSuccess(userService.advancedSearch(name, email, city, occupation, pageable));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Utility method to create Pageable
    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    // Role Management Endpoints
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> assignRoleToUser(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        try {
            User updated = userService.assignRoleToUser(userId, roleId);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<?> removeRoleFromUser(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        try {
            User updated = userService.removeRoleFromUser(userId, roleId);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<?> getUserRoles(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long userId) {
        try {
            return ResponseUtils.handlerSuccess(userService.getUserRoles(userId));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/{userId}/permissions")
    public ResponseEntity<?> getUserPermissions(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long userId) {
        try {
            return ResponseUtils.handlerSuccess(userService.getUserPermissions(userId));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}


