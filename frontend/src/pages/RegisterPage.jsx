import React, { useContext, useState } from 'react';
import { Form, Input, Button, Card, Row, Col, Alert, Spin, Progress, Divider } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, UserAddOutlined, IdcardOutlined, SafetyOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import './AuthPages.css';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register, loading, error } = useContext(AuthContext);
  const [form] = Form.useForm();
  const [localError, setLocalError] = useState(null);
  const [passwordStrength, setPasswordStrength] = useState(0);

  const calculatePasswordStrength = (password) => {
    if (!password) return 0;
    let strength = 0;
    if (password.length >= 6) strength += 25;
    if (password.length >= 10) strength += 25;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength += 25;
    if (/\d/.test(password)) strength += 15;
    if (/[^a-zA-Z0-9]/.test(password)) strength += 10;
    return Math.min(strength, 100);
  };

  const getPasswordStrengthColor = (strength) => {
    if (strength < 40) return '#ff4d4f';
    if (strength < 70) return '#faad14';
    return '#52c41a';
  };

  const getPasswordStrengthText = (strength) => {
    if (strength === 0) return '';
    if (strength < 40) return 'Yếu';
    if (strength < 70) return 'Trung bình';
    return 'Mạnh';
  };

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
      <Row justify="center" style={{ minHeight: '100vh', paddingTop: '60px', paddingBottom: '60px' }}>
        <Col xs={24} sm={24} md={20} lg={14} xl={12} xxl={10}>
          <Card
            title={
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px' }}>
                <UserAddOutlined style={{ fontSize: '24px' }} />
                <span>Đăng Ký Tài Khoản</span>
              </div>
            }
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
                onClose={() => setLocalError(null)}
                className="auth-alert"
              />
            )}

            <Spin spinning={loading} tip="Đang xử lý...">
              <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
                autoComplete="off"
                requiredMark="optional"
              >
                <Form.Item
                  name="username"
                  label={<><UserOutlined style={{ marginRight: 6 }} /> Username</>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập username' },
                    { min: 3, message: 'Username phải có ít nhất 3 ký tự' },
                    { pattern: /^[a-zA-Z0-9_]+$/, message: 'Username chỉ chứa chữ, số và dấu _' }
                  ]}
                  hasFeedback
                >
                  <Input 
                    prefix={<UserOutlined className="input-icon" />} 
                    placeholder="Chọn username của bạn"
                    size="large"
                  />
                </Form.Item>

                <Form.Item
                  name="fullName"
                  label={<><IdcardOutlined style={{ marginRight: 6 }} /> Họ và Tên</>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập họ và tên' },
                    { min: 2, message: 'Họ và tên phải có ít nhất 2 ký tự' },
                  ]}
                  hasFeedback
                >
                  <Input 
                    prefix={<IdcardOutlined className="input-icon" />} 
                    placeholder="Nhập họ và tên đầy đủ"
                    size="large"
                  />
                </Form.Item>

                <Form.Item
                  name="email"
                  label={<><MailOutlined style={{ marginRight: 6 }} /> Email</>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập email' },
                    { type: 'email', message: 'Email không hợp lệ' },
                  ]}
                  hasFeedback
                >
                  <Input
                    prefix={<MailOutlined className="input-icon" />}
                    placeholder="example@email.com"
                    size="large"
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  label={<><LockOutlined style={{ marginRight: 6 }} /> Mật Khẩu</>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập mật khẩu' },
                    { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự' },
                  ]}
                  hasFeedback
                >
                  <Input.Password
                    prefix={<LockOutlined className="input-icon" />}
                    placeholder="Tối thiểu 6 ký tự"
                    size="large"
                    onChange={(e) => setPasswordStrength(calculatePasswordStrength(e.target.value))}
                  />
                </Form.Item>

                {passwordStrength > 0 && (
                  <div style={{ marginTop: '-16px', marginBottom: '24px' }}>
                    <Progress
                      percent={passwordStrength}
                      strokeColor={getPasswordStrengthColor(passwordStrength)}
                      showInfo={false}
                      size="small"
                    />
                    <span style={{ 
                      fontSize: '13px', 
                      color: getPasswordStrengthColor(passwordStrength),
                      fontWeight: 500 
                    }}>
                      Độ mạnh mật khẩu: {getPasswordStrengthText(passwordStrength)}
                    </span>
                  </div>
                )}

                <Form.Item
                  name="confirmPassword"
                  label={<><SafetyOutlined style={{ marginRight: 6 }} /> Xác Nhận Mật Khẩu</>}
                  dependencies={['password']}
                  rules={[
                    { required: true, message: 'Vui lòng xác nhận mật khẩu' },
                    ({ getFieldValue }) => ({
                      validator(_, value) {
                        if (!value || getFieldValue('password') === value) {
                          return Promise.resolve();
                        }
                        return Promise.reject(new Error('Mật khẩu không khớp'));
                      },
                    }),
                  ]}
                  hasFeedback
                >
                  <Input.Password
                    prefix={<SafetyOutlined className="input-icon" />}
                    placeholder="Nhập lại mật khẩu"
                    size="large"
                  />
                </Form.Item>

                <Form.Item style={{ marginBottom: 0 }}>
                  <Button
                    type="primary"
                    htmlType="submit"
                    block
                    size="large"
                    loading={loading}
                    icon={<UserAddOutlined />}
                  >
                    {loading ? 'Đang đăng ký...' : 'Đăng Ký Ngay'}
                  </Button>
                </Form.Item>
              </Form>

              <Divider style={{ margin: '32px 0', color: '#999', fontSize: '14px' }}>
                Hoặc
              </Divider>

              <div className="auth-form-footer">
                <p style={{ margin: 0 }}>
                  Đã có tài khoản?{' '}
                  <Link to="/login" className="auth-link">
                    Đăng nhập ngay
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
