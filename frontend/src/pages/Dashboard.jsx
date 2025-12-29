import React, { useState, useEffect, useContext } from 'react';
import { Row, Col, Card, Statistic, Empty, Spin, Alert, Button, Space, Table, Progress } from 'antd';
import {
  CreditCardOutlined,
  DollarOutlined,
  SwapOutlined,
  HistoryOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { accountService } from '../services/accountService';
import { AuthContext } from '../context/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const [accounts, setAccounts] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [accountsRes, statsRes, historyRes] = await Promise.all([
        accountService.getUserAccounts(),
        accountService.getAccountStatistics(),
        accountService.getTransactionHistory(),
      ]);

      setAccounts(accountsRes.data.data || []);
      setStatistics(statsRes.data.data || {});
      setRecentTransactions((historyRes.data.data || []).slice(0, 5));
      setLastUpdated(new Date().toLocaleString());
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
    const interval = setInterval(fetchDashboardData, 30000); // Refresh every 30 seconds
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="dashboard-container">
      <Alert
        message="Banking Dashboard"
        description={`Welcome, ${user?.username || 'User'}! Last updated: ${lastUpdated || 'Loading...'}`}
        type="success"
        closable
        style={{ marginBottom: '24px' }}
      />

      {/* Key Statistics */}
      <Row gutter={16}>
        <Col xs={24} sm={12} md={6}>
          <Card hoverable className="stat-card">
            <Statistic
              title="Total Balance"
              value={statistics?.totalBalance || 0}
              prefix="$"
              valueStyle={{ color: '#1890ff', fontSize: '24px' }}
              suffix={
                <DollarOutlined style={{ marginLeft: '8px', color: '#1890ff' }} />
              }
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card hoverable className="stat-card">
            <Statistic
              title="Total Accounts"
              value={statistics?.totalAccounts || 0}
              valueStyle={{ color: '#52c41a', fontSize: '24px' }}
              suffix={
                <CreditCardOutlined style={{ marginLeft: '8px', color: '#52c41a' }} />
              }
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card hoverable className="stat-card">
            <Statistic
              title="Total Transactions"
              value={statistics?.totalTransactions || 0}
              valueStyle={{ color: '#faad14', fontSize: '24px' }}
              suffix={
                <SwapOutlined style={{ marginLeft: '8px', color: '#faad14' }} />
              }
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card hoverable className="stat-card">
            <Button 
              type="primary" 
              icon={<ReloadOutlined />}
              onClick={fetchDashboardData}
              loading={loading}
              block
            >
              Refresh
            </Button>
          </Card>
        </Col>
      </Row>

      {/* Bank Accounts */}
      <Row gutter={16} style={{ marginTop: '24px' }}>
        <Col xs={24} md={12}>
          <Card 
            title={<><CreditCardOutlined /> My Bank Accounts</>}
            extra={
              <Button type="link" onClick={() => navigate('/accounts')}>
                View All
              </Button>
            }
          >
            {loading ? (
              <Spin size="large" />
            ) : accounts.length === 0 ? (
              <Empty description="No accounts found">
                <Button type="primary" onClick={() => navigate('/accounts')}>
                  Create Account
                </Button>
              </Empty>
            ) : (
              <div className="accounts-list">
                {accounts.map((account) => (
                  <div key={account.id} className="account-item">
                    <div className="account-header">
                      <span className="account-number">{account.accountNumber}</span>
                      <span className={`account-type ${account.accountType.toLowerCase()}`}>
                        {account.accountType}
                      </span>
                    </div>
                    <div className="account-balance">
                      ${parseFloat(account.balance).toFixed(2)}
                    </div>
                    <div className="account-status">
                      <Progress 
                        percent={80} 
                        status={account.status === 'ACTIVE' ? 'success' : 'exception'}
                        showInfo={false}
                      />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </Col>

        {/* Quick Actions */}
        <Col xs={24} md={12}>
          <Card title="Quick Actions">
            <Space direction="vertical" style={{ width: '100%' }} size="large">
              <Button 
                type="primary" 
                size="large"
                block
                icon={<CreditCardOutlined />}
                onClick={() => navigate('/accounts')}
              >
                Manage Accounts
              </Button>
              <Button 
                size="large"
                block
                icon={<SwapOutlined />}
                onClick={() => navigate('/transactions')}
              >
                Transfer Money
              </Button>
              <Button 
                size="large"
                block
                icon={<HistoryOutlined />}
                onClick={() => navigate('/transaction-history')}
              >
                View History
              </Button>
            </Space>
          </Card>
        </Col>
      </Row>

      {/* Recent Transactions */}
      <Row gutter={16} style={{ marginTop: '24px' }}>
        <Col xs={24}>
          <Card
            title={<><HistoryOutlined /> Recent Transactions</>}
            extra={
              <Button type="link" onClick={() => navigate('/transaction-history')}>
                View All
              </Button>
            }
          >
            {loading ? (
              <Spin size="large" />
            ) : recentTransactions.length === 0 ? (
              <Empty description="No transactions yet" />
            ) : (
              <Table
                columns={[
                  {
                    title: 'Date',
                    dataIndex: 'createdAt',
                    key: 'createdAt',
                    render: (date) => new Date(date).toLocaleString(),
                  },
                  {
                    title: 'Type',
                    dataIndex: 'transactionType',
                    key: 'transactionType',
                    render: (type) => (
                      <span style={{
                        padding: '4px 8px',
                        borderRadius: '4px',
                        backgroundColor: type === 'TRANSFER' ? '#e6f7ff' :
                                        type === 'DEPOSIT' ? '#f6ffed' :
                                        type === 'WITHDRAW' ? '#fff1f0' : '#f9f0ff',
                        color: type === 'TRANSFER' ? '#1890ff' :
                              type === 'DEPOSIT' ? '#52c41a' :
                              type === 'WITHDRAW' ? '#ff4d4f' : '#722ed1',
                      }}>
                        {type === 'DEPOSIT' && <ArrowDownOutlined />} {type === 'WITHDRAW' && <ArrowUpOutlined />} {type}
                      </span>
                    ),
                  },
                  {
                    title: 'Amount',
                    dataIndex: 'amount',
                    key: 'amount',
                    render: (amount) => <span>${parseFloat(amount).toFixed(2)}</span>,
                  },
                  {
                    title: 'Status',
                    dataIndex: 'status',
                    key: 'status',
                    render: (status) => (
                      <span style={{ color: status === 'COMPLETED' ? '#52c41a' : '#ff4d4f' }}>
                        {status}
                      </span>
                    ),
                  },
                ]}
                dataSource={recentTransactions}
                rowKey="id"
                pagination={false}
              />
            )}
          </Card>
        </Col>
      </Row>

      {/* System Info */}
      <Row gutter={16} style={{ marginTop: '24px' }}>
        <Col xs={24}>
          <Card title="System Information" style={{ backgroundColor: '#fafafa' }}>
            <Row gutter={16}>
              <Col xs={24} sm={12} md={6}>
                <p><strong>API Gateway:</strong> Port 8080</p>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <p><strong>Auth Service:</strong> Port 8086</p>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <p><strong>Account Service:</strong> Port 8083</p>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <p><strong>Eureka Server:</strong> Port 8761</p>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
