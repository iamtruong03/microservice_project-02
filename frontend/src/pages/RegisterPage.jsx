import React, { useContext, useState } from 'react';
import { Form, Input, Button, Card, Row, Col, Alert, Spin, Select, DatePicker } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import dayjs from 'dayjs';
import './AuthPages.css';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register, loading, error } = useContext(AuthContext);
  const [form] = Form.useForm();
  const [localError, setLocalError] = useState(null);

  const onFinish = async (values) => {
    setLocalError(null);

    const userData = {
      firstName: values.firstName,
      lastName: values.lastName,
      email: values.email,
      password: values.password,
      phoneNumber: values.phoneNumber,
      dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : null,
      gender: values.gender || 'OTHER',
    };

    const result = await register(userData);

    if (result.success) {
      navigate('/');
    } else {
      setLocalError(result.error);
    }
  };

  return (
    <div className="auth-container">
      <Row justify="center" style={{ minHeight: '100vh', paddingTop: '40px', paddingBottom: '40px' }}>
        <Col xs={22} sm={20} md={12} lg={8}>
          <Card
            title="Đăng Ký"
            className="auth-card"
            bordered={false}
          >
            {(error || localError) && (
              <Alert
                message="Đăng ký thất bại"
                description={error || localError}
                type="error"
                showIcon
                closable
                style={{ marginBottom: '20px' }}
              />
            )}

            <Spin spinning={loading}>
              <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
                autoComplete="off"
              >
                <Form.Item
                  name="firstName"
                  label="Tên"
                  rules={[
                    { required: true, message: 'Vui lòng nhập tên' },
                    { min: 2, message: 'Tên phải có ít nhất 2 ký tự' },
                  ]}
                >
                  <Input placeholder="Nhập tên của bạn" />
                </Form.Item>

                <Form.Item
                  name="lastName"
                  label="Họ"
                  rules={[
                    { required: true, message: 'Vui lòng nhập họ' },
                    { min: 2, message: 'Họ phải có ít nhất 2 ký tự' },
                  ]}
                >
                  <Input placeholder="Nhập họ của bạn" />
                </Form.Item>

                <Form.Item
                  name="email"
                  label="Email"
                  rules={[
                    { required: true, message: 'Vui lòng nhập email' },
                    { type: 'email', message: 'Email không hợp lệ' },
                  ]}
                >
                  <Input
                    prefix={<MailOutlined />}
                    placeholder="Nhập email"
                  />
                </Form.Item>

                <Form.Item
                  name="phoneNumber"
                  label="Số Điện Thoại"
                  rules={[
                    { required: true, message: 'Vui lòng nhập số điện thoại' },
                    {
                      pattern: /^[0-9+\-\s()]*$/,
                      message: 'Số điện thoại không hợp lệ',
                    },
                  ]}
                >
                  <Input
                    prefix={<PhoneOutlined />}
                    placeholder="Nhập số điện thoại"
                  />
                </Form.Item>

                <Form.Item
                  name="dateOfBirth"
                  label="Ngày Sinh"
                  rules={[
                    { required: true, message: 'Vui lòng chọn ngày sinh' },
                  ]}
                >
                  <DatePicker
                    style={{ width: '100%' }}
                    placeholder="Chọn ngày sinh"
                    format="DD/MM/YYYY"
                  />
                </Form.Item>

                <Form.Item
                  name="gender"
                  label="Giới Tính"
                >
                  <Select placeholder="Chọn giới tính">
                    <Select.Option value="MALE">Nam</Select.Option>
                    <Select.Option value="FEMALE">Nữ</Select.Option>
                    <Select.Option value="OTHER">Khác</Select.Option>
                  </Select>
                </Form.Item>

                <Form.Item
                  name="password"
                  label="Mật Khẩu"
                  rules={[
                    { required: true, message: 'Vui lòng nhập mật khẩu' },
                    {
                      min: 6,
                      message: 'Mật khẩu phải có ít nhất 6 ký tự',
                    },
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="Nhập mật khẩu"
                  />
                </Form.Item>

                <Form.Item
                  name="confirmPassword"
                  label="Xác Nhận Mật Khẩu"
                  dependencies={['password']}
                  rules={[
                    { required: true, message: 'Vui lòng xác nhận mật khẩu' },
                    ({ getFieldValue }) => ({
                      validator(_, value) {
                        if (!value || getFieldValue('password') === value) {
                          return Promise.resolve();
                        }
                        return Promise.reject(
                          new Error('Mật khẩu không khớp')
                        );
                      },
                    }),
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="Xác nhận mật khẩu"
                  />
                </Form.Item>

                <Form.Item>
                  <Button
                    type="primary"
                    htmlType="submit"
                    block
                    size="large"
                    loading={loading}
                  >
                    Đăng Ký
                  </Button>
                </Form.Item>
              </Form>

              <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <p>
                  Đã có tài khoản?{' '}
                  <Link to="/login" style={{ color: '#1890ff' }}>
                    Đăng nhập
                  </Link>
                </p>
              </div>
            </Spin>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default RegisterPage;
