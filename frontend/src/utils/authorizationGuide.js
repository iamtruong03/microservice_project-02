// User Management Integration Guide
// Hướng dẫn tích hợp quản lý người dùng

import { userService } from '../services/index';

/**
 * USERS MANAGEMENT (User Service)
 * Quản lý người dùng - Port 8082
 */

// 1. Tạo user mới (Admin)
export const createNewUser = async (userData) => {
  const { name, email, password, phone, city, occupation, status } = userData;
  try {
    const response = await userService.createUser({
      name,
      email,
      password, // Bắt buộc
      phone,
      city,
      occupation,
      status: status || 'ACTIVE'
    });
    return response;
  } catch (error) {
    console.error('Failed to create user:', error);
    throw error;
  }
};

// 2. Lấy danh sách users (có phân trang)
export const getUsersList = async (page = 0, size = 10) => {
  try {
    const response = await userService.getUsersPaged(page, size);
    return response;
  } catch (error) {
    console.error('Failed to fetch users:', error);
    throw error;
  }
};

// 3. Cập nhật user
export const updateUserInfo = async (userId, userData) => {
  try {
    const response = await userService.updateUser(userId, userData);
    return response;
  } catch (error) {
    console.error('Failed to update user:', error);
    throw error;
  }
};

// 4. Xóa user
export const deleteUserAccount = async (userId) => {
  try {
    await userService.deleteUser(userId);
  } catch (error) {
    console.error('Failed to delete user:', error);
    throw error;
  }
};

// 5. Tìm kiếm users
export const searchUsersInfo = async (keyword) => {
  try {
    const response = await userService.searchUsers(keyword);
    return response;
  } catch (error) {
    console.error('Failed to search users:', error);
    throw error;
  }
};

/**
 * ROLES & PERMISSIONS MANAGEMENT
 * Quản lý vai trò và quyền hạn
 */

// 1. Gán role cho user
export const assignRoleToUser = async (userId, roleId) => {
  try {
    const response = await userService.assignRoleToUser(userId, roleId);
    return response;
  } catch (error) {
    console.error('Failed to assign role:', error);
    throw error;
  }
};

// 2. Gỡ role khỏi user
export const removeRoleFromUser = async (userId, roleId) => {
  try {
    const response = await userService.removeRoleFromUser(userId, roleId);
    return response;
  } catch (error) {
    console.error('Failed to remove role:', error);
    throw error;
  }
};

// 3. Lấy danh sách roles của user
export const getUserRoles = async (userId) => {
  try {
    const response = await userService.getUserRoles(userId);
    return response;
  } catch (error) {
    console.error('Failed to fetch user roles:', error);
    throw error;
  }
};

// 4. Lấy danh sách permissions của user (dựa trên roles)
export const getUserPermissions = async (userId) => {
  try {
    const response = await userService.getUserPermissions(userId);
    return response;
  } catch (error) {
    console.error('Failed to fetch user permissions:', error);
    throw error;
  }
};

/**
 * DATA AUTHORIZATION (Tất cả Services)
 * Phân quyền dữ liệu theo User ID
 */

// ORDERS - Quản lý đơn hàng
export const getUserOrders = async () => {
  // Tự động lấy đơn hàng của user hiện tại
  // Không cần truyền userId vì system tự xác định từ JWT token
  try {
    const response = await userService.getUserOrders();
    return response;
  } catch (error) {
    console.error('Failed to fetch orders:', error);
    throw error;
  }
};

export const getOrderDetail = async (orderId) => {
  // Kiểm tra xem user có quyền xem đơn hàng này không
  // Nếu không, API sẽ trả về "Access denied"
  try {
    const response = await userService.getOrderDetail(orderId);
    return response;
  } catch (error) {
    console.error('Failed to fetch order:', error);
    throw error;
  }
};

// INVENTORY - Quản lý kho hàng
export const getUserInventory = async () => {
  // Lấy inventory của user hiện tại
  try {
    const response = await userService.getUserInventory();
    return response;
  } catch (error) {
    console.error('Failed to fetch inventory:', error);
    throw error;
  }
};

export const createInventoryItem = async (productId, quantity) => {
  // Tạo inventory tự động gán cho user hiện tại
  try {
    const response = await userService.createInventory({
      productId,
      quantity
      // userId tự động được gán = userId của người dùng hiện tại
    });
    return response;
  } catch (error) {
    console.error('Failed to create inventory:', error);
    throw error;
  }
};

// TRANSACTIONS - Quản lý giao dịch
export const getUserTransactions = async () => {
  // Lấy giao dịch của user hiện tại
  try {
    const response = await userService.getUserTransactions();
    return response;
  } catch (error) {
    console.error('Failed to fetch transactions:', error);
    throw error;
  }
};

export const getTransactionDetail = async (transactionId) => {
  // Kiểm tra xem user có quyền xem giao dịch này không
  try {
    const response = await userService.getTransactionDetail(transactionId);
    return response;
  } catch (error) {
    console.error('Failed to fetch transaction:', error);
    throw error;
  }
};

/**
 * SECURITY FEATURES
 * Các tính năng bảo mật
 */

// Kiểm tra xem user có quyền truy cập tài nguyên không
export const canAccessResource = async (resourceId, resourceType) => {
  // Gọi đến API để kiểm tra quyền
  // Returns: true/false
  try {
    const response = await fetch(`/api/${resourceType}/${resourceId}`, {
      method: 'HEAD',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    return response.ok;
  } catch (error) {
    return false;
  }
};

/**
 * ERROR HANDLING
 * Xử lý lỗi phân quyền
 */

export const handleAuthorizationError = (error) => {
  if (error.response?.status === 403 || error.message === 'Access denied') {
    // User không có quyền truy cập
    return {
      error: 'access_denied',
      message: 'Bạn không có quyền truy cập tài nguyên này'
    };
  }
  
  if (error.response?.status === 401) {
    // Token không hợp lệ hoặc hết hạn
    return {
      error: 'unauthorized',
      message: 'Vui lòng đăng nhập lại'
    };
  }
  
  return {
    error: 'unknown',
    message: error.message
  };
};

/**
 * USAGE EXAMPLES
 * Ví dụ sử dụng
 */

// Ví dụ 1: Lấy danh sách user và gán role
/*
const users = await getUsersList(0, 10);
for (const user of users.content) {
  await assignRoleToUser(user.id, 2); // Gán role "MANAGER"
}
*/

// Ví dụ 2: Tạo user mới với password
/*
const newUser = await createNewUser({
  name: 'John Doe',
  email: 'john@example.com',
  password: 'SecurePassword123',
  phone: '+84912345678',
  city: 'Hanoi',
  occupation: 'Engineer',
  status: 'ACTIVE'
});
*/

// Ví dụ 3: Lấy các order của user hiện tại (xác thực bằng JWT)
/*
try {
  const orders = await getUserOrders();
  console.log('User orders:', orders);
} catch (error) {
  const err = handleAuthorizationError(error);
  console.error(err.message);
}
*/

// Ví dụ 4: Kiểm tra quyền trước khi truy cập
/*
if (await canAccessResource(orderId, 'orders')) {
  const order = await getOrderDetail(orderId);
} else {
  console.error('Không có quyền truy cập đơn hàng này');
}
*/
