import React, { useState, useEffect } from 'react';
import { Card, Button, Table, Empty, Spin, Modal, Form, Input, Select, message, Space } from 'antd';
import { PlusOutlined, DollarOutlined, HistoryOutlined, BarChartOutlined } from '@ant-design/icons';
import { accountService } from '../services/accountService';
import './Accounts.css';

const Accounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const response = await accountService.getUserAccounts();
      setAccounts(response.data.data || []);
    } catch (error) {
      message.error('Failed to fetch accounts');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAccount = async (values) => {
    try {
      await accountService.createBankAccount(values.accountType);
      message.success('Bank account created successfully');
      setIsModalVisible(false);
      form.resetFields();
      fetchAccounts();
    } catch (error) {
      message.error('Failed to create account');
      console.error(error);
    }
  };

  const columns = [
    {
      title: 'Account Number',
      dataIndex: 'accountNumber',
      key: 'accountNumber',
      render: (text) => <span style={{ fontWeight: 'bold' }}>{text}</span>,
    },
    {
      title: 'Account Type',
      dataIndex: 'accountType',
      key: 'accountType',
    },
    {
      title: 'Balance',
      dataIndex: 'balance',
      key: 'balance',
      render: (balance) => <span style={{ color: '#52c41a', fontWeight: 'bold' }}>
        ${parseFloat(balance).toFixed(2)}
      </span>,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <span style={{
          padding: '4px 8px',
          borderRadius: '4px',
          backgroundColor: status === 'ACTIVE' ? '#f6ffed' : '#fff1f0',
          color: status === 'ACTIVE' ? '#52c41a' : '#ff4d4f',
        }}>
          {status}
        </span>
      ),
    },
    {
      title: 'Created',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => new Date(date).toLocaleDateString(),
    },
  ];

  return (
    <div className="accounts-container">
      <Card className="accounts-card">
        <div className="accounts-header">
          <h2>My Bank Accounts</h2>
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            onClick={() => setIsModalVisible(true)}
          >
            Create Account
          </Button>
        </div>

        {loading ? (
          <Spin size="large" />
        ) : accounts.length === 0 ? (
          <Empty description="No accounts found" />
        ) : (
          <Table 
            columns={columns} 
            dataSource={accounts}
            rowKey="id"
            pagination={{ pageSize: 10 }}
          />
        )}
      </Card>

      {/* Create Account Modal */}
      <Modal
        title="Create New Bank Account"
        open={isModalVisible}
        onOk={() => form.submit()}
        onCancel={() => {
          setIsModalVisible(false);
          form.resetFields();
        }}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleCreateAccount}
        >
          <Form.Item
            label="Account Type"
            name="accountType"
            initialValue="SAVINGS"
            rules={[{ required: true, message: 'Please select account type' }]}
          >
            <Select>
              <Select.Option value="SAVINGS">Savings</Select.Option>
              <Select.Option value="CHECKING">Checking</Select.Option>
              <Select.Option value="INVESTMENT">Investment</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Accounts;
