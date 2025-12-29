import React, { useState, useEffect } from 'react';
import {
  Button,
  Form,
  Input,
  Modal,
  message,
  List,
  Card,
  Empty,
  Spin,
  Badge,
  Space,
  Divider,
  Alert,
} from 'antd';
import { BellOutlined, CheckCircleOutlined, ClockCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import { notificationService } from '../services';

const Notifications = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [form] = Form.useForm();
  const [serviceHealth, setServiceHealth] = useState(null);

  // Check service health
  useEffect(() => {
    const checkHealth = async () => {
      try {
        const response = await notificationService.checkHealth();
        setServiceHealth(response.data);
      } catch (error) {
        console.error('Failed to check health:', error);
      }
    };
    checkHealth();
  }, []);

  const handleSend = async (values) => {
    setLoading(true);
    try {
      const response = await notificationService.sendNotification(
        values.recipientEmail,
        values.subject,
        values.message
      );
      message.success('Notification sent successfully');
      
      // Add to notifications list
      const newNotification = {
        id: Date.now(),
        ...values,
        ...response.data,
        sentAt: new Date().toISOString(),
      };
      setNotifications([newNotification, ...notifications]);
      
      setIsModalVisible(false);
      form.resetFields();
    } catch (error) {
      message.error('Failed to send notification');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'SENT':
        return <CheckCircleOutlined style={{ color: '#52c41a' }} />;
      case 'PENDING':
        return <ClockCircleOutlined style={{ color: '#faad14' }} />;
      case 'FAILED':
        return <CloseCircleOutlined style={{ color: '#ff4d4f' }} />;
      default:
        return null;
    }
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      SENT: { color: 'green', text: 'Sent' },
      PENDING: { color: 'orange', text: 'Pending' },
      FAILED: { color: 'red', text: 'Failed' },
    };
    const config = statusMap[status] || { color: 'default', text: 'Unknown' };
    return <Badge color={config.color} text={config.text} />;
  };

  return (
    <div style={{ padding: '24px' }}>
      {serviceHealth && (
        <Alert
          message="Service Status"
          description={serviceHealth}
          type="success"
          closable
          style={{ marginBottom: '24px' }}
        />
      )}

      <div style={{ marginBottom: '24px' }}>
        <Button
          type="primary"
          icon={<BellOutlined />}
          size="large"
          onClick={() => setIsModalVisible(true)}
        >
          Send Notification
        </Button>
      </div>

      <Card title="Recent Notifications" style={{ marginBottom: '24px' }}>
        <Spin spinning={loading}>
          {notifications.length === 0 ? (
            <Empty description="No notifications sent yet" />
          ) : (
            <List
              dataSource={notifications}
              renderItem={(notification) => (
                <List.Item key={notification.id}>
                  <List.Item.Meta
                    avatar={getStatusIcon(notification.status)}
                    title={
                      <Space>
                        <span>{notification.subject}</span>
                        {getStatusBadge(notification.status)}
                      </Space>
                    }
                    description={
                      <div>
                        <p>
                          <strong>To:</strong> {notification.recipientEmail}
                        </p>
                        <p>
                          <strong>Message:</strong> {notification.message}
                        </p>
                        {notification.sentAt && (
                          <p style={{ color: '#999', fontSize: '12px' }}>
                            Sent at: {new Date(notification.sentAt).toLocaleString()}
                          </p>
                        )}
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          )}
        </Spin>
      </Card>

      <Card title="Example Events" style={{ backgroundColor: '#fafafa' }}>
        <p>Notifications are sent when:</p>
        <ul>
          <li>✅ Order is created or updated</li>
          <li>✅ Inventory is reserved or updated</li>
          <li>✅ Transaction is completed</li>
          <li>✅ System events occur</li>
        </ul>
        <Divider />
        <p>This service listens to Kafka topics and sends emails automatically.</p>
      </Card>

      <Modal
        title="Send Notification"
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null}
      >
        <Form form={form} layout="vertical" onFinish={handleSend}>
          <Form.Item
            name="recipientEmail"
            label="Recipient Email"
            rules={[
              { required: true, message: 'Please input recipient email' },
              { type: 'email', message: 'Invalid email format' },
            ]}
          >
            <Input placeholder="example@example.com" />
          </Form.Item>

          <Form.Item
            name="subject"
            label="Subject"
            rules={[{ required: true, message: 'Please input subject' }]}
          >
            <Input placeholder="Notification subject" />
          </Form.Item>

          <Form.Item
            name="message"
            label="Message"
            rules={[{ required: true, message: 'Please input message' }]}
          >
            <Input.TextArea rows={4} placeholder="Notification message" />
          </Form.Item>

          <Button type="primary" block htmlType="submit" loading={loading}>
            Send
          </Button>
        </Form>
      </Modal>
    </div>
  );
};

export default Notifications;
