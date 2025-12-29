import axiosInstance from './api';

const API_BASE_URL = '/accounts';

export const accountService = {
  /**
   * Tạo tài khoản ngân hàng
   */
  createBankAccount: (accountType = 'SAVINGS') => {
    return axiosInstance.post(`${API_BASE_URL}?accountType=${accountType}`);
  },

  /**
   * Xem tất cả tài khoản của user
   */
  getUserAccounts: () => {
    return axiosInstance.get(API_BASE_URL);
  },

  /**
   * Xem số dư tài khoản
   */
  getAccountBalance: (accountId) => {
    return axiosInstance.get(`${API_BASE_URL}/${accountId}/balance`);
  },

  /**
   * Chuyển tiền nội bộ
   */
  transferMoney: (transferData) => {
    return axiosInstance.post(`${API_BASE_URL}/transfer`, transferData);
  },

  /**
   * Rút tiền / Nạp tiền
   */
  depositOrWithdraw: (data) => {
    return axiosInstance.post(`${API_BASE_URL}/deposit-withdraw`, data);
  },

  /**
   * Lịch sử giao dịch
   */
  getTransactionHistory: () => {
    return axiosInstance.get(`${API_BASE_URL}/transactions/history`);
  },

  /**
   * Thống kê tài khoản
   */
  getAccountStatistics: () => {
    return axiosInstance.get(`${API_BASE_URL}/statistics`);
  },
};
