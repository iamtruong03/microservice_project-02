import React, { useContext, useState } from 'react';
import { Form, Input, Button, Card, Row, Col, Alert, Spin } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import './AuthPages.css';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register, loading, error } = useContext(AuthContext);
  const [form] = Form.useForm();
  const [localError, setLocalError] = useState(null);

  const onFinish = async (values) => {
    setLocalError(null);

    const userData = {
      username: values.username,
      fullName: values.fullName,
      email: values.email,
      password: values.password,
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
                  name="username"
                  label="Username"
                  rules={[
                    { required: true, message: 'Vui lòng nhập username' },
                    { min: 3, message: 'Username phải có ít nhất 3 ký tự' },
                  ]}
                >
                  <Input prefix={<UserOutlined />} placeholder="Nhập username" />
                </Form.Item>

                <Form.Item
                  name="fullName"
                  label="Họ và Tên"
                  rules={[
                    { required: true, message: 'Vui lòng nhập họ và tên' },
                    { min: 2, message: 'Họ và tên phải có ít nhất 2 ký tự' },
                  ]}
                >
                  <Input placeholder="Nhập họ và tên" />
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
