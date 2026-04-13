import apiClient from './client';

/**
 * 提交预约请求
 * @param {Object} data { seatId, startTime, endTime }
 */
export function createReservation(data) {
  return apiClient.post('/reservations', data);
}

/**
 * 获取我的预约列表
 * @param {Object} params { page, size }
 */
export function getMyReservations(params) {
  return apiClient.get('/reservations/me', { params });
}

/**
 * 取消预约
 * @param {Number|String} id 预约ID
 */
export function cancelReservation(id) {
  return apiClient.put(`/reservations/${id}/cancel`);
}

/**
 * 预约延后
 * @param {Number|String} id 预约ID
 */
export function extendReservation(id) {
  return apiClient.put(`/reservations/${id}/extend`);
}

/**
 * 签到验证（基于定位）
 * @param {Number|String} id 预约ID
 * @param {Object} data { latitude, longitude }
 */
export function checkIn(id, data) {
  return apiClient.post(`/reservations/${id}/check-in`, data);
}

/**
 * 结束学习
 * @param {Number|String} id 预约ID
 */
export function endStudy(id) {
  return apiClient.put(`/reservations/${id}/end`);
}

/**
 * 删除预约记录
 * @param {Number|String} id 预约ID
 */
export function deleteReservation(id) {
  return apiClient.delete(`/reservations/${id}`);
}
