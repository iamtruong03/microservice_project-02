import { notificationApi } from './api';

const API_BASE_URL = '/api/notifications';

export const notificationService = {
  /**
   * Lấy tất cả thông báo
   */
  getAllNotifications: (page = 0, size = 10) => {
    return notificationApi.get(`${API_BASE_URL}?page=${page}&size=${size}`);
  },

  /**
   * Lấy thông báo theo user ID
   */
  getNotificationsByUserId: (userId) => {
    return notificationApi.get(`${API_BASE_URL}/user/${userId}`);
  },

  /**
   * Đánh dấu thông báo đã đọc
   */
  markAsRead: (notificationId) => {
    return notificationApi.put(`${API_BASE_URL}/${notificationId}/read`);
  },

  /**
   * Tạo thông báo mới
   */
  createNotification: (notificationData) => {
    return notificationApi.post(API_BASE_URL, notificationData);
  },

  /**
   * Xóa thông báo
   */
  deleteNotification: (notificationId) => {
    return notificationApi.delete(`${API_BASE_URL}/${notificationId}`);
  },

  /**
   * Lấy thống kê realtime
   */
  getRealTimeStatistics: () => {
    return notificationApi.get('/api/statistics/realtime');
  },

  /**
   * Lấy báo cáo thống kê
   */
  getStatisticsReport: () => {
    return notificationApi.get('/api/statistics/report');
  },
};