import { transactionApi } from './api';

const API_BASE_URL = '/api/transactions';

export const transactionService = {
  /**
   * Tạo giao dịch mới
   */
  createTransaction: (transactionData) => {
    return transactionApi.post(API_BASE_URL, transactionData);
  },

  /**
   * Lấy tất cả giao dịch
   */
  getAllTransactions: (page = 0, size = 10) => {
    return transactionApi.get(`${API_BASE_URL}?page=${page}&size=${size}`);
  },

  /**
   * Lấy giao dịch theo ID
   */
  getTransactionById: (transactionId) => {
    return transactionApi.get(`${API_BASE_URL}/${transactionId}`);
  },

  /**
   * Lấy giao dịch theo user ID
   */
  getTransactionsByUserId: (userId) => {
    return transactionApi.get(`${API_BASE_URL}/user/${userId}`);
  },

  /**
   * Tìm kiếm giao dịch
   */
  searchTransactions: (keyword, page = 0, size = 10) => {
    return transactionApi.get(`${API_BASE_URL}/search?keyword=${keyword}&page=${page}&size=${size}`);
  },

  /**
   * Thống kê giao dịch
   */
  getTransactionStatistics: () => {
    return transactionApi.get(`${API_BASE_URL}/statistics`);
  },

  /**
   * Cập nhật trạng thái giao dịch
   */
  updateTransactionStatus: (transactionId, status) => {
    return transactionApi.put(`${API_BASE_URL}/${transactionId}/status`, { status });
  },
};