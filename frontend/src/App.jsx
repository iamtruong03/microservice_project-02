import React from 'react';
import { ConfigProvider } from 'antd';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import Dashboard from './pages/Dashboard';
import Orders from './pages/Orders';
import Inventory from './pages/Inventory';
import Accounting from './pages/Accounting';
import Notifications from './pages/Notifications';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import RealTimeDashboard from './components/RealTimeDashboard';
import PrivateRoute from './components/PrivateRoute';
import { StatisticsProvider } from './context/StatisticsContext';
import { AuthProvider } from './context/AuthContext';
import './App.css';

function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1890ff',
          borderRadius: 6,
        },
      }}
    >
      <AuthProvider>
        <StatisticsProvider>
          <BrowserRouter>
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Protected Routes */}
              <Route element={<PrivateRoute><MainLayout /></PrivateRoute>}>
                <Route path="/" element={<Dashboard />} />
                <Route path="/orders" element={<Orders />} />
                <Route path="/inventory" element={<Inventory />} />
                <Route path="/accounting" element={<Accounting />} />
                <Route path="/notifications" element={<Notifications />} />
                <Route path="/statistics" element={<RealTimeDashboard />} />
              </Route>

              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </BrowserRouter>
        </StatisticsProvider>
      </AuthProvider>
    </ConfigProvider>
  );
}

export default App;
