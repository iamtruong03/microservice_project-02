import api from './api';

export const authService = {
  // Login
  login: (email, password) =>
    api.post('/auth/login', { email, password }),

  // Register
  register: (userData) =>
    api.post('/auth/register', userData),

  // Verify token
  verifyToken: () =>
    api.get('/auth/verify'),

  // Logout
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  // Get current user
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  // Set token
  setToken: (token) => {
    localStorage.setItem('token', token);
  },

  // Set user
  setUser: (user) => {
    localStorage.setItem('user', JSON.stringify(user));
  },

  // Get token
  getToken: () => localStorage.getItem('token'),

  // Check if authenticated
  isAuthenticated: () => !!localStorage.getItem('token'),
};
