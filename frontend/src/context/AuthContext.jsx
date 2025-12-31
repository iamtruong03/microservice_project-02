import React, { createContext, useState, useCallback, useEffect } from 'react';
import { notification } from 'antd';
import { authService } from '../services/authService';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is already logged in
    const token = authService.getToken();
    const storedUser = authService.getCurrentUser();

    if (token && storedUser) {
      setUser(storedUser);
      setIsAuthenticated(true);
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (userName, password) => {
    setLoading(true);
    try {
      const response = await authService.login(userName, password);
      const authData = response.data;

      // Use the new setAuthData method
      authService.setAuthData(authData);

      const user = {
        id: authData.id,
        userName: authData.userName,
        email: authData.email,
        fullName: authData.fullName,
        isAdmin: authData.isAdmin || false,
      };

      setUser(user);
      setIsAuthenticated(true);

      return { success: true, user };
    } catch (err) {
      let errorMessage = 'Login failed';
      
      // Handle different error response formats
      if (err.response?.data) {
        const data = err.response.data;
        
        // If the message contains nested JSON (from service-to-service calls), parse it
        if (data.message && data.message.includes('{"message":')) {
          try {
            const nestedStart = data.message.indexOf('{"message":');
            const nestedEnd = data.message.lastIndexOf('}') + 1;
            const nestedJson = data.message.substring(nestedStart, nestedEnd);
            const parsed = JSON.parse(nestedJson);
            errorMessage = parsed.message || data.message;
          } catch (parseError) {
            // If parsing fails, use the original message
            errorMessage = data.message || data.error || errorMessage;
          }
        } else {
          // Normal error response
          errorMessage = data.message || data.error || errorMessage;
        }
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  }, []);

  const register = useCallback(async (userData) => {
    setLoading(true);
    try {
      const response = await authService.register(userData);

      const { token, id, username, email, fullName, type } = response.data;

      const user = {
        id,
        username,
        email,
        fullName,
        type,
      };

      authService.setToken(token);
      authService.setUser(user);

      setUser(user);
      setIsAuthenticated(true);

      return { success: true, user };
    } catch (err) {
      const message = err.response?.data?.message || 'Registration failed';
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    setLoading(true);
    try {
      await authService.logout();
    } catch (err) {
      console.error('Logout error:', err);
    } finally {
      setUser(null);
      setIsAuthenticated(false);
      setLoading(false);
    }
  }, []);

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
