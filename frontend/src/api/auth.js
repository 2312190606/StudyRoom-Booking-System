import apiClient from './client';

/**
 * 用户注册
 * @param {Object} data { username, password, phone }
 */
export function register(data) {
  return apiClient.post('/auth/register', data);
}

/**
 * 用户登录
 * @param {Object} data { username, password }
 */
export function login(data) {
  return apiClient.post('/auth/login', data);
}

/**
 * 刷新 Token
 */
export function refreshToken() {
  return apiClient.post('/auth/refresh');
}
