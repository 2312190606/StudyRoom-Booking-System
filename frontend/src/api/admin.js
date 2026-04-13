import apiClient from './client';

// -- 仪表盘相关 --
export function getDashboardStats() {
  return apiClient.get('/admin/dashboard/stats');
}

export function getDashboardUtilization() {
  return apiClient.get('/admin/dashboard/utilization');
}

export function getDashboardUsersTrend() {
  return apiClient.get('/admin/dashboard/users-trend');
}

// -- 自习室与座位管理相关 --
export function getAdminRooms() {
  return apiClient.get('/admin/rooms');
}

export function createRoom(data) {
  return apiClient.post('/admin/rooms', data);
}

export function updateRoom(id, data) {
  return apiClient.put(`/admin/rooms/${id}`, data);
}

export function updateMaintenanceSeats(id, maintenanceSeats) {
  return apiClient.put(`/admin/rooms/${id}/maintenance`, { maintenanceSeats });
}

export function deleteRoom(id) {
  return apiClient.delete(`/admin/rooms/${id}`);
}

export function batchCreateSeats(roomId, data) {
  return apiClient.post(`/admin/rooms/${roomId}/seats/batch`, data);
}

export function updateSeat(seatId, data) {
  return apiClient.put(`/admin/seats/${seatId}`, data);
}

// -- 预约记录管理 --
export function getAdminReservations(params) {
  return apiClient.get('/admin/reservations', { params });
}

export function adminCancelReservation(id) {
  return apiClient.put(`/admin/reservations/${id}/cancel`);
}

// -- 用户管理 --
export function getAdminUsers(params) {
  return apiClient.get('/admin/users', { params });
}

export function createUser(data) {
  return apiClient.post('/admin/users', data);
}

export function deleteUser(id) {
  return apiClient.delete(`/admin/users/${id}`);
}

export function updateUserStatus(id, data) {
  return apiClient.put(`/admin/users/${id}/status`, data);
}

export function updateUser(id, data) {
  return apiClient.put(`/admin/users/${id}`, data);
}

// -- 系统配置与内容管理 --
export function getConfig() {
  return apiClient.get('/admin/config');
}

export function updateConfig(data) {
  return apiClient.put('/admin/config', data);
}

export function createAnnouncement(data) {
  return apiClient.post('/admin/announcements', data);
}
