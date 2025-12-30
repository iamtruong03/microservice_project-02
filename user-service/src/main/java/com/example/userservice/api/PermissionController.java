package com.example.userservice.api;

import com.example.userservice.dto.PermissionDTO;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.service.PermissionService;
import com.example.userservice.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    // Create new permission
    @PostMapping
    public ResponseEntity<?> createPermission(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestBody PermissionDTO permissionDTO) {
        try {
            PermissionDTO created = permissionService.createPermission(permissionDTO);
            return ResponseUtils.handlerSuccess(created);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Update permission
    @PutMapping("/{permissionId}")
    public ResponseEntity<?> updatePermission(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long permissionId,
            @RequestBody PermissionDTO permissionDTO) {
        try {
            PermissionDTO updated = permissionService.updatePermission(permissionId, permissionDTO);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Get permission by id
    @GetMapping("/{permissionId}")
    public ResponseEntity<?> getPermissionById(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long permissionId) {
        try {
            PermissionDTO permission = permissionService.getPermissionById(permissionId);
            return ResponseUtils.handlerSuccess(permission);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Get all permissions
    @GetMapping
    public ResponseEntity<?> getAllPermissions(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<PermissionDTO> permissions = permissionService.getAllPermissionsWithPagination(pageable);
            return ResponseUtils.handlerSuccess(permissions);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Search permissions
    @GetMapping("/search")
    public ResponseEntity<?> searchPermissions(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<PermissionDTO> permissions = permissionService.searchPermissions(keyword, pageable);
            return ResponseUtils.handlerSuccess(permissions);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Delete permission
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<?> deletePermission(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long permissionId) {
        try {
            permissionService.deletePermission(permissionId);
            return ResponseUtils.handlerSuccess("Permission deleted successfully");
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Deactivate permission
    @PatchMapping("/{permissionId}/deactivate")
    public ResponseEntity<?> deactivatePermission(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long permissionId) {
        try {
            PermissionDTO updated = permissionService.deactivatePermission(permissionId);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    // Activate permission
    @PatchMapping("/{permissionId}/activate")
    public ResponseEntity<?> activatePermission(
            @RequestHeader(name = "uid", defaultValue = "") String uid,
            @PathVariable Long permissionId) {
        try {
            PermissionDTO updated = permissionService.activatePermission(permissionId);
            return ResponseUtils.handlerSuccess(updated);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
