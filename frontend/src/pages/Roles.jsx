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
  Select,
  Tag,
  Popconfirm,
  Empty,
  Spin,
  Checkbox,
  App,
  Tabs,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SafetyOutlined,
  ReloadOutlined,
  LockOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { roleService, permissionService } from '../services/index';
import './Roles.css';

const RolesContent = () => {
  const { message: messageApi } = App.useApp();
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [permissionsLoading, setPermissionsLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isPermissionModalVisible, setIsPermissionModalVisible] = useState(false);
  const [selectedRole, setSelectedRole] = useState(null);
  const [selectedPermission, setSelectedPermission] = useState(null);
  const [form] = Form.useForm();
  const [permissionForm] = Form.useForm();
  const [selectedPermissions, setSelectedPermissions] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [roleSearchKeyword, setRoleSearchKeyword] = useState('');

  // Fetch roles and permissions
  const fetchRoles = async () => {
    setLoading(true);
    try {
      const response = await roleService.getAllRoles();
      // API returns paginated response with content array
      const rolesArray = response.data?.data?.content || response.data?.data || [];
      setRoles(Array.isArray(rolesArray) ? rolesArray : []);
    } catch (error) {
      messageApi.error('Failed to fetch roles');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const fetchPermissions = async (page = 0, pageSize = 10) => {
    setPermissionsLoading(true);
    try {
      const response = await permissionService.getAllPermissions(page, pageSize);
      // API returns paginated response with content array
      const permissionsArray = response.data?.data?.content || response.data?.data || [];
      setPermissions(Array.isArray(permissionsArray) ? permissionsArray : []);
    } catch (error) {
      messageApi.error('Failed to fetch permissions');
      console.error(error);
    } finally {
      setPermissionsLoading(false);
    }
  };

  // Search roles
  const handleSearchRoles = async () => {
    if (!roleSearchKeyword.trim()) {
      fetchRoles();
      return;
    }

    setLoading(true);
    try {
      const response = await roleService.searchRoles(roleSearchKeyword, 0, 10);
      // Handle paginated response
      const rolesArray = response.data?.data?.content || response.data?.data || [];
      setRoles(Array.isArray(rolesArray) ? rolesArray : []);
    } catch (error) {
      messageApi.error('Search failed');
    } finally {
      setLoading(false);
    }
  };

  // Search permissions
  const handleSearchPermissions = async () => {
    if (!searchKeyword.trim()) {
      fetchPermissions(0, 10);
      return;
    }

    setPermissionsLoading(true);
    try {
      const response = await permissionService.searchPermissions(searchKeyword, 0, 10);
      // Handle paginated response
      const permissionsArray = response.data?.data?.content || response.data?.data || [];
      setPermissions(Array.isArray(permissionsArray) ? permissionsArray : []);
    } catch (error) {
      messageApi.error('Search failed');
    } finally {
      setPermissionsLoading(false);
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
      setIsPermissionModalVisible(false);
      permissionForm.resetFields();
      setSelectedPermission(null);
      fetchPermissions(0, 10);
    } catch (error) {
      messageApi.error(selectedPermission ? 'Failed to update permission' : 'Failed to create permission');
      console.error(error);
    }
  };

  // Open create permission modal
  const handleCreatePermission = () => {
    setSelectedPermission(null);
    permissionForm.resetFields();
    setIsPermissionModalVisible(true);
  };

  // Open edit permission modal
  const handleEditPermission = (permission) => {
    setSelectedPermission(permission);
    permissionForm.setFieldsValue({
      name: permission.name,
      description: permission.description,
      resource: permission.resource,
      action: permission.action,
      isActive: permission.isActive,
    });
    setIsPermissionModalVisible(true);
  };

  // Delete permission
  const handleDeletePermission = async (id) => {
    try {
      await permissionService.deletePermission(id);
      messageApi.success('Permission deleted successfully');
      fetchPermissions(0, 10);
    } catch (error) {
      messageApi.error('Failed to delete permission');
      console.error(error);
    }
  };

  useEffect(() => {
    fetchRoles();
    fetchPermissions();
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

  const handleSaveRole = async (values) => {
    try {
      const roleData = {
        name: values.name,
        description: values.description,
        isActive: values.isActive !== false,
        permissionIds: selectedPermissions,
      };

      if (selectedRole) {
        await roleService.updateRole(selectedRole.id, roleData);
        messageApi.success('Role updated successfully');
      } else {
        await roleService.createRole(roleData);
        messageApi.success('Role created successfully');
      }
      
      setIsModalVisible(false);
      form.resetFields();
      setSelectedRole(null);
      setSelectedPermissions([]);
      fetchRoles();
    } catch (error) {
      messageApi.error(selectedRole ? 'Failed to update role' : 'Failed to create role');
      console.error(error);
    }
  };

  const handleDeleteRole = async (id) => {
    try {
      await roleService.deleteRole(id);
      messageApi.success('Role deleted successfully');
      fetchRoles();
    } catch (error) {
      messageApi.error('Failed to delete role');
      console.error(error);
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
      title: 'Permissions',
      dataIndex: 'permissionIds',
      key: 'permissionIds',
      render: (permIds) => (
        <Space size="small" wrap>
          {(permIds || []).map(id => {
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
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
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

  // Permission columns
  const permissionColumns = [
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
              <SafetyOutlined /> Role & Permission Management
            </h2>
          </Col>
        </Row>
      </Card>

      {/* Tabs for Roles and Permissions */}
      <Card>
        <Tabs
          defaultActiveKey="roles"
          items={[
            {
              key: 'roles',
              label: 'Roles',
              icon: <SafetyOutlined />,
              children: (
                <div>
                  <Row gutter={16} style={{ marginBottom: '16px' }}>
                    <Col xs={24} sm={16}>
                      <Input.Group compact style={{ display: 'flex' }}>
                        <Input
                          style={{ flex: 1 }}
                          placeholder="Search roles by name or description..."
                          value={roleSearchKeyword}
                          onChange={(e) => setRoleSearchKeyword(e.target.value)}
                          onPressEnter={handleSearchRoles}
                          prefix={<SearchOutlined />}
                        />
                        <Button type="primary" onClick={handleSearchRoles} loading={loading}>
                          Search
                        </Button>
                      </Input.Group>
                    </Col>
                    <Col xs={24} sm={8} style={{ textAlign: 'right' }}>
                      <Space>
                        <Button icon={<ReloadOutlined />} onClick={() => {
                          setRoleSearchKeyword('');
                          fetchRoles();
                        }}>
                          Refresh
                        </Button>
                        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateRole}>
                          Add Role
                        </Button>
                      </Space>
                    </Col>
                  </Row>

                  {/* Roles Table */}
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
                </div>
              ),
            },
            {
              key: 'permissions',
              label: 'Permissions',
              icon: <LockOutlined />,
              children: (
                <div>
                  <Row gutter={16} style={{ marginBottom: '16px' }}>
                    <Col xs={24} sm={16}>
                      <Input.Group compact style={{ display: 'flex' }}>
                        <Input
                          style={{ flex: 1 }}
                          placeholder="Search permissions by name, resource, or action..."
                          value={searchKeyword}
                          onChange={(e) => setSearchKeyword(e.target.value)}
                          onPressEnter={handleSearchPermissions}
                          prefix={<SearchOutlined />}
                        />
                        <Button type="primary" onClick={handleSearchPermissions} loading={permissionsLoading}>
                          Search
                        </Button>
                      </Input.Group>
                    </Col>
                    <Col xs={24} sm={8} style={{ textAlign: 'right' }}>
                      <Space>
                        <Button icon={<ReloadOutlined />} onClick={() => {
                          setRoleSearchKeyword('');
                          fetchPermissions();
                        }}>
                          Refresh
                        </Button>
                        <Button
                          type="primary"
                          icon={<PlusOutlined />}
                          onClick={handleCreatePermission}
                        >
                          Add Permission
                        </Button>
                      </Space>
                    </Col>
                  </Row>

                  {/* Permissions Table */}
                  {permissions.length === 0 ? (
                    <Empty description="No permissions found" />
                  ) : (
                    <Table
                      columns={permissionColumns}
                      dataSource={permissions}
                      rowKey="id"
                      loading={permissionsLoading}
                      pagination={false}
                      scroll={{ x: 1200 }}
                    />
                  )}
                </div>
              ),
            },
          ]}
        />
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
            <Select
              mode="multiple"
              placeholder="Select permissions"
              value={selectedPermissions}
              onChange={setSelectedPermissions}
              style={{ width: '100%' }}
              options={permissions.map(p => ({
                label: `${p.name} (${p.resource})`,
                value: p.id,
              }))}
              optionLabelProp="label"
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

      {/* Create/Edit Permission Modal */}
      <Modal
        title={selectedPermission ? 'Edit Permission' : 'Add New Permission'}
        open={isPermissionModalVisible}
        onCancel={() => {
          setIsPermissionModalVisible(false);
          permissionForm.resetFields();
          setSelectedPermission(null);
        }}
        onOk={() => permissionForm.submit()}
        width={600}
      >
        <Form
          form={permissionForm}
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

const Roles = () => {
  return (
    <App>
      <RolesContent />
    </App>
  );
};

export default Roles;
