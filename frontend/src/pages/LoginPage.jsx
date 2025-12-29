import React, { useContext, useState } from 'react';
import { Form, Input, Button, Card, Row, Col, Alert, Spin, Checkbox } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import './AuthPages.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login, loading, error } = useContext(AuthContext);
  const [form] = Form.useForm();
  const [localError, setLocalError] = useState(null);

  const onFinish = async (values) => {
    setLocalError(null);
    const result = await login(values.email, values.password);

    if (result.success) {
      navigate('/');
    } else {
      setLocalError(result.error);
    }
  };

  return (
    <div className="auth-container">
      <Row justify="center" align="middle" style={{ minHeight: '100vh' }}>
        <Col xs={22} sm={20} md={12} lg={8}>
          <Card
            title="Đăng Nhập"
            className="auth-card"
            bordered={false}
          >
            {(error || localError) && (
              <Alert
                message="Đăng nhập thất bại"
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
                  name="email"
                  label="Email"
                  rules={[
                    { required: true, message: 'Vui lòng nhập email' },
                    { type: 'email', message: 'Email không hợp lệ' },
                  ]}
                >
                  <Input
                    prefix={<UserOutlined />}
                    placeholder="Nhập email của bạn"
                    size="large"
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  label="Mật Khẩu"
                  rules={[
                    { required: true, message: 'Vui lòng nhập mật khẩu' },
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined />}
                    placeholder="Nhập mật khẩu"
                    size="large"
                  />
                </Form.Item>

                <Form.Item name="remember" valuePropName="checked">
                  <Checkbox>Nhớ mật khẩu</Checkbox>
                </Form.Item>

                <Form.Item>
                  <Button
                    type="primary"
                    htmlType="submit"
                    block
                    size="large"
                    loading={loading}
                  >
                    Đăng Nhập
                  </Button>
                </Form.Item>
              </Form>

              <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <p>
                  Chưa có tài khoản?{' '}
                  <Link to="/register" style={{ color: '#1890ff' }}>
                    Đăng ký ngay
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

export default LoginPage;
