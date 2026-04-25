/**
 * client.js 拦截器覆盖率测试
 */
import { describe, test, expect, jest, beforeAll } from '@jest/globals';

// 存储拦截器回调
let requestSuccessHandler = null;
let requestErrorHandler = null;
let responseSuccessHandler = null;
let responseErrorHandler = null;

const mockAxiosInstance = {
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
  interceptors: {
    request: {
      use: jest.fn((successHandler, errorHandler) => {
        requestSuccessHandler = successHandler;
        requestErrorHandler = errorHandler;
      }),
      eject: jest.fn()
    },
    response: {
      use: jest.fn((successHandler, errorHandler) => {
        responseSuccessHandler = successHandler;
        responseErrorHandler = errorHandler;
      }),
      eject: jest.fn()
    }
  }
};

// Mock axios
jest.mock('axios', () => ({
  __esModule: true,
  default: {
    create: jest.fn(() => mockAxiosInstance)
  }
}));

// Mock Vant
jest.mock('vant', () => ({
  showToast: jest.fn()
}));

// Polyfill import.meta
global.import = {
  meta: {
    env: {
      VITE_API_BASE_URL: '/api'
    }
  }
};

describe('client.js 拦截器覆盖率测试', () => {
  let apiClient;

  beforeAll(async () => {
    const clientModule = await import('../../api/client.js');
    apiClient = clientModule.default;
  });

  test('拦截器被正确注册', () => {
    expect(mockAxiosInstance.interceptors.request.use).toHaveBeenCalled();
    expect(mockAxiosInstance.interceptors.response.use).toHaveBeenCalled();
  });

  test('请求拦截器成功回调存在', () => {
    expect(typeof requestSuccessHandler).toBe('function');
  });

  test('请求拦截器错误回调存在', () => {
    expect(typeof requestErrorHandler).toBe('function');
  });

  test('响应拦截器成功回调存在', () => {
    expect(typeof responseSuccessHandler).toBe('function');
  });

  test('响应拦截器错误回调存在', () => {
    expect(typeof responseErrorHandler).toBe('function');
  });

  test('请求拦截器成功回调 - 无 token', () => {
    localStorage.clear();
    const config = { headers: {} };
    const result = requestSuccessHandler(config);
    expect(result).toBe(config);
    expect(result.headers['Authorization']).toBeUndefined();
  });

  test('请求拦截器成功回调 - 有 token', () => {
    localStorage.setItem('token', 'test-token');
    const config = { headers: {} };
    const result = requestSuccessHandler(config);
    expect(result.headers['Authorization']).toBe('Bearer test-token');
    localStorage.clear();
  });

  test('响应拦截器成功回调 - 200 响应', () => {
    const response = { data: { code: 200, data: { test: true } } };
    const result = responseSuccessHandler(response);
    expect(result).toEqual({ test: true });
  });

  test('响应拦截器成功回调 - 201 响应', () => {
    const response = { data: { code: 201, data: { created: true } } };
    const result = responseSuccessHandler(response);
    expect(result).toEqual({ created: true });
  });

  test('apiClient 有 get 方法', () => {
    expect(typeof apiClient.get).toBe('function');
  });

  test('apiClient 有 post 方法', () => {
    expect(typeof apiClient.post).toBe('function');
  });

  test('apiClient 有 put 方法', () => {
    expect(typeof apiClient.put).toBe('function');
  });

  test('apiClient 有 delete 方法', () => {
    expect(typeof apiClient.delete).toBe('function');
  });
});
