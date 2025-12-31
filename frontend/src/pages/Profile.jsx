import React, { useState, useEffect } from 'react';
import {
  Card,
  Descriptions,
  Avatar,
  Button,
  Space,
  Row,
  Col,
  Modal,
  Form,
  Input,
  App,
  Divider,
  Statistic,
  Tag,
} from 'antd';
import {
  UserOutlined,
  EditOutlined,
  SaveOutlined,
  CloseOutlined,
  MailOutlined,
  PhoneOutlined,
} from '@ant-design/icons';
import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { userService } from '../services/index';
import './Profile.css';

const ProfileContent = () => {
  const { message: messageApi } = App.useApp();
  const { user: authUser } = useContext(AuthContext);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);
  const [form] = Form.useForm();

  // Fetch full user details from API
  useEffect(() => {
    if (authUser?.id) {
      fetchUserDetails();
    }
  }, [authUser]);

  const fetchUserDetails = async () => {
    setLoading(true);
    try {
      const response = await userService.getUserById(authUser.id);
      const userData = response.data?.data || response.data;
      setUser(userData);
    } catch (error) {
      messageApi.error('Failed to load user details');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleEditClick = () => {
    if (user) {
      form.setFieldsValue({
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phoneNumber: user.phoneNumber,
        city: user.city,
        occupation: user.occupation,
      });
      setIsEditModalVisible(true);
    }
  };

  const handleSaveProfile = async (values) => {
    try {
      const updateData = {
        firstName: values.firstName,
        lastName: values.lastName,
        email: values.email,
        phoneNumber: values.phoneNumber,
        city: values.city,
        occupation: values.occupation,
      };

      await userService.updateUser(authUser.id, updateData);
      messageApi.success('Profile updated successfully');
      setIsEditModalVisible(false);
      fetchUserDetails();
    } catch (error) {
      messageApi.error('Failed to update profile');
      console.error(error);
    }
  };

  if (loading) {
    return <div style={{ padding: '24px' }}>Loading...</div>;
  }

  if (!user) {
    return <div style={{ padding: '24px' }}>No user data available</div>;
  }

  const fullName = `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.userName;

  return (
    <div style={{ padding: '24px' }}>
      {/* Header with Avatar */}
      <Card style={{ marginBottom: '24px' }}>
        <Row gutter={24} align="middle">
          <Col xs={24} sm={4} style={{ textAlign: 'center' }}>
            <Avatar
              size={120}
              icon={<UserOutlined />}
              style={{ backgroundColor: '#1890ff' }}
            >
              {fullName.charAt(0).toUpperCase()}
            </Avatar>
          </Col>
          <Col xs={24} sm={20}>
            <h1 style={{ margin: '0 0 8px 0' }}>{fullName}</h1>
            <p style={{ margin: '0 0 16px 0', color: '#666' }}>
              <Tag color="blue">{user.userName}</Tag>
              <Tag color={user.status === 'ACTIVE' ? 'green' : 'red'} style={{ marginLeft: '8px' }}>
                {user.status || 'ACTIVE'}
              </Tag>
            </p>
            <Space>
              <Button
                type="primary"
                icon={<EditOutlined />}
                onClick={handleEditClick}
              >
                Edit Profile
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* User Details */}
      <Card title="Personal Information" style={{ marginBottom: '24px' }}>
        <Descriptions bordered column={1}>
          <Descriptions.Item label="User ID">
            <Tag color="default">{user.id}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Username">
            {user.userName}
          </Descriptions.Item>
          <Descriptions.Item label="First Name">
            {user.firstName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Last Name">
            {user.lastName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label={<><MailOutlined /> Email</>}>
            <a href={`mailto:${user.email}`}>{user.email}</a>
          </Descriptions.Item>
          <Descriptions.Item label={<><PhoneOutlined /> Phone</>}>
            {user.phoneNumber || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="City">
            {user.city || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Occupation">
            {user.occupation || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Status">
            <Tag color={user.status === 'ACTIVE' ? 'green' : 'red'}>
              {user.status || 'ACTIVE'}
            </Tag>
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {/* Account Information */}
      <Card title="Account Information">
        <Row gutter={16}>
          <Col xs={24} sm={8}>
            <Statistic
              title="Account Age"
              value="Active"
              valueStyle={{ color: '#52c41a' }}
            />
          </Col>
          <Col xs={24} sm={8}>
            <Statistic
              title="Last Login"
              value="Today"
              valueStyle={{ color: '#1890ff' }}
            />
          </Col>
          <Col xs={24} sm={8}>
            <Statistic
              title="Account Status"
              value={user.status || 'ACTIVE'}
              valueStyle={{ color: user.status === 'ACTIVE' ? '#52c41a' : '#ff4d4f' }}
            />
          </Col>
        </Row>
      </Card>

      {/* Edit Profile Modal */}
      <Modal
        title="Edit Profile"
        open={isEditModalVisible}
        onCancel={() => setIsEditModalVisible(false)}
        onOk={() => form.submit()}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSaveProfile}
        >
          <Form.Item
            label="First Name"
            name="firstName"
          >
            <Input placeholder="Enter first name" />
          </Form.Item>

          <Form.Item
            label="Last Name"
            name="lastName"
          >
            <Input placeholder="Enter last name" />
          </Form.Item>

          <Form.Item
            label="Email"
            name="email"
            rules={[
              { type: 'email', message: 'Invalid email format' },
            ]}
          >
            <Input placeholder="Enter email" type="email" />
          </Form.Item>

          <Form.Item
            label="Phone Number"
            name="phoneNumber"
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
        </Form>
      </Modal>
    </div>
  );
};

const Profile = () => {
  return (
    <App>
      <ProfileContent />
    </App>
  );
};

export default Profile;
