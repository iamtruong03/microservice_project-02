import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  Space,
  message,
  Card,
  Row,
  Col,
  Pagination,
  Select,
  Popconfirm,
  Tag,
  Avatar,
  Tooltip,
  Drawer,
  Divider,
  Statistic,
  Empty,
  App,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  UserAddOutlined,
  MailOutlined,
  PhoneOutlined,
  EnvironmentOutlined,
  ShopOutlined,
} from '@ant-design/icons';
import { userService } from '../services/index';
import './Users.css';

const UsersContent = () => {
  const { message: messageApi } = App.useApp();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isDrawerVisible, setIsDrawerVisible] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [sortBy, setSortBy] = useState('id');
  const [sortDir, setSortDir] = useState('asc');
  const [stats, setStats] = useState({
    total: 0,
    active: 0,
    inactive: 0,
  });

  // Fetch users with pagination
  const fetchUsers = async (page = 1, pageSize = 10) => {
    setLoading(true);
    try {
      const response = await userService.getUsersPaged(
        page - 1,
        pageSize,
        sortBy,
        sortDir
      );
      // Handle paginated response
      const usersArray = response.data?.data?.content || response.data?.data || response.content || [];
      setUsers(Array.isArray(usersArray) ? usersArray : []);
      setPagination({
        current: page,
        pageSize,
        total: response.data?.data?.totalElements || response.totalElements || usersArray.length || 0,
      });
      // Update stats
      setStats({
        total: response.data?.data?.totalElements || response.totalElements || usersArray.length || 0,
        active: usersArray?.filter(u => u.status === 'ACTIVE').length || 0,
        inactive: usersArray?.filter(u => u.status === 'INACTIVE').length || 0,
      });
    } catch (error) {
      messageApi.error('Failed to load users');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // Initial load and refresh
  useEffect(() => {
    fetchUsers(pagination.current, pagination.pageSize);
  }, [sortBy, sortDir]);

  // Search users
  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      fetchUsers(1, pagination.pageSize);
      return;
    }

    setLoading(true);
    try {
      const response = await userService.searchUsers(
        searchKeyword,
        0,
        pagination.pageSize,
        sortBy,
        sortDir
      );
      // Handle paginated response
      const usersArray = response.data?.data?.content || response.data?.data || response.content || [];
      setUsers(Array.isArray(usersArray) ? usersArray : []);
      setPagination({
        current: 1,
        pageSize: pagination.pageSize,
        total: response.data?.data?.totalElements || response.totalElements || usersArray.length || 0,
      });
    } catch (error) {
      messageApi.error('Search failed');
    } finally {
      setLoading(false);
    }
  };

  // Create or update user
  const handleSaveUser = async (values) => {
    try {
      if (selectedUser) {
        await userService.updateUser(selectedUser.id, values);
        messageApi.success('User updated successfully');
      } else {
        await userService.createUser(values);
        messageApi.success('User created successfully');
      }
      setIsModalVisible(false);
      form.resetFields();
      setSelectedUser(null);
      fetchUsers(1, pagination.pageSize);
    } catch (error) {
      messageApi.error(selectedUser ? 'Failed to update user' : 'Failed to create user');
    }
  };

  // Open create modal
  const handleCreateUser = () => {
    setSelectedUser(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  // Open edit modal
  const handleEditUser = (user) => {
    setSelectedUser(user);
    form.setFieldsValue({
      name: user.name,
      email: user.email,
      phone: user.phone,
      city: user.city,
      occupation: user.occupation,
      status: user.status || 'ACTIVE',
    });
    setIsModalVisible(true);
  };

  // View user details
  const handleViewUser = (user) => {
    setSelectedUser(user);
    setIsDrawerVisible(true);
  };

  // Delete user
  const handleDeleteUser = async (id) => {
    try {
      await userService.deleteUser(id);
      messageApi.success('User deleted successfully');
      fetchUsers(pagination.current, pagination.pageSize);
    } catch (error) {
      messageApi.error('Failed to delete user');
    }
  };

  // Table columns
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
      sorter: true,
    },
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <span style={{ fontWeight: 500, cursor: 'pointer' }} onClick={() => handleViewUser(record)}>
          {text}
        </span>
      ),
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      render: (email) => (
        <Tooltip title={email}>
          <span>{email.length > 25 ? email.substring(0, 25) + '...' : email}</span>
        </Tooltip>
      ),
    },
    {
      title: 'Phone',
      dataIndex: 'phone',
      key: 'phone',
      render: (phone) => phone || '-',
    },
    {
      title: 'City',
      dataIndex: 'city',
      key: 'city',
      render: (city) => city || '-',
    },
    {
      title: 'Occupation',
      dataIndex: 'occupation',
      key: 'occupation',
      render: (occupation) => occupation || '-',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={status === 'ACTIVE' ? 'green' : 'red'}>
          {status || 'ACTIVE'}
        </Tag>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 150,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="primary"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEditUser(record)}
          >
            Edit
          </Button>
          <Popconfirm
            title="Delete User"
            description="Are you sure you want to delete this user?"
            onConfirm={() => handleDeleteUser(record.id)}
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
      {/* Statistics */}
      <Row gutter={16} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Total Users"
              value={stats.total}
              prefix={<UserAddOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Active"
              value={stats.active}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="Inactive"
              value={stats.inactive}
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
      </Row>

      {/* Search and Create */}
      <Card style={{ marginBottom: '24px' }}>
        <Row gutter={16} style={{ marginBottom: '16px' }}>
          <Col xs={24} sm={16}>
            <Input.Group compact style={{ display: 'flex' }}>
              <Input
                style={{ flex: 1 }}
                placeholder="Search users by name, email, city, or occupation..."
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                onPressEnter={handleSearch}
                prefix={<SearchOutlined />}
              />
              <Button type="primary" onClick={handleSearch} loading={loading}>
                Search
              </Button>
            </Input.Group>
          </Col>
          <Col xs={24} sm={8} style={{ textAlign: 'right' }}>
            <Space>
              <Button
                icon={<ReloadOutlined />}
                onClick={() => {
                  setSearchKeyword('');
                  fetchUsers(1, pagination.pageSize);
                }}
              >
                Reset
              </Button>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleCreateUser}
              >
                Add User
              </Button>
            </Space>
          </Col>
        </Row>

        {/* Sort Options */}
        <Row gutter={16}>
          <Col xs={24} sm={12}>
            <Select
              style={{ width: '100%' }}
              placeholder="Sort by..."
              value={sortBy}
              onChange={setSortBy}
              options={[
                { label: 'ID', value: 'id' },
                { label: 'Name', value: 'name' },
                { label: 'Email', value: 'email' },
              ]}
            />
          </Col>
          <Col xs={24} sm={12}>
            <Select
              style={{ width: '100%' }}
              placeholder="Sort direction..."
              value={sortDir}
              onChange={setSortDir}
              options={[
                { label: 'Ascending', value: 'asc' },
                { label: 'Descending', value: 'desc' },
              ]}
            />
          </Col>
        </Row>
      </Card>

      {/* Users Table */}
      <Card loading={loading}>
        {users.length === 0 ? (
          <Empty description="No users found" />
        ) : (
          <>
            <Table
              columns={columns}
              dataSource={users}
              rowKey="id"
              loading={loading}
              pagination={false}
              scroll={{ x: 1200 }}
            />
            <Pagination
              style={{ marginTop: '16px', textAlign: 'right' }}
              current={pagination.current}
              pageSize={pagination.pageSize}
              total={pagination.total}
              onChange={(page, pageSize) => {
                setPagination({ ...pagination, current: page, pageSize });
                fetchUsers(page, pageSize);
              }}
              pageSizeOptions={['5', '10', '15', '20', '50']}
              showSizeChanger
              showTotal={(total) => `Total ${total} users`}
            />
          </>
        )}
      </Card>

      {/* Create/Edit Modal */}
      <Modal
        title={selectedUser ? 'Edit User' : 'Add New User'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          form.resetFields();
          setSelectedUser(null);
        }}
        onOk={() => form.submit()}
        width={500}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSaveUser}
        >
          <Form.Item
            label="Name"
            name="name"
            rules={[{ required: true, message: 'Please enter name' }]}
          >
            <Input placeholder="Enter full name" />
          </Form.Item>

          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: 'Please enter email' },
              { type: 'email', message: 'Invalid email format' },
            ]}
          >
            <Input placeholder="Enter email" type="email" />
          </Form.Item>

          <Form.Item
            label="Password"
            name="password"
            rules={[
              { required: !selectedUser, message: 'Please enter password' },
              { min: 6, message: 'Password must be at least 6 characters' },
            ]}
          >
            <Input.Password placeholder="Enter password" />
          </Form.Item>

          <Form.Item
            label="Phone"
            name="phone"
          >
            <Input placeholder="Enter phone number" />
          </Form.Item>

          <Form.Item
            label="City"
            name="city"
          >
            <Input placeholder="Enter city" />
          </Form.Item>

          <Form.Item
            label="Occupation"
            name="occupation"
          >
            <Input placeholder="Enter occupation" />
          </Form.Item>

          <Form.Item
            label="Status"
            name="status"
            initialValue="ACTIVE"
          >
            <Select
              options={[
                { label: 'Active', value: 'ACTIVE' },
                { label: 'Inactive', value: 'INACTIVE' },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* User Details Drawer */}
      <Drawer
        title="User Details"
        onClose={() => setIsDrawerVisible(false)}
        open={isDrawerVisible}
        width={400}
      >
        {selectedUser && (
          <div>
            <Row gutter={16} style={{ marginBottom: '16px' }}>
              <Col xs={24} style={{ textAlign: 'center' }}>
                <Avatar
                  size={64}
                  style={{ backgroundColor: '#87d068' }}
                >
                  {selectedUser.name?.charAt(0).toUpperCase()}
                </Avatar>
              </Col>
            </Row>

            <Divider />

            <Space direction="vertical" style={{ width: '100%' }}>
              <div>
                <strong>ID:</strong> {selectedUser.id}
              </div>
              <div>
                <strong>Name:</strong> {selectedUser.name}
              </div>
              <div>
                <MailOutlined /> <strong>Email:</strong> {selectedUser.email}
              </div>
              {selectedUser.phone && (
                <div>
                  <PhoneOutlined /> <strong>Phone:</strong> {selectedUser.phone}
                </div>
              )}
              {selectedUser.city && (
                <div>
                  <EnvironmentOutlined /> <strong>City:</strong> {selectedUser.city}
                </div>
              )}
              {selectedUser.occupation && (
                <div>
                  <ShopOutlined /> <strong>Occupation:</strong> {selectedUser.occupation}
                </div>
              )}
              <div>
                <strong>Status:</strong>{' '}
                <Tag color={selectedUser.status === 'ACTIVE' ? 'green' : 'red'}>
                  {selectedUser.status || 'ACTIVE'}
                </Tag>
              </div>
            </Space>

            <Divider />

            <Space style={{ width: '100%' }}>
              <Button
                type="primary"
                icon={<EditOutlined />}
                onClick={() => {
                  setIsDrawerVisible(false);
                  handleEditUser(selectedUser);
                }}
                block
              >
                Edit
              </Button>
              <Popconfirm
                title="Delete User"
                description="Are you sure?"
                onConfirm={() => {
                  setIsDrawerVisible(false);
                  handleDeleteUser(selectedUser.id);
                }}
              >
                <Button type="danger" icon={<DeleteOutlined />} block>
                  Delete
                </Button>
              </Popconfirm>
            </Space>
          </div>
        )}
      </Drawer>
    </div>
  );
};

const Users = () => {
  return (
    <App>
      <UsersContent />
    </App>
  );
};

export default Users;
