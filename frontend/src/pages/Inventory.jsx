import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
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
  ShoppingCartOutlined,
} from '@ant-design/icons';
import { inventoryService } from '../../services';

const Inventory = () => {
  const [inventories, setInventories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingInventory, setEditingInventory] = useState(null);
  const [form] = Form.useForm();
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalQuantity: 0,
    reservedQuantity: 0,
  });

  const fetchInventories = async () => {
    setLoading(true);
    try {
      const response = await inventoryService.getAllInventories();
      const data = response.data;
      setInventories(data);

      // Calculate stats
      const totalQuantity = data.reduce((sum, item) => sum + (item.quantity || 0), 0);
      const reservedQuantity = data.reduce((sum, item) => sum + (item.reservedQuantity || 0), 0);

      setStats({
        totalProducts: data.length,
        totalQuantity,
        reservedQuantity,
      });
    } catch (error) {
      message.error('Failed to fetch inventory');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInventories();
  }, []);

  const handleCreate = () => {
    setEditingInventory(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingInventory(record);
    form.setFieldsValue(record);
    setIsModalVisible(true);
  };

  const handleDelete = async (inventoryId) => {
    try {
      await inventoryService.deleteInventory(inventoryId);
      message.success('Inventory deleted successfully');
      fetchInventories();
    } catch (error) {
      message.error('Failed to delete inventory');
    }
  };

  const handleSubmit = async (values) => {
    try {
      if (editingInventory) {
        await inventoryService.updateInventory(editingInventory.id, values);
        message.success('Inventory updated successfully');
      } else {
        await inventoryService.createInventory(values);
        message.success('Inventory created successfully');
      }
      setIsModalVisible(false);
      form.resetFields();
      fetchInventories();
    } catch (error) {
      message.error('Failed to save inventory');
      console.error(error);
    }
  };

  const columns = [
    {
      title: 'Product ID',
      dataIndex: 'productId',
      key: 'productId',
      width: 100,
    },
    {
      title: 'Total Quantity',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 120,
    },
    {
      title: 'Available',
      key: 'available',
      render: (_, record) => record.quantity - (record.reservedQuantity || 0),
      width: 100,
    },
    {
      title: 'Reserved',
      dataIndex: 'reservedQuantity',
      key: 'reservedQuantity',
      width: 100,
    },
    {
      title: 'Stock Level',
      key: 'stockLevel',
      render: (_, record) => {
        const percentage = (record.quantity / 100) * 100;
        let color = 'green';
        if (percentage < 30) color = 'red';
        else if (percentage < 60) color = 'orange';
        return (
          <Badge
            color={color}
            text={percentage > 70 ? 'Good' : percentage > 30 ? 'Medium' : 'Low'}
          />
        );
      },
      width: 100,
    },
    {
      title: 'Last Updated',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      render: (date) => date ? new Date(date).toLocaleString() : 'N/A',
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
            title="Delete Inventory"
            description="Are you sure you want to delete this inventory?"
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
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Total Products"
              value={stats.totalProducts}
              prefix={<ShoppingCartOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Total Quantity"
              value={stats.totalQuantity}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Reserved"
              value={stats.reservedQuantity}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Available"
              value={stats.totalQuantity - stats.reservedQuantity}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      <div style={{ marginBottom: '16px' }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          New Inventory
        </Button>
      </div>

      <Spin spinning={loading}>
        {inventories.length === 0 ? (
          <Empty description="No inventory items" />
        ) : (
          <Table columns={columns} dataSource={inventories} rowKey="id" scroll={{ x: 1200 }} />
        )}
      </Spin>

      <Modal
        title={editingInventory ? 'Edit Inventory' : 'Create New Inventory'}
        open={isModalVisible}
        onOk={() => form.submit()}
        onCancel={() => setIsModalVisible(false)}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="productId" label="Product ID" rules={[{ required: true }]}>
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item name="quantity" label="Quantity" rules={[{ required: true }]}>
            <InputNumber min={0} />
          </Form.Item>
          <Form.Item name="reservedQuantity" label="Reserved Quantity">
            <InputNumber min={0} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Inventory;
