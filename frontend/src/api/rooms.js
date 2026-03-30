import apiClient from './client';

/**
 * 分页获取自习室列表（含位置、楼层筛选）
 * @param {Object} params { page, size, location, floor }
 */
export function getRooms(params) {
  return apiClient.get('/rooms', { params });
}

/**
 * 获取自习室详情
 * @param {Number|String} id 自习室ID
 */
export function getRoomDetail(id) {
  return apiClient.get(`/rooms/${id}`);
}

/**
 * 获取指定自习室的座位可视化视图信息及状态
 * @param {Number|String} id 自习室ID
 */
export function getRoomSeats(id) {
  return apiClient.get(`/rooms/${id}/seats`);
}
