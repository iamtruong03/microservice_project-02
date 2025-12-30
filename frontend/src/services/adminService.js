import axios from 'axios';

const API_BASE_URL = 'http://localhost:8082/api';

export const roleService = {
  // Create role
  createRole: (roleData) =>
    axios.post(`${API_BASE_URL}/roles`, roleData, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Update role
  updateRole: (roleId, roleData) =>
    axios.put(`${API_BASE_URL}/roles/${roleId}`, roleData, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Get role by id
  getRoleById: (roleId) =>
    axios.get(`${API_BASE_URL}/roles/${roleId}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Get all roles
  getAllRoles: (page = 0, size = 10) =>
    axios.get(`${API_BASE_URL}/roles?page=${page}&size=${size}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Search roles
  searchRoles: (keyword, page = 0, size = 10) =>
    axios.get(`${API_BASE_URL}/roles/search?keyword=${keyword}&page=${page}&size=${size}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Delete role
  deleteRole: (roleId) =>
    axios.delete(`${API_BASE_URL}/roles/${roleId}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Deactivate role
  deactivateRole: (roleId) =>
    axios.patch(`${API_BASE_URL}/roles/${roleId}/deactivate`, {}, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Activate role
  activateRole: (roleId) =>
    axios.patch(`${API_BASE_URL}/roles/${roleId}/activate`, {}, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    })
};

export const permissionService = {
  // Create permission
  createPermission: (permissionData) =>
    axios.post(`${API_BASE_URL}/permissions`, permissionData, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Update permission
  updatePermission: (permissionId, permissionData) =>
    axios.put(`${API_BASE_URL}/permissions/${permissionId}`, permissionData, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Get permission by id
  getPermissionById: (permissionId) =>
    axios.get(`${API_BASE_URL}/permissions/${permissionId}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Get all permissions
  getAllPermissions: (page = 0, size = 10) =>
    axios.get(`${API_BASE_URL}/permissions?page=${page}&size=${size}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Search permissions
  searchPermissions: (keyword, page = 0, size = 10) =>
    axios.get(`${API_BASE_URL}/permissions/search?keyword=${keyword}&page=${page}&size=${size}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Delete permission
  deletePermission: (permissionId) =>
    axios.delete(`${API_BASE_URL}/permissions/${permissionId}`, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Deactivate permission
  deactivatePermission: (permissionId) =>
    axios.patch(`${API_BASE_URL}/permissions/${permissionId}/deactivate`, {}, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    }),

  // Activate permission
  activatePermission: (permissionId) =>
    axios.patch(`${API_BASE_URL}/permissions/${permissionId}/activate`, {}, {
      headers: { 'uid': localStorage.getItem('userId') || '' }
    })
};
