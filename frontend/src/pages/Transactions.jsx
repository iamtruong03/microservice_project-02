import React, { useState, useEffect } from 'react';
import { Card, Form, Button, InputNumber, Select, message, Spin, Tabs } from 'antd';
import { DollarOutlined, ArrowRightOutlined } from '@ant-design/icons';
import { accountService } from '../services/accountService';
import './Transactions.css';

const TransactionOperations = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const [transferForm] = Form.useForm();

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      const response = await accountService.getUserAccounts();
      setAccounts(response.data.data || []);
    } catch (error) {
      message.error('Failed to fetch accounts');
      console.error(error);
    }
  };

  const handleDepositWithdraw = async (values) => {
    try {
      setLoading(true);
      await accountService.depositOrWithdraw({
        accountId: values.accountId,
        amount: values.amount,
        type: values.type,
        description: values.description,
      });
      message.success(`${values.type} successful`);
      form.resetFields();
      fetchAccounts();
    } catch (error) {
      message.error('Transaction failed');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleTransfer = async (values) => {
    try {
      setLoading(true);
      await accountService.transferMoney({
        fromAccountId: values.fromAccountId,
        toAccountId: values.toAccountId,
        amount: values.amount,
        description: values.description,
      });
      message.success('Transfer successful');
      transferForm.resetFields();
      fetchAccounts();
    } catch (error) {
      message.error('Transfer failed');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const accountOptions = accounts.map(acc => ({
    label: `${acc.accountNumber} - $${parseFloat(acc.balance).toFixed(2)}`,
    value: acc.id,
  }));

  return (
    <div className="transaction-operations-container">
      <Tabs
        items={[
          {
            key: '1',
            label: 'Deposit & Withdraw',
            children: (
              <Card className="transaction-card">
                <h3>Deposit or Withdraw Funds</h3>
                <Form
                  form={form}
                  layout="vertical"
                  onFinish={handleDepositWithdraw}
                >
                  <Form.Item
                    label="Account"
                    name="accountId"
                    rules={[{ required: true, message: 'Please select account' }]}
                  >
                    <Select
                      placeholder="Select account"
                      options={accountOptions}
                    />
                  </Form.Item>

                  <Form.Item
                    label="Transaction Type"
                    name="type"
                    initialValue="DEPOSIT"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Select.Option value="DEPOSIT">
                        <DollarOutlined /> Deposit (Add Money)
                      </Select.Option>
                      <Select.Option value="WITHDRAW">
                        <ArrowRightOutlined /> Withdraw (Remove Money)
                      </Select.Option>
                    </Select>
                  </Form.Item>

                  <Form.Item
                    label="Amount"
                    name="amount"
                    rules={[
                      { required: true, message: 'Please enter amount' },
                      { pattern: /^\d+(\.\d{1,2})?$/, message: 'Invalid amount' },
                    ]}
                  >
                    <InputNumber
                      placeholder="Enter amount"
                      min={0.01}
                      step={0.01}
                      prefix="$"
                      style={{ width: '100%' }}
                    />
                  </Form.Item>

                  <Form.Item
                    label="Description"
                    name="description"
                  >
                    <input type="text" placeholder="Optional description" />
                  </Form.Item>

                  <Button type="primary" htmlType="submit" loading={loading} block>
                    Submit Transaction
                  </Button>
                </Form>
              </Card>
            ),
          },
          {
            key: '2',
            label: 'Transfer Between Accounts',
            children: (
              <Card className="transaction-card">
                <h3>Transfer Money Between Your Accounts</h3>
                <Form
                  form={transferForm}
                  layout="vertical"
                  onFinish={handleTransfer}
                >
                  <Form.Item
                    label="From Account"
                    name="fromAccountId"
                    rules={[{ required: true, message: 'Please select source account' }]}
                  >
                    <Select
                      placeholder="Select source account"
                      options={accountOptions}
                    />
                  </Form.Item>

                  <Form.Item
                    label="To Account"
                    name="toAccountId"
                    rules={[{ required: true, message: 'Please select destination account' }]}
                  >
                    <Select
                      placeholder="Select destination account"
                      options={accountOptions}
                    />
                  </Form.Item>

                  <Form.Item
                    label="Amount"
                    name="amount"
                    rules={[
                      { required: true, message: 'Please enter amount' },
                      { pattern: /^\d+(\.\d{1,2})?$/, message: 'Invalid amount' },
                    ]}
                  >
                    <InputNumber
                      placeholder="Enter amount"
                      min={0.01}
                      step={0.01}
                      prefix="$"
                      style={{ width: '100%' }}
                    />
                  </Form.Item>

                  <Form.Item
                    label="Description"
                    name="description"
                  >
                    <input type="text" placeholder="Optional description" />
                  </Form.Item>

                  <Button type="primary" htmlType="submit" loading={loading} block>
                    Transfer Money
                  </Button>
                </Form>
              </Card>
            ),
          },
        ]}
      />
    </div>
  );
};

export default TransactionOperations;
