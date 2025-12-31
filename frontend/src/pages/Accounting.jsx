import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  message,
  Popconfirm,
  Space,
  Badge,
  Empty,
  Spin,
  Statistic,
  Row,
  Col,
  Card,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  DollarOutlined,
} from '@ant-design/icons';
import { transactionService } from '../services';

const Accounting = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);
  const [form] = Form.useForm();
  const [stats, setStats] = useState({
    totalTransactions: 0,
    totalDebit: 0,
    totalCredit: 0,
  });

  const fetchTransactions = async () => {
    setLoading(true);
    try {
      const response = await transactionService.getAllTransactions();
      const data = response.data;
      setTransactions(data);

      // Calculate stats
      const totalDebit = data
        .filter((t) => t.transactionType === 'DEBIT')
        .reduce((sum, t) => sum + (t.amount || 0), 0);
      const totalCredit = data
        .filter((t) => t.transactionType === 'CREDIT')
        .reduce((sum, t) => sum + (t.amount || 0), 0);

      setStats({
        totalTransactions: data.length,
        totalDebit,
        totalCredit,
      });
    } catch (error) {
      message.error('Failed to fetch transactions');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions();
  }, []);

  const handleCreate = () => {
    setEditingTransaction(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingTransaction(record);
    form.setFieldsValue(record);
    setIsModalVisible(true);
  };

  const handleDelete = async (transactionId) => {
    try {
      await transactionService.deleteTransaction(transactionId);
      message.success('Transaction deleted successfully');
      fetchTransactions();
    } catch (error) {
      message.error('Failed to delete transaction');
    }
  };

  const handleSubmit = async (values) => {
    try {
      if (editingTransaction) {
        await transactionService.updateTransaction(editingTransaction.id, values);
        message.success('Transaction updated successfully');
      } else {
        await transactionService.createTransaction(values);
        message.success('Transaction created successfully');
      }
      setIsModalVisible(false);
      form.resetFields();
      fetchTransactions();
    } catch (error) {
      message.error('Failed to save transaction');
      console.error(error);
    }
  };

  const columns = [
    {
      title: 'Transaction ID',
      dataIndex: 'id',
      key: 'id',
      width: 100,
    },
    {
      title: 'Order ID',
      dataIndex: 'orderId',
      key: 'orderId',
      width: 100,
    },
    {
      title: 'Customer ID',
      dataIndex: 'customerId',
      key: 'customerId',
      width: 120,
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount) => `$${amount.toFixed(2)}`,
      width: 120,
    },
    {
      title: 'Type',
      dataIndex: 'transactionType',
      key: 'transactionType',
      render: (type) => {
        const color = type === 'DEBIT' ? 'red' : 'green';
        return <Badge color={color} text={type} />;
      },
      width: 100,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const colors = {
          PENDING: 'processing',
          COMPLETED: 'success',
          FAILED: 'error',
        };
        return <Badge status={colors[status] || 'default'} text={status} />;
      },
      width: 100,
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
      width: 150,
    },
    {
      title: 'Created At',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => new Date(date).toLocaleString(),
      width: 180,
    },
    {
      title: 'Actions',
      key: 'actions',
      fixed: 'right',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button
            type="primary"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            Edit
          </Button>
          <Popconfirm
            title="Delete Transaction"
            description="Are you sure you want to delete this transaction?"
            onConfirm={() => handleDelete(record.id)}
            okText="Yes"
            cancelText="No"
          >
            <Button type="danger" size="small" icon={<DeleteOutlined />}>
              Delete
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Row gutter={16} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Total Transactions"
              value={stats.totalTransactions}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Total Debit"
              value={stats.totalDebit}
              precision={2}
              valueStyle={{ color: '#ff4d4f' }}
              prefix="$"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card>
            <Statistic
              title="Total Credit"
              value={stats.totalCredit}
              precision={2}
              valueStyle={{ color: '#52c41a' }}
              prefix="$"
            />
          </Card>
        </Col>
      </Row>

      <div style={{ marginBottom: '16px' }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          New Transaction
        </Button>
      </div>

      <Spin spinning={loading}>
        {transactions.length === 0 ? (
          <Empty description="No transactions" />
        ) : (
          <Table columns={columns} dataSource={transactions} rowKey="id" scroll={{ x: 1400 }} />
        )}
      </Spin>

      <Modal
        title={editingTransaction ? 'Edit Transaction' : 'Create New Transaction'}
        open={isModalVisible}
        onOk={() => form.submit()}
        onCancel={() => setIsModalVisible(false)}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="orderId" label="Order ID" rules={[{ required: true }]}>
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item name="customerId" label="Customer ID" rules={[{ required: true }]}>
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item name="amount" label="Amount" rules={[{ required: true }]}>
            <InputNumber min={0} step={0.01} precision={2} />
          </Form.Item>
          <Form.Item
            name="transactionType"
            label="Transaction Type"
            rules={[{ required: true }]}
          >
            <Select>
              <Select.Option value="DEBIT">Debit</Select.Option>
              <Select.Option value="CREDIT">Credit</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="Status">
            <Select>
              <Select.Option value="PENDING">Pending</Select.Option>
              <Select.Option value="COMPLETED">Completed</Select.Option>
              <Select.Option value="FAILED">Failed</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Accounting;
