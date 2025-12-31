import { userApi } from './api';

const API_BASE_URL = '/api';

export const roleService = {
  // Create role
  createRole: (roleData) =>
    userApi.post(`${API_BASE_URL}/roles`, roleData),

  // Update role
  updateRole: (roleId, roleData) =>
    userApi.put(`${API_BASE_URL}/roles/${roleId}`, roleData),

  // Get role by id
  getRoleById: (roleId) =>
    userApi.get(`${API_BASE_URL}/roles/${roleId}`),

  // Get all roles
  getAllRoles: (page = 0, size = 10) =>
    userApi.get(`${API_BASE_URL}/roles?page=${page}&size=${size}`),

  // Search roles
  searchRoles: (keyword, page = 0, size = 10) =>
    userApi.get(`${API_BASE_URL}/roles/search?keyword=${keyword}&page=${page}&size=${size}`),

  // Delete role
  deleteRole: (roleId) =>
    userApi.delete(`${API_BASE_URL}/roles/${roleId}`),

  // Assign permission to role
  assignPermissionToRole: (roleId, permissionId) =>
    userApi.post(`${API_BASE_URL}/roles/${roleId}/permissions/${permissionId}`),

  // Remove permission from role
  removePermissionFromRole: (roleId, permissionId) =>
    userApi.delete(`${API_BASE_URL}/roles/${roleId}/permissions/${permissionId}`),
};

export const permissionService = {
  // Get all permissions
  getAllPermissions: (page = 0, size = 10) =>
    userApi.get(`${API_BASE_URL}/permissions?page=${page}&size=${size}`),

  // Search permissions
  searchPermissions: (keyword, page = 0, size = 10) =>
    userApi.get(`${API_BASE_URL}/permissions/search?keyword=${keyword}&page=${page}&size=${size}`),

  // Get permission by id
  getPermissionById: (permissionId) =>
    userApi.get(`${API_BASE_URL}/permissions/${permissionId}`),

  // Create permission
  createPermission: (permissionData) =>
    userApi.post(`${API_BASE_URL}/permissions`, permissionData),

  // Update permission
  updatePermission: (permissionId, permissionData) =>
    userApi.put(`${API_BASE_URL}/permissions/${permissionId}`, permissionData),

  // Delete permission
  deletePermission: (permissionId) =>
    userApi.delete(`${API_BASE_URL}/permissions/${permissionId}`),
};

export const userService = {
  // Get all users with pagination
  getAllUsers: (page = 0, size = 10) =>
    userApi.get(`${API_BASE_URL}/users?page=${page}&size=${size}`),

  // Get users with pagination and sorting
  getUsersPaged: (page = 0, size = 10, sortBy = 'id', sortDir = 'asc') =>
    userApi.get(`${API_BASE_URL}/users?page=${page}&size=${size}&sort=${sortBy},${sortDir}`),

  // Search users
  searchUsers: (keyword, page = 0, size = 10, sortBy = 'id', sortDir = 'asc') =>
    userApi.get(`${API_BASE_URL}/users/search?keyword=${keyword}&page=${page}&size=${size}&sort=${sortBy},${sortDir}`),

  // Advanced search
  advancedSearch: (params, page = 0, size = 10) => {
    const searchParams = new URLSearchParams({
      page,
      size,
      ...params
    });
    return userApi.get(`${API_BASE_URL}/users/advanced-search?${searchParams}`);
  },

  // Get user by id
  getUserById: (userId) =>
    userApi.get(`${API_BASE_URL}/users/${userId}`),

  // Create user
  createUser: (userData) =>
    userApi.post(`${API_BASE_URL}/users`, userData),

  // Update user
  updateUser: (userId, userData) =>
    userApi.put(`${API_BASE_URL}/users/${userId}`, userData),

  // Delete user
  deleteUser: (userId) =>
    userApi.delete(`${API_BASE_URL}/users/${userId}`),

  // Assign role to user
  assignRoleToUser: (userId, roleId) =>
    userApi.post(`${API_BASE_URL}/users/${userId}/roles/${roleId}`),
};
