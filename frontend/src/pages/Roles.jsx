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
  Transfer,
  Tag,
  Popconfirm,
  Empty,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SafetyOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import './Roles.css';

const Roles = () => {
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedRole, setSelectedRole] = useState(null);
  const [form] = Form.useForm();
  const [selectedPermissions, setSelectedPermissions] = useState([]);

  // Mock data - Replace with API calls
  const mockRoles = [
    {
      id: 1,
      name: 'ADMIN',
      description: 'Administrator with full access',
      permissionIds: [1, 2, 3, 4, 5],
      isActive: true,
    },
    {
      id: 2,
      name: 'USER',
      description: 'Regular user with limited access',
      permissionIds: [1, 2],
      isActive: true,
    },
    {
      id: 3,
      name: 'MODERATOR',
      description: 'Moderator with content management',
      permissionIds: [1, 2, 3, 4],
      isActive: true,
    },
  ];

  const mockPermissions = [
    { id: 1, name: 'VIEW_USERS', resource: 'users', action: 'view' },
    { id: 2, name: 'CREATE_USER', resource: 'users', action: 'create' },
    { id: 3, name: 'EDIT_USER', resource: 'users', action: 'edit' },
    { id: 4, name: 'DELETE_USER', resource: 'users', action: 'delete' },
    { id: 5, name: 'MANAGE_ROLES', resource: 'roles', action: 'manage' },
  ];

  useEffect(() => {
    setRoles(mockRoles);
    setPermissions(mockPermissions);
  }, []);

  const handleCreateRole = () => {
    setSelectedRole(null);
    form.resetFields();
    setSelectedPermissions([]);
    setIsModalVisible(true);
  };

  const handleEditRole = (role) => {
    setSelectedRole(role);
    form.setFieldsValue({
      name: role.name,
      description: role.description,
      isActive: role.isActive,
    });
    setSelectedPermissions(role.permissionIds || []);
    setIsModalVisible(true);
  };

  const handleSaveRole = (values) => {
    try {
      if (selectedRole) {
        message.success('Role updated successfully');
      } else {
        message.success('Role created successfully');
      }
      setIsModalVisible(false);
      form.resetFields();
      setSelectedRole(null);
      setSelectedPermissions([]);
    } catch (error) {
      message.error(selectedRole ? 'Failed to update role' : 'Failed to create role');
    }
  };

  const handleDeleteRole = (id) => {
    try {
      setRoles(roles.filter(r => r.id !== id));
      message.success('Role deleted successfully');
    } catch (error) {
      message.error('Failed to delete role');
    }
  };

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
      render: (text) => <span style={{ fontWeight: 500 }}>{text}</span>,
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: 'Permissions',
      dataIndex: 'permissionIds',
      key: 'permissionIds',
      render: (permIds) => (
        <Space size="small" wrap>
          {permIds.map(id => {
            const perm = permissions.find(p => p.id === id);
            return perm ? (
              <Tag key={id} color="blue">
                {perm.name}
              </Tag>
            ) : null;
          })}
        </Space>
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
            onClick={() => handleEditRole(record)}
          >
            Edit
          </Button>
          <Popconfirm
            title="Delete Role"
            description="Are you sure?"
            onConfirm={() => handleDeleteRole(record.id)}
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
              <SafetyOutlined /> Role Management
            </h2>
          </Col>
          <Col>
            <Space>
              <Button icon={<ReloadOutlined />}>Refresh</Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateRole}>
                Add Role
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* Roles Table */}
      <Card loading={loading}>
        {roles.length === 0 ? (
          <Empty description="No roles found" />
        ) : (
          <Table
            columns={columns}
            dataSource={roles}
            rowKey="id"
            loading={loading}
            pagination={false}
            scroll={{ x: 1200 }}
          />
        )}
      </Card>

      {/* Create/Edit Modal */}
      <Modal
        title={selectedRole ? 'Edit Role' : 'Add New Role'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          form.resetFields();
          setSelectedRole(null);
          setSelectedPermissions([]);
        }}
        onOk={() => form.submit()}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSaveRole}
        >
          <Form.Item
            label="Role Name"
            name="name"
            rules={[{ required: true, message: 'Please enter role name' }]}
          >
            <Input placeholder="Enter role name (e.g., ADMIN, USER)" />
          </Form.Item>

          <Form.Item
            label="Description"
            name="description"
          >
            <Input.TextArea
              placeholder="Enter role description"
              rows={3}
            />
          </Form.Item>

          <Form.Item
            label="Permissions"
          >
            <Transfer
              dataSource={permissions.map(p => ({
                key: p.id.toString(),
                title: `${p.name} (${p.resource})`,
              }))}
              selectedKeys={selectedPermissions.map(id => id.toString())}
              onChange={(selected) => setSelectedPermissions(selected.map(id => parseInt(id)))}
              render={(item) => item.title}
              titles={['Available Permissions', 'Selected Permissions']}
            />
          </Form.Item>

          <Form.Item
            label="Status"
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

export default Roles;
