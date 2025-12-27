import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Statistic, Empty, Spin, Alert } from 'antd';
import {
  ShoppingCartOutlined,
  InboxOutlined,
  DollarOutlined,
  BellOutlined,
} from '@ant-design/icons';
import { orderService, inventoryService, accountingService } from '../../services';

const Dashboard = () => {
  const [stats, setStats] = useState({
    orders: 0,
    inventory: 0,
    transactions: 0,
    notifications: 0,
  });
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);

  const fetchStats = async () => {
    setLoading(true);
    try {
      const [ordersRes, inventoryRes, accountingRes] = await Promise.all([
        orderService.getAllOrders(),
        inventoryService.getAllInventories(),
        accountingService.getAllTransactions(),
      ]);

      setStats({
        orders: ordersRes.data.length,
        inventory: inventoryRes.data.length,
        transactions: accountingRes.data.length,
        notifications: Math.floor(Math.random() * 50) + 1, // Mock data
      });
      
      setLastUpdated(new Date().toLocaleString());
    } catch (error) {
      console.error('Failed to fetch stats:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
    const interval = setInterval(fetchStats, 30000); // Refresh every 30 seconds
    return () => clearInterval(interval);
  }, []);

  return (
    <div style={{ padding: '24px' }}>
      <Alert
        message="System Status"
        description={`Last updated: ${lastUpdated || 'Loading...'}`}
        type="info"
        closable
        style={{ marginBottom: '24px' }}
      />

      <Row gutter={16}>
        <Col xs={24} sm={12} md={6}>
          <Card hoverable>
            <Statistic
              title="Total Orders"
              value={stats.orders}
              prefix={<ShoppingCartOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card hoverable>
            <Statistic
              title="Inventory Items"
              value={stats.inventory}
              prefix={<InboxOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card hoverable>
            <Statistic
              title="Transactions"
              value={stats.transactions}
              prefix={<DollarOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card hoverable>
            <Statistic
              title="Notifications"
              value={stats.notifications}
              prefix={<BellOutlined />}
              valueStyle={{ color: '#f5222d' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginTop: '24px' }}>
        <Col xs={24} md={12}>
          <Card title="System Overview" style={{ height: '300px' }}>
            <Spin spinning={loading}>
              <div>
                <p>
                  <strong>API Gateway:</strong> Running on port 8080
                </p>
                <p>
                  <strong>Order Service:</strong> Running on port 8081
                </p>
                <p>
                  <strong>Inventory Service:</strong> Running on port 8082
                </p>
                <p>
                  <strong>Notification Service:</strong> Running on port 8083
                </p>
                <p>
                  <strong>Accounting Service:</strong> Running on port 8084
                </p>
                <p>
                  <strong>Eureka Server:</strong> Running on port 8761
                </p>
              </div>
            </Spin>
          </Card>
        </Col>

        <Col xs={24} md={12}>
          <Card title="Quick Links">
            <ul>
              <li>
                <a href="http://localhost:8761" target="_blank" rel="noopener noreferrer">
                  Eureka Dashboard
                </a>
              </li>
              <li>
                <a href="http://localhost:8080/api/orders" target="_blank" rel="noopener noreferrer">
                  Orders API
                </a>
              </li>
              <li>
                <a href="http://localhost:8080/api/inventory" target="_blank" rel="noopener noreferrer">
                  Inventory API
                </a>
              </li>
              <li>
                <a href="http://localhost:8080/api/accounting/transactions" target="_blank" rel="noopener noreferrer">
                  Accounting API
                </a>
              </li>
            </ul>
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginTop: '24px' }}>
        <Col xs={24}>
          <Card title="Architecture">
            <div style={{ backgroundColor: '#fafafa', padding: '16px', borderRadius: '4px' }}>
              <p style={{ marginBottom: '8px' }}>
                <strong>Frontend:</strong> React (Vite) on port 5173
              </p>
              <p style={{ marginBottom: '8px' }}>
                <strong>API Gateway:</strong> Spring Cloud Gateway on port 8080
              </p>
              <p style={{ marginBottom: '8px' }}>
                <strong>Microservices:</strong> 4 Spring Boot services
              </p>
              <p style={{ marginBottom: '8px' }}>
                <strong>Message Broker:</strong> Apache Kafka
              </p>
              <p style={{ marginBottom: '8px' }}>
                <strong>Service Discovery:</strong> Eureka on port 8761
              </p>
              <p>
                <strong>Databases:</strong> MySQL (3 instances)
              </p>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
