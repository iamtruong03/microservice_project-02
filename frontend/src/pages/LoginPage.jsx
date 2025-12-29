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
    const result = await login(values.username, values.password);

    if (result.success) {
      navigate('/');
    } else {
      setLocalError(result.error);
    }
  };

  return (
    <div className="auth-container">
      <Row justify="center" style={{ minHeight: '100vh', paddingTop: '40px', paddingBottom: '40px' }}>
        <Col xs={24} sm={22} md={16} lg={10}>
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
                  name="username"
                  label="Username"
                  rules={[
                    { required: true, message: 'Vui lòng nhập username' },
                  ]}
                >
                  <Input
                    prefix={<UserOutlined />}
                    placeholder="Nhập username"
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

              <div className="auth-form-footer">
                <p>
                  Chưa có tài khoản?{' '}
                  <Link to="/register">
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
