import axios from 'axios';
import { showToast } from 'vant';

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 假设使用 localStorage 存储并配合 Pinia 使用
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    const res = response.data;
    
    // 结合 GEMINI.md 标准，后端返回 { code, msg, data } 格式
    if (res.code !== 200 && res.code !== 201) {
      showToast(res.msg || 'Error');
      // 可以根据特定的 code 抛错（例如 Token 过期 401 触发登出跳转）
      if (res.code === 401) {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
      return Promise.reject(new Error(res.msg || 'Error'));
    }
    return res.data;
  },
  (error) => {
    let msg = error.message;
    if (error.response) {
       msg = error.response.data?.msg || '服务器错误';
    }
    showToast({ type: 'danger', message: msg });
    return Promise.reject(error);
  }
);

export default apiClient;
