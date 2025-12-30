package com.example.userservice.service;

import com.example.userservice.dto.RoleDTO;
import com.example.userservice.dto.PageResponse;
import com.example.userservice.model.Role;
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

    // Create new role
    public RoleDTO createRole(RoleDTO roleDTO) {
        log.info("Creating new role: {}", roleDTO.getName());
        
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new IllegalArgumentException("Role name already exists: " + roleDTO.getName());
        }

        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setIsActive(true);

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with id: {}", savedRole.getId());
        return convertToDTO(savedRole);
    }

    // Update existing role
    public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {
        log.info("Updating role with id: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        if (roleDTO.getName() != null && !roleDTO.getName().equals(role.getName())) {
            if (roleRepository.existsByName(roleDTO.getName())) {
                throw new IllegalArgumentException("Role name already exists: " + roleDTO.getName());
            }
            role.setName(roleDTO.getName());
        }

        if (roleDTO.getDescription() != null) {
            role.setDescription(roleDTO.getDescription());
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated successfully");
        return convertToDTO(updatedRole);
    }

    // Get role by id
    public RoleDTO getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        return convertToDTO(role);
    }

    // Get all roles
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
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
    public void deleteRole(Long roleId) {
        log.info("Deleting role with id: {}", roleId);
        
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
        
        roleRepository.deleteById(roleId);
        log.info("Role deleted successfully");
    }

    // Deactivate role
    public RoleDTO deactivateRole(Long roleId) {
        log.info("Deactivating role with id: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        
        role.setIsActive(false);
        Role updatedRole = roleRepository.save(role);
        return convertToDTO(updatedRole);
    }

    // Activate role
    public RoleDTO activateRole(Long roleId) {
        log.info("Activating role with id: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        
        role.setIsActive(true);
        Role updatedRole = roleRepository.save(role);
        return convertToDTO(updatedRole);
    }

    private RoleDTO convertToDTO(Role role) {
        return RoleDTO.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .isActive(role.getIsActive())
            .build();
    }
}
