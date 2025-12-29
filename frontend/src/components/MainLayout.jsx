import React, { useState, useContext } from 'react';
import {
  Layout,
  Menu,
  Button,
  Drawer,
  Badge,
  Dropdown,
  Space,
} from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  ShoppingCartOutlined,
  InboxOutlined,
  DollarOutlined,
  BellOutlined,
  BarChartOutlined,
  LogoutOutlined,
  UserOutlined,
  TeamOutlined,
  SafetyOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const { Header, Sider, Content } = Layout;

const MainLayout = ({ children }) => {
  const [collapsed, setCollapsed] = useState(false);
  const [mobileDrawerOpen, setMobileDrawerOpen] = useState(false);
  const navigate = useNavigate();
  const { user, logout } = useContext(AuthContext);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Đăng xuất',
      onClick: handleLogout,
    },
  ];

  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: 'Dashboard',
      onClick: () => {
        navigate('/');
        setMobileDrawerOpen(false);
      },
    },
    {
      key: '/orders',
      icon: <ShoppingCartOutlined />,
      label: 'Orders',
      onClick: () => {
        navigate('/orders');
        setMobileDrawerOpen(false);
      },
    },
    {
      key: '/inventory',
      icon: <InboxOutlined />,
      label: 'Inventory',
      onClick: () => {
        navigate('/inventory');
        setMobileDrawerOpen(false);
      },
    },
    {
      key: '/accounting',
      icon: <DollarOutlined />,
      label: 'Accounting',
      onClick: () => {
        navigate('/accounting');
        setMobileDrawerOpen(false);
      },
    },
    {
      key: '/notifications',
      icon: <BellOutlined />,
      label: 'Notifications',
      onClick: () => {
        navigate('/notifications');
        setMobileDrawerOpen(false);
      },
    },
    {
      key: '/users',
      icon: <TeamOutlined />,
      label: 'Users',
      onClick: () => {
        navigate('/users');
        setMobileDrawerOpen(false);
      },
    },
    {
      key: '/roles',
      icon: <SafetyOutlined />,
      label: 'Roles',
      onClick: () => {
        navigate('/roles');
        setMobileDrawerOpen(false);
      },
    },
    {
      key: '/statistics',
      icon: <BarChartOutlined />,
      label: 'Real-Time Statistics',
      onClick: () => {
        navigate('/statistics');
        setMobileDrawerOpen(false);
      },
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {/* Desktop Sidebar */}
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={200}
        style={{
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
          display: window.innerWidth < 768 ? 'none' : 'block',
        }}
      >
        <div
          style={{
            height: '64px',
            background: 'rgba(255, 255, 255, 0.85)',
            margin: '16px',
            borderRadius: '6px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 'bold',
            color: '#1890ff',
          }}
        >
          {!collapsed && '🚀 Microservice'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          defaultSelectedKeys={['/']}
          items={menuItems}
        />
      </Sider>

      {/* Mobile Drawer */}
      <Drawer
        title="Menu"
        placement="left"
        onClose={() => setMobileDrawerOpen(false)}
        open={mobileDrawerOpen}
        bodyStyle={{ padding: 0 }}
      >
        <Menu theme="light" mode="inline" items={menuItems} />
      </Drawer>

      {/* Main Content */}
      <Layout
        style={{
          marginLeft: window.innerWidth < 768 ? 0 : collapsed ? 80 : 200,
          transition: 'margin-left 0.2s',
        }}
      >
        {/* Header */}
        <Header
          style={{
            padding: '0 24px',
            background: '#fff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            boxShadow: '0 1px 4px rgba(0, 0, 0, 0.08)',
            position: 'sticky',
            top: 0,
            zIndex: 999,
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            {window.innerWidth < 768 ? (
              <Button
                type="text"
                icon={<MenuUnfoldOutlined />}
                onClick={() => setMobileDrawerOpen(true)}
              />
            ) : (
              <Button
                type="text"
                icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                onClick={() => setCollapsed(!collapsed)}
              />
            )}
            <h1 style={{ margin: 0, fontSize: '18px', fontWeight: 'bold' }}>
              Microservice Management
            </h1>
          </div>

          <Space size="middle">
            <Badge count={3} style={{ backgroundColor: '#52c41a' }}>
              <BellOutlined style={{ fontSize: '18px', cursor: 'pointer' }} />
            </Badge>

            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" trigger={['click']}>
              <Button type="text" icon={<UserOutlined />}>
                {user?.fullName || user?.username || 'User'}
              </Button>
            </Dropdown>
          </Space>
        </Header>

        {/* Content */}
        <Content style={{ background: '#f0f2f5' }}>
          {children ?? <Outlet />}
        </Content>

        {/* Footer */}
        <footer
          style={{
            textAlign: 'center',
            padding: '24px',
            background: '#fff',
            borderTop: '1px solid #f0f0f0',
            marginTop: '24px',
          }}
        >
          <p style={{ margin: 0, color: '#999' }}>
            © 2024 Microservice Platform. All rights reserved.
          </p>
        </footer>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
