import axios from 'axios';

// Service URLs
const AUTH_SERVICE_URL = 'http://localhost:8086';
const USER_SERVICE_URL = 'http://localhost:8082';
const ACCOUNT_SERVICE_URL = 'http://localhost:8084';
const TRANSACTION_SERVICE_URL = 'http://localhost:8085';
const NOTIFICATION_SERVICE_URL = 'http://localhost:8083';

// Create axios instances for each service
export const authApi = axios.create({
  baseURL: AUTH_SERVICE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const userApi = axios.create({
  baseURL: USER_SERVICE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const accountApi = axios.create({
  baseURL: ACCOUNT_SERVICE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const transactionApi = axios.create({
  baseURL: TRANSACTION_SERVICE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const notificationApi = axios.create({
  baseURL: NOTIFICATION_SERVICE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add authentication token to all requests
const addAuthInterceptor = (apiInstance) => {
  apiInstance.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  apiInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        
        try {
          // Try to refresh token
          const refreshToken = localStorage.getItem('refreshToken');
          if (refreshToken) {
            const response = await authApi.post('/auth/refresh', { refreshToken });
            const { token: newToken, refreshToken: newRefreshToken } = response.data;
            
            localStorage.setItem('token', newToken);
            localStorage.setItem('refreshToken', newRefreshToken);
            
            // Retry original request with new token
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return apiInstance(originalRequest);
          }
        } catch (refreshError) {
          // Refresh failed, logout user
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
          window.location.href = '/login';
          return Promise.reject(refreshError);
        }
      }
      
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      
      return Promise.reject(error);
    }
  );
};

// Apply interceptors to all services
addAuthInterceptor(authApi);
addAuthInterceptor(userApi);
addAuthInterceptor(accountApi);
addAuthInterceptor(transactionApi);
addAuthInterceptor(notificationApi);

// Default export for backward compatibility
const api = authApi;
export default api;
