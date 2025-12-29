import React, { useContext, useState } from 'react';
import { Form, Input, Button, Card, Row, Col, Alert, Spin, Checkbox, Divider } from 'antd';
import { UserOutlined, LockOutlined, LoginOutlined } from '@ant-design/icons';
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
      <Row justify="center" style={{ minHeight: '100vh', paddingTop: '60px', paddingBottom: '60px' }}>
        <Col xs={24} sm={22} md={18} lg={12} xl={10}>
          <Card
            title={
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '12px' }}>
                <LoginOutlined style={{ fontSize: '28px' }} />
                <span>Đăng Nhập Hệ Thống</span>
              </div>
            }
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
                onClose={() => setLocalError(null)}
                className="auth-alert"
              />
            )}

            <Spin spinning={loading} tip="Đang xác thực...">
              <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
                autoComplete="off"
                requiredMark="optional"
                initialValues={{ remember: true }}
              >
                <Form.Item
                  name="username"
                  label={<><UserOutlined style={{ marginRight: 6 }} /> Username</>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập username' },
                  ]}
                  hasFeedback
                >
                  <Input
                    prefix={<UserOutlined className="input-icon" />}
                    placeholder="Nhập username của bạn"
                    size="large"
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  label={<><LockOutlined style={{ marginRight: 6 }} /> Mật Khẩu</>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập mật khẩu' },
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined className="input-icon" />}
                    placeholder="Nhập mật khẩu"
                    size="large"
                  />
                </Form.Item>

                <Form.Item>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Form.Item name="remember" valuePropName="checked" noStyle>
                      <Checkbox>Nhớ đăng nhập</Checkbox>
                    </Form.Item>
                    <Link to="/forgot-password" className="forgot-password-link">
                      Quên mật khẩu?
                    </Link>
                  </div>
                </Form.Item>

                <Form.Item style={{ marginBottom: 0 }}>
                  <Button
                    type="primary"
                    htmlType="submit"
                    block
                    size="large"
                    loading={loading}
                    icon={<LoginOutlined />}
                  >
                    {loading ? 'Đang đăng nhập...' : 'Đăng Nhập'}
                  </Button>
                </Form.Item>
              </Form>

              <Divider style={{ margin: '32px 0', color: '#999', fontSize: '14px' }}>
                Hoặc
              </Divider>

              <div className="auth-form-footer">
                <p style={{ margin: 0 }}>
                  Chưa có tài khoản?{' '}
                  <Link to="/register" className="auth-link">
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
