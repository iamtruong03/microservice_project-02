import api from './api';

export const orderService = {
  // Create a new order
  createOrder: (orderData) => api.post('/orders', orderData),

  // Get order by ID
  getOrder: (orderId) => api.get(`/orders/${orderId}`),

  // Get all orders
  getAllOrders: (params) => api.get('/orders', { params }),

  // Get orders by customer
  getOrdersByCustomer: (customerId) => api.get(`/orders/customer/${customerId}`),

  // Update order
  updateOrder: (orderId, orderData) => api.put(`/orders/${orderId}`, orderData),

  // Delete order
  deleteOrder: (orderId) => api.delete(`/orders/${orderId}`),

  // Get order statistics
  getOrderStats: () => api.get('/orders/stats'),
};

export const inventoryService = {
  // Create inventory
  createInventory: (inventoryData) => api.post('/inventory', inventoryData),

  // Get inventory by ID
  getInventory: (inventoryId) => api.get(`/inventory/${inventoryId}`),

  // Get inventory by product
  getInventoryByProduct: (productId) => api.get(`/inventory/product/${productId}`),

  // Get all inventories
  getAllInventories: (params) => api.get('/inventory', { params }),

  // Update inventory
  updateInventory: (inventoryId, inventoryData) => api.put(`/inventory/${inventoryId}`, inventoryData),

  // Reserve inventory
  reserveInventory: (productId, quantity) =>
    api.post('/inventory/reserve', null, {
      params: { productId, quantity },
    }),

  // Delete inventory
  deleteInventory: (inventoryId) => api.delete(`/inventory/${inventoryId}`),
};

export const accountingService = {
  // Create transaction
  createTransaction: (transactionData) => api.post('/accounting/transactions', transactionData),

  // Get transaction by ID
  getTransaction: (transactionId) => api.get(`/accounting/transactions/${transactionId}`),

  // Get all transactions
  getAllTransactions: (params) => api.get('/accounting/transactions', { params }),

  // Get transactions by order
  getTransactionsByOrder: (orderId) => api.get(`/accounting/orders/${orderId}/transactions`),

  // Get transactions by customer
  getTransactionsByCustomer: (customerId) =>
    api.get(`/accounting/customers/${customerId}/transactions`),

  // Update transaction
  updateTransaction: (transactionId, transactionData) =>
    api.put(`/accounting/transactions/${transactionId}`, transactionData),

  // Delete transaction
  deleteTransaction: (transactionId) => api.delete(`/accounting/transactions/${transactionId}`),
};

export const notificationService = {
  // Send notification
  sendNotification: (recipientEmail, subject, message) =>
    api.post('/notifications', null, {
      params: { recipientEmail, subject, message },
    }),

  // Check health
  checkHealth: () => api.get('/notifications/health'),
};
