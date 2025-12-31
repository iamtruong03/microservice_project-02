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
  Tag,
  Popconfirm,
  Empty,
  Spin,
  App,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  LockOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { permissionService } from '../services/index';
import './Permissions.css';

const PermissionsContent = () => {
  const { message: messageApi } = App.useApp();
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedPermission, setSelectedPermission] = useState(null);
  const [form] = Form.useForm();
  const [searchKeyword, setSearchKeyword] = useState('');

  // Fetch permissions
  const fetchPermissions = async (page = 0, pageSize = 10) => {
    setLoading(true);
    try {
      const response = await permissionService.getAllPermissions(page, pageSize);
      // Handle paginated response
      const permissionsArray = response.data?.data?.content || response.data?.data || [];
      setPermissions(Array.isArray(permissionsArray) ? permissionsArray : []);
      setPagination({
        current: page + 1,
        pageSize,
        total: response.data?.data?.totalElements || permissionsArray.length || 0,
      });
    } catch (error) {
      messageApi.error('Failed to fetch permissions');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // Initial load
  useEffect(() => {
    fetchPermissions(0, pagination.pageSize);
  }, []);

  // Search permissions
  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      fetchPermissions(0, pagination.pageSize);
      return;
    }

    setLoading(true);
    try {
      const response = await permissionService.searchPermissions(searchKeyword, 0, pagination.pageSize);
      // Handle paginated response
      const permissionsArray = response.data?.data?.content || response.data?.data || [];
      setPermissions(Array.isArray(permissionsArray) ? permissionsArray : []);
      setPagination({
        current: 1,
        pageSize: pagination.pageSize,
        total: response.data?.data?.totalElements || permissionsArray.length || 0,
      });
    } catch (error) {
      messageApi.error('Search failed');
    } finally {
      setLoading(false);
    }
  };

  // Create or update permission
  const handleSavePermission = async (values) => {
    try {
      if (selectedPermission) {
        await permissionService.updatePermission(selectedPermission.id, values);
        messageApi.success('Permission updated successfully');
      } else {
        await permissionService.createPermission(values);
        messageApi.success('Permission created successfully');
      }
      setIsModalVisible(false);
      form.resetFields();
      setSelectedPermission(null);
      fetchPermissions(0, pagination.pageSize);
    } catch (error) {
      messageApi.error(selectedPermission ? 'Failed to update permission' : 'Failed to create permission');
      console.error(error);
    }
  };

  // Open create modal
  const handleCreatePermission = () => {
    setSelectedPermission(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  // Open edit modal
  const handleEditPermission = (permission) => {
    setSelectedPermission(permission);
    form.setFieldsValue({
      name: permission.name,
      description: permission.description,
      resource: permission.resource,
      action: permission.action,
      isActive: permission.isActive,
    });
    setIsModalVisible(true);
  };

  // Delete permission
  const handleDeletePermission = async (id) => {
    try {
      await permissionService.deletePermission(id);
      messageApi.success('Permission deleted successfully');
      fetchPermissions(0, pagination.pageSize);
    } catch (error) {
      messageApi.error('Failed to delete permission');
      console.error(error);
    }
  };

  // Table columns
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      render: (text) => <strong>{text}</strong>,
    },
    {
      title: 'Resource',
      dataIndex: 'resource',
      key: 'resource',
      render: (resource) => (
        <Tag color="blue">{resource}</Tag>
      ),
    },
    {
      title: 'Action',
      dataIndex: 'action',
      key: 'action',
      render: (action) => (
        <Tag color="cyan">{action}</Tag>
      ),
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
      render: (description) => (
        <span>{description || '-'}</span>
      ),
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (isActive) => (
        <Tag color={isActive ? 'green' : 'red'}>
          {isActive ? 'Active' : 'Inactive'}
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
            onClick={() => handleEditPermission(record)}
          >
            Edit
          </Button>
          <Popconfirm
            title="Delete Permission"
            description="Are you sure?"
            onConfirm={() => handleDeletePermission(record.id)}
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
      {/* Header */}
      <Card style={{ marginBottom: '24px' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <h2 style={{ margin: 0 }}>
              <LockOutlined /> Permission Management
            </h2>
          </Col>
          <Col>
            <Space>
              <Button icon={<ReloadOutlined />} onClick={() => fetchPermissions(0, pagination.pageSize)}>
                Refresh
              </Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreatePermission}>
                Add Permission
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* Search */}
      <Card style={{ marginBottom: '24px' }}>
        <Input.Group compact style={{ display: 'flex' }}>
          <Input
            style={{ flex: 1 }}
            placeholder="Search permissions by name, resource, or action..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onPressEnter={handleSearch}
            prefix={<SearchOutlined />}
          />
          <Button type="primary" onClick={handleSearch} loading={loading}>
            Search
          </Button>
          <Button
            onClick={() => {
              setSearchKeyword('');
              fetchPermissions(0, pagination.pageSize);
            }}
          >
            Reset
          </Button>
        </Input.Group>
      </Card>

      {/* Permissions Table */}
      <Card loading={loading}>
        {permissions.length === 0 ? (
          <Empty description="No permissions found" />
        ) : (
          <Table
            columns={columns}
            dataSource={permissions}
            rowKey="id"
            loading={loading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              onChange: (page, pageSize) => {
                setPagination({ ...pagination, current: page, pageSize });
                fetchPermissions(page - 1, pageSize);
              },
              pageSizeOptions: ['5', '10', '15', '20', '50'],
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} permissions`,
            }}
            scroll={{ x: 1200 }}
          />
        )}
      </Card>

      {/* Create/Edit Modal */}
      <Modal
        title={selectedPermission ? 'Edit Permission' : 'Add New Permission'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          form.resetFields();
          setSelectedPermission(null);
        }}
        onOk={() => form.submit()}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSavePermission}
        >
          <Form.Item
            label="Name"
            name="name"
            rules={[{ required: true, message: 'Please enter permission name' }]}
          >
            <Input placeholder="e.g., USER_VIEW, USER_CREATE" />
          </Form.Item>

          <Form.Item
            label="Resource"
            name="resource"
            rules={[{ required: true, message: 'Please enter resource name' }]}
          >
            <Input placeholder="e.g., USER, ROLE, PERMISSION" />
          </Form.Item>

          <Form.Item
            label="Action"
            name="action"
            rules={[{ required: true, message: 'Please enter action' }]}
          >
            <Input placeholder="e.g., VIEW, CREATE, UPDATE, DELETE" />
          </Form.Item>

          <Form.Item
            label="Description"
            name="description"
          >
            <Input.TextArea placeholder="Permission description" rows={3} />
          </Form.Item>

          <Form.Item
            label="Active"
            name="isActive"
            valuePropName="checked"
            initialValue={true}
          >
            <input type="checkbox" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

const Permissions = () => {
  return (
    <App>
      <PermissionsContent />
    </App>
  );
};

export default Permissions;
