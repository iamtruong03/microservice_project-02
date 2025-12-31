import React from 'react';
import { ConfigProvider } from 'antd';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import Dashboard from './pages/Dashboard';
import Accounts from './pages/Accounts';
import Transactions from './pages/Transactions';
import TransactionHistory from './pages/TransactionHistory';
import Accounting from './pages/Accounting';
import Notifications from './pages/Notifications';
import Users from './pages/Users';
import Roles from './pages/Roles';
import Profile from './pages/Profile';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import RealTimeDashboard from './components/RealTimeDashboard';
import PrivateRoute from './components/PrivateRoute';
import AdminRoute from './components/AdminRoute';
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
                <Route path="/profile" element={<Profile />} />
                <Route path="/accounts" element={<Accounts />} />
                <Route path="/transactions" element={<Transactions />} />
                <Route path="/transaction-history" element={<TransactionHistory />} />
                <Route path="/accounting" element={<Accounting />} />
                <Route path="/notifications" element={<Notifications />} />
                <Route path="/users" element={<AdminRoute><Users /></AdminRoute>} />
                <Route path="/roles" element={<AdminRoute><Roles /></AdminRoute>} />
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
