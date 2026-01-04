package com.example.userservice.service;

import com.example.userservice.dto.RoleDTO;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.model.Role;
import com.example.userservice.model.RolePermission;
import com.example.userservice.repository.RolePermissionRepository;
import com.example.userservice.repository.RoleRepository;
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
public class RoleService {

  private final RoleRepository roleRepository;
  private final RolePermissionRepository rolePermissionRepository;

  // Create new role
  public RoleDTO createRole(RoleDTO roleDTO) {

    if (roleRepository.existsByName(roleDTO.getName())) {
      throw new IllegalArgumentException("Role name already exists: " + roleDTO.getName());
    }

    // 1️⃣ Tạo role
    Role role = new Role();
    role.setName(roleDTO.getName());
    role.setDescription(roleDTO.getDescription());
    role.setIsActive(true);

    Role savedRole = roleRepository.save(role);

    // 2️⃣ Insert role_permissions bằng ID
    if (roleDTO.getPermissionIds() != null) {

      List<RolePermission> rolePermissions =
          roleDTO.getPermissionIds().stream()
              .distinct()
              .map(pid -> new RolePermission(null, savedRole.getId(), pid))
              .toList();

      rolePermissionRepository.saveAll(rolePermissions);
    }

    return convertToDTO(savedRole);
  }


  // Update existing role
  public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {

    Role role = roleRepository.findById(roleId)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

    if (roleDTO.getName() != null) {
      role.setName(roleDTO.getName());
    }

    if (roleDTO.getDescription() != null) {
      role.setDescription(roleDTO.getDescription());
    }

    roleRepository.save(role);

    // 🔥 Reset permission mapping
    if (roleDTO.getPermissionIds() != null) {

      rolePermissionRepository.deleteByRoleId(roleId);

      List<RolePermission> rolePermissions =
          roleDTO.getPermissionIds().stream()
              .distinct()
              .map(pid -> new RolePermission(null, roleId, pid))
              .toList();

      rolePermissionRepository.saveAll(rolePermissions);
    }

    return convertToDTO(role);
  }


  // Get role by id
  public RoleDTO getRoleById(Long roleId) {
    Role role = roleRepository.findById(roleId)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    return convertToDTO(role);
  }

  // Get roles with pagination
  public PageResponse<RoleDTO> getAllRolesWithPagination(Pageable pageable) {
    Page<Role> roles = roleRepository.findAll(pageable);
    return PageResponse.<RoleDTO>builder()
        .content(roles.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()))
        .page(roles.getNumber())
        .size(roles.getSize())
        .totalElements(roles.getTotalElements())
        .totalPages(roles.getTotalPages())
        .first(roles.isFirst())
        .last(roles.isLast())
        .hasNext(roles.hasNext())
        .hasPrevious(roles.hasPrevious())
        .build();
  }

  // Search roles by name
  public PageResponse<RoleDTO> searchRoles(String keyword, Pageable pageable) {
    Page<Role> roles = roleRepository.findByNameContainingIgnoreCase(keyword, pageable);
    return PageResponse.<RoleDTO>builder()
        .content(roles.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()))
        .page(roles.getNumber())
        .size(roles.getSize())
        .totalElements(roles.getTotalElements())
        .totalPages(roles.getTotalPages())
        .first(roles.isFirst())
        .last(roles.isLast())
        .hasNext(roles.hasNext())
        .hasPrevious(roles.hasPrevious())
        .build();
  }

  // Delete role
  @Transactional
  public void deleteRole(Long roleId) {

      if (!roleRepository.existsById(roleId)) {
          throw new ResourceNotFoundException("Role not found with id: " + roleId);
      }

      rolePermissionRepository.deleteByRoleId(roleId);
      roleRepository.deleteById(roleId);
  }


    // Deactivate role
  public RoleDTO deactivateRole(Long roleId) {

    Role role = roleRepository.findById(roleId)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

    role.setIsActive(false);
    Role updatedRole = roleRepository.save(role);
    return convertToDTO(updatedRole);
  }

  // Activate role
  public RoleDTO activateRole(Long roleId) {

    Role role = roleRepository.findById(roleId)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

    role.setIsActive(true);
    Role updatedRole = roleRepository.save(role);
    return convertToDTO(updatedRole);
  }

  private RoleDTO convertToDTO(Role role) {

    List<Long> permissionIds =
        rolePermissionRepository.findByRoleId(role.getId())
            .stream()
            .map(RolePermission::getPermissionId)
            .toList();

    return RoleDTO.builder()
        .id(role.getId())
        .name(role.getName())
        .description(role.getDescription())
        .isActive(role.getIsActive())
        .permissionIds(permissionIds)
        .build();
  }
}
