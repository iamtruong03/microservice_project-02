import { authApi } from './api';

export const authService = {
  // Login
  login: (userName, password) =>
    authApi.post('/auth/login', { userName, password }),

  // Register
  register: (userData) =>
    authApi.post('/auth/register', userData),

  // Verify token
  verifyToken: () =>
    authApi.get('/auth/validate'),

  // Logout
  logout: async () => {
    const token = authService.getToken();
    if (token) {
      try {
        await authApi.post('/auth/logout', { token });
      } catch (error) {
        console.error('Logout API call failed:', error);
      }
    }
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  // Get current user
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  // Set token and user data from login response
  setAuthData: (authResponse) => {
    if (authResponse.token) {
      localStorage.setItem('token', authResponse.token);
    }
    if (authResponse.refreshToken) {
      localStorage.setItem('refreshToken', authResponse.refreshToken);
    }
    if (authResponse.userName || authResponse.id) {
      const user = {
        id: authResponse.id,
        userName: authResponse.userName,
        email: authResponse.email,
        fullName: authResponse.fullName,
        isAdmin: authResponse.isAdmin || false
      };
      localStorage.setItem('user', JSON.stringify(user));
    }
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

  // Get refresh token
  getRefreshToken: () => localStorage.getItem('refreshToken'),

  // Refresh token
  refreshToken: async () => {
    const refreshToken = authService.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    
    const response = await authApi.post('/auth/refresh', { refreshToken });
    const authData = response.data;
    authService.setAuthData(authData);
    return authData;
  },

  // Check if authenticated
  isAuthenticated: () => !!localStorage.getItem('token'),
};
