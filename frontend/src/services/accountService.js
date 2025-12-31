import { accountApi } from './api';

const API_BASE_URL = '/api/accounts';

export const accountService = {
  /**
   * Tạo tài khoản ngân hàng
   */
  createBankAccount: (accountType = 'SAVINGS') => {
    return accountApi.post(`${API_BASE_URL}?accountType=${accountType}`);
  },

  /**
   * Xem tất cả tài khoản của user
   */
  getUserAccounts: () => {
    return accountApi.get(API_BASE_URL);
  },

  /**
   * Xem số dư tài khoản
   */
  getAccountBalance: (accountId) => {
    return accountApi.get(`${API_BASE_URL}/${accountId}/balance`);
  },

  /**
   * Chuyển tiền nội bộ
   */
  transferMoney: (transferData) => {
    return accountApi.post(`${API_BASE_URL}/transfer`, transferData);
  },

  /**
   * Rút tiền / Nạp tiền
   */
  depositOrWithdraw: (data) => {
    return accountApi.post(`${API_BASE_URL}/deposit-withdraw`, data);
  },

  /**
   * Lịch sử giao dịch
   */
  getTransactionHistory: () => {
    return accountApi.get(`${API_BASE_URL}/transactions/history`);
  },

  /**
   * Thống kê tài khoản
   */
  getAccountStatistics: () => {
    return accountApi.get(`${API_BASE_URL}/statistics`);
  },
};
