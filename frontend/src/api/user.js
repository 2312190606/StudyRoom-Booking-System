import apiClient from './client';

/**
 * 获取个人信息
 */
export function getUserProfile() {
  return apiClient.get('/user/profile');
}

/**
 * 修改个人资料
 * @param {Object} data { avatar, phone, email }
 */
export function updateUserProfile(data) {
  return apiClient.put('/user/profile', data);
}

/**
 * 修改登录密码
 * @param {Object} data { oldPassword, newPassword }
 */
export function updatePassword(data) {
  return apiClient.put('/user/password', data);
}

/**
 * 获取学习时长统计（按日/周/月图表数据）
 */
export function getUserStats() {
  return apiClient.get('/user/stats');
}

/**
 * 获取常用座位配置列表
 */
export function getFavorites() {
  return apiClient.get('/favorites');
}

/**
 * 快捷配置快速预约常用座位
 * @param {Number|String} id 预设项ID
 */
export function quickReserveFavorite(id) {
  return apiClient.post(`/favorites/${id}/quick-reserve`);
}

/**
 * 获取信誉分
 */
export function getCreditScore() {
  return apiClient.get('/user/credit-score');
}
