package com.example.userservice.service;

import com.example.userservice.dto.PermissionDTO;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.model.Permission;
import com.example.userservice.repository.PermissionRepository;
import com.example.userservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    // Create new permission
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        
        if (permissionRepository.existsByName(permissionDTO.getName())) {
            throw new IllegalArgumentException("Permission name already exists: " + permissionDTO.getName());
        }

        Permission permission = new Permission();
        permission.setName(permissionDTO.getName());
        permission.setDescription(permissionDTO.getDescription());
        permission.setResource(permissionDTO.getResource());
        permission.setAction(permissionDTO.getAction());
        permission.setIsActive(true);

        Permission savedPermission = permissionRepository.save(permission);
        return convertToDTO(savedPermission);
    }

    // Update existing permission
    public PermissionDTO updatePermission(Long permissionId, PermissionDTO permissionDTO) {
        
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        if (permissionDTO.getName() != null && !permissionDTO.getName().equals(permission.getName())) {
            if (permissionRepository.existsByName(permissionDTO.getName())) {
                throw new IllegalArgumentException("Permission name already exists: " + permissionDTO.getName());
            }
            permission.setName(permissionDTO.getName());
        }

        if (permissionDTO.getDescription() != null) {
            permission.setDescription(permissionDTO.getDescription());
        }

        if (permissionDTO.getResource() != null) {
            permission.setResource(permissionDTO.getResource());
        }

        if (permissionDTO.getAction() != null) {
            permission.setAction(permissionDTO.getAction());
        }

        Permission updatedPermission = permissionRepository.save(permission);
        return convertToDTO(updatedPermission);
    }

    // Get permission by id
    public PermissionDTO getPermissionById(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));
        return convertToDTO(permission);
    }

    // Get all permissions
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Get permissions with pagination
    public PageResponse<PermissionDTO> getAllPermissionsWithPagination(Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        return PageResponse.<PermissionDTO>builder()
            .content(permissions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()))
            .page(permissions.getNumber())
            .size(permissions.getSize())
            .totalElements(permissions.getTotalElements())
            .totalPages(permissions.getTotalPages())
            .first(permissions.isFirst())
            .last(permissions.isLast())
            .hasNext(permissions.hasNext())
            .hasPrevious(permissions.hasPrevious())
            .build();
    }

    // Search permissions by name
    public PageResponse<PermissionDTO> searchPermissions(String keyword, Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return PageResponse.<PermissionDTO>builder()
            .content(permissions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()))
            .page(permissions.getNumber())
            .size(permissions.getSize())
            .totalElements(permissions.getTotalElements())
            .totalPages(permissions.getTotalPages())
            .first(permissions.isFirst())
            .last(permissions.isLast())
            .hasNext(permissions.hasNext())
            .hasPrevious(permissions.hasPrevious())
            .build();
    }

    // Delete permission
    public void deletePermission(Long permissionId) {
        
        if (!permissionRepository.existsById(permissionId)) {
            throw new ResourceNotFoundException("Permission not found with id: " + permissionId);
        }
        
        permissionRepository.deleteById(permissionId);
    }

    // Deactivate permission
    public PermissionDTO deactivatePermission(Long permissionId) {
        
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));
        
        permission.setIsActive(false);
        Permission updatedPermission = permissionRepository.save(permission);
        return convertToDTO(updatedPermission);
    }

    // Activate permission
    public PermissionDTO activatePermission(Long permissionId) {
        
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));
        
        permission.setIsActive(true);
        Permission updatedPermission = permissionRepository.save(permission);
        return convertToDTO(updatedPermission);
    }

    private PermissionDTO convertToDTO(Permission permission) {
        return PermissionDTO.builder()
            .id(permission.getId())
            .name(permission.getName())
            .description(permission.getDescription())
            .resource(permission.getResource())
            .action(permission.getAction())
            .isActive(permission.getIsActive())
            .build();
    }
}
