import React, { useState, useEffect } from 'react';
import { Card, Table, Empty, Spin, message, Tabs } from 'antd';
import { HistoryOutlined, BarChartOutlined } from '@ant-design/icons';
import { accountService } from '../services/accountService';
import './TransactionHistory.css';

const TransactionHistory = () => {
  const [transactions, setTransactions] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [historyRes, statsRes] = await Promise.all([
        accountService.getTransactionHistory(),
        accountService.getAccountStatistics(),
      ]);
      
      setTransactions(historyRes.data.data || []);
      setStatistics(statsRes.data.data || {});
    } catch (error) {
      showErrorNotification('Lỗi tải dữ liệu', 'Không thể tải lịch sử giao dịch');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'Date',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => new Date(date).toLocaleString(),
      width: 180,
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
          {type}
        </span>
      ),
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount) => <span style={{ fontWeight: 'bold' }}>${parseFloat(amount).toFixed(2)}</span>,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <span style={{
          color: status === 'COMPLETED' ? '#52c41a' : '#ff4d4f',
        }}>
          {status}
        </span>
      ),
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
  ];

  return (
    <div className="transaction-history-container">
      <Tabs
        items={[
          {
            key: '1',
            label: <><HistoryOutlined /> Transaction History</>,
            children: (
              <Card className="history-card">
                {loading ? (
                  <Spin size="large" />
                ) : transactions.length === 0 ? (
                  <Empty description="No transactions found" />
                ) : (
                  <Table
                    columns={columns}
                    dataSource={transactions}
                    rowKey="id"
                    pagination={{ pageSize: 15 }}
                  />
                )}
              </Card>
            ),
          },
          {
            key: '2',
            label: <><BarChartOutlined /> Statistics</>,
            children: (
              <Card className="statistics-card">
                {loading ? (
                  <Spin size="large" />
                ) : !statistics ? (
                  <Empty description="No statistics available" />
                ) : (
                  <div className="stats-grid">
                    <div className="stat-item">
                      <h4>Total Balance</h4>
                      <p className="stat-value" style={{ color: '#1890ff' }}>
                        ${parseFloat(statistics.totalBalance || 0).toFixed(2)}
                      </p>
                    </div>
                    <div className="stat-item">
                      <h4>Total Accounts</h4>
                      <p className="stat-value" style={{ color: '#52c41a' }}>
                        {statistics.totalAccounts || 0}
                      </p>
                    </div>
                    <div className="stat-item">
                      <h4>Total Transactions</h4>
                      <p className="stat-value" style={{ color: '#ff4d4f' }}>
                        {statistics.totalTransactions || 0}
                      </p>
                    </div>
                  </div>
                )}
              </Card>
            ),
          },
        ]}
      />
    </div>
  );
};

export default TransactionHistory;
