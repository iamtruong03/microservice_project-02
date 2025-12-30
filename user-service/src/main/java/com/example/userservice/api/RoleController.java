package com.example.userservice.api;

import com.example.userservice.dto.RoleDTO;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.service.RoleService;
import com.example.userservice.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // Create new role
    @PostMapping
    public ResponseEntity<?> createRole(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestBody RoleDTO roleDTO) {
        try {
            RoleDTO created = roleService.createRole(roleDTO);
            return ResponseUtils.handlerSuccess(created);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Update role
    @PutMapping("/{roleId}")
    public ResponseEntity<?> updateRole(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long roleId,
            @RequestBody RoleDTO roleDTO) {
        try {
            RoleDTO updated = roleService.updateRole(roleId, roleDTO);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Get role by id
    @GetMapping("/{roleId}")
    public ResponseEntity<?> getRoleById(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long roleId) {
        try {
            RoleDTO role = roleService.getRoleById(roleId);
            return ResponseUtils.handlerSuccess(role);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Get all roles
    @GetMapping
    public ResponseEntity<?> getAllRoles(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<RoleDTO> roles = roleService.getAllRolesWithPagination(pageable);
            return ResponseUtils.handlerSuccess(roles);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Search roles
    @GetMapping("/search")
    public ResponseEntity<?> searchRoles(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<RoleDTO> roles = roleService.searchRoles(keyword, pageable);
            return ResponseUtils.handlerSuccess(roles);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Delete role
    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long roleId) {
        try {
            roleService.deleteRole(roleId);
            return ResponseUtils.handlerSuccess("Role deleted successfully");
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Deactivate role
    @PatchMapping("/{roleId}/deactivate")
    public ResponseEntity<?> deactivateRole(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long roleId) {
        try {
            RoleDTO updated = roleService.deactivateRole(roleId);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Activate role
    @PatchMapping("/{roleId}/activate")
    public ResponseEntity<?> activateRole(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long roleId) {
        try {
            RoleDTO updated = roleService.activateRole(roleId);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
