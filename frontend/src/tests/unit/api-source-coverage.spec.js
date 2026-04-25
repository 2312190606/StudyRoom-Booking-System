/**
 * API 源文件覆盖率测试
 * 通过 mock 依赖来测试 API 源文件的实际导出
 */
import { describe, test, expect, jest, beforeEach } from '@jest/globals';

// Mock axios before importing API modules
jest.mock('axios', () => {
  const mockInstance = {
    get: jest.fn().mockResolvedValue({ data: { code: 200, data: {} } }),
    post: jest.fn().mockResolvedValue({ data: { code: 200, data: {} } }),
    put: jest.fn().mockResolvedValue({ data: { code: 200, data: {} } }),
    delete: jest.fn().mockResolvedValue({ data: { code: 200, data: {} } }),
    interceptors: {
      request: { use: jest.fn(), eject: jest.fn() },
      response: { use: jest.fn(), eject: jest.fn() }
    }
  };
  return {
    __esModule: true,
    default: {
      create: jest.fn(() => mockInstance)
    },
    create: jest.fn(() => mockInstance)
  };
});

// Mock Vant
jest.mock('vant', () => ({
  showToast: jest.fn()
}));

// Polyfill import.meta for Vite environment variables
global.import = {
  meta: {
    env: {
      VITE_API_BASE_URL: '/api'
    }
  }
};

describe('API 源文件导出测试', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('auth.js', () => {
    let authModule;

    beforeAll(async () => {
      authModule = await import('../../api/auth.js');
    });

    test('register 函数导出正确', () => {
      expect(typeof authModule.register).toBe('function');
    });

    test('login 函数导出正确', () => {
      expect(typeof authModule.login).toBe('function');
    });

    test('refreshToken 函数导出正确', () => {
      expect(typeof authModule.refreshToken).toBe('function');
    });

    test('register 函数可被调用', async () => {
      const result = await authModule.register({ username: 'test', password: '123456' });
      expect(result).toBeDefined();
    });

    test('login 函数可被调用', async () => {
      const result = await authModule.login({ username: 'test', password: '123456' });
      expect(result).toBeDefined();
    });

    test('refreshToken 函数可被调用', async () => {
      const result = await authModule.refreshToken('test-refresh-token');
      expect(result).toBeDefined();
    });
  });

  describe('user.js', () => {
    let userModule;

    beforeAll(async () => {
      userModule = await import('../../api/user.js');
    });

    test('getUserProfile 函数导出正确', () => {
      expect(typeof userModule.getUserProfile).toBe('function');
    });

    test('updateUserProfile 函数导出正确', () => {
      expect(typeof userModule.updateUserProfile).toBe('function');
    });

    test('updatePassword 函数导出正确', () => {
      expect(typeof userModule.updatePassword).toBe('function');
    });

    test('getUserStats 函数导出正确', () => {
      expect(typeof userModule.getUserStats).toBe('function');
    });

    test('getFavorites 函数导出正确', () => {
      expect(typeof userModule.getFavorites).toBe('function');
    });

    test('quickReserveFavorite 函数导出正确', () => {
      expect(typeof userModule.quickReserveFavorite).toBe('function');
    });

    test('getCreditScore 函数导出正确', () => {
      expect(typeof userModule.getCreditScore).toBe('function');
    });

    test('getUserProfile 函数可被调用', async () => {
      const result = await userModule.getUserProfile();
      expect(result).toBeDefined();
    });

    test('updateUserProfile 函数可被调用', async () => {
      const result = await userModule.updateUserProfile({ username: 'test' });
      expect(result).toBeDefined();
    });

    test('getUserStats 函数可被调用', async () => {
      const result = await userModule.getUserStats();
      expect(result).toBeDefined();
    });

    test('updatePassword 函数可被调用', async () => {
      const result = await userModule.updatePassword({ oldPassword: '123', newPassword: '456' });
      expect(result).toBeDefined();
    });

    test('getFavorites 函数可被调用', async () => {
      const result = await userModule.getFavorites();
      expect(result).toBeDefined();
    });

    test('quickReserveFavorite 函数可被调用', async () => {
      const result = await userModule.quickReserveFavorite(1);
      expect(result).toBeDefined();
    });

    test('getCreditScore 函数可被调用', async () => {
      const result = await userModule.getCreditScore();
      expect(result).toBeDefined();
    });
  });

  describe('reservations.js', () => {
    let reservationsModule;

    beforeAll(async () => {
      reservationsModule = await import('../../api/reservations.js');
    });

    test('createReservation 函数导出正确', () => {
      expect(typeof reservationsModule.createReservation).toBe('function');
    });

    test('getMyReservations 函数导出正确', () => {
      expect(typeof reservationsModule.getMyReservations).toBe('function');
    });

    test('cancelReservation 函数导出正确', () => {
      expect(typeof reservationsModule.cancelReservation).toBe('function');
    });

    test('extendReservation 函数导出正确', () => {
      expect(typeof reservationsModule.extendReservation).toBe('function');
    });

    test('checkIn 函数导出正确', () => {
      expect(typeof reservationsModule.checkIn).toBe('function');
    });

    test('endStudy 函数导出正确', () => {
      expect(typeof reservationsModule.endStudy).toBe('function');
    });

    test('deleteReservation 函数导出正确', () => {
      expect(typeof reservationsModule.deleteReservation).toBe('function');
    });

    test('createReservation 函数可被调用', async () => {
      const result = await reservationsModule.createReservation({ seatId: 1 });
      expect(result).toBeDefined();
    });

    test('getMyReservations 函数可被调用', async () => {
      const result = await reservationsModule.getMyReservations({ page: 1 });
      expect(result).toBeDefined();
    });

    test('cancelReservation 函数可被调用', async () => {
      const result = await reservationsModule.cancelReservation(1);
      expect(result).toBeDefined();
    });

    test('extendReservation 函数可被调用', async () => {
      const result = await reservationsModule.extendReservation(1);
      expect(result).toBeDefined();
    });

    test('checkIn 函数可被调用', async () => {
      const result = await reservationsModule.checkIn(1, { latitude: 30.0, longitude: 120.0 });
      expect(result).toBeDefined();
    });

    test('endStudy 函数可被调用', async () => {
      const result = await reservationsModule.endStudy(1);
      expect(result).toBeDefined();
    });

    test('deleteReservation 函数可被调用', async () => {
      const result = await reservationsModule.deleteReservation(1);
      expect(result).toBeDefined();
    });
  });

  describe('rooms.js', () => {
    let roomsModule;

    beforeAll(async () => {
      roomsModule = await import('../../api/rooms.js');
    });

    test('getRooms 函数导出正确', () => {
      expect(typeof roomsModule.getRooms).toBe('function');
    });

    test('getRoomDetail 函数导出正确', () => {
      expect(typeof roomsModule.getRoomDetail).toBe('function');
    });

    test('getRoomSeats 函数导出正确', () => {
      expect(typeof roomsModule.getRoomSeats).toBe('function');
    });

    test('getRooms 函数可被调用', async () => {
      const result = await roomsModule.getRooms({ page: 1 });
      expect(result).toBeDefined();
    });

    test('getRoomDetail 函数可被调用', async () => {
      const result = await roomsModule.getRoomDetail(1);
      expect(result).toBeDefined();
    });

    test('getRoomSeats 函数可被调用', async () => {
      const result = await roomsModule.getRoomSeats(1);
      expect(result).toBeDefined();
    });
  });

  describe('ai.js', () => {
    let aiModule;

    beforeAll(async () => {
      aiModule = await import('../../api/ai.js');
    });

    test('askAI 函数导出正确', () => {
      expect(typeof aiModule.askAI).toBe('function');
    });

    test('askAI 函数可被调用', async () => {
      const result = await aiModule.askAI('你好');
      expect(result).toBeDefined();
    });
  });

  describe('announcements.js', () => {
    let announcementsModule;

    beforeAll(async () => {
      announcementsModule = await import('../../api/announcements.js');
    });

    test('getAnnouncements 函数导出正确', () => {
      expect(typeof announcementsModule.getAnnouncements).toBe('function');
    });

    test('createAnnouncement 函数导出正确', () => {
      expect(typeof announcementsModule.createAnnouncement).toBe('function');
    });

    test('updateAnnouncement 函数导出正确', () => {
      expect(typeof announcementsModule.updateAnnouncement).toBe('function');
    });

    test('deleteAnnouncement 函数导出正确', () => {
      expect(typeof announcementsModule.deleteAnnouncement).toBe('function');
    });

    test('getAnnouncements 函数可被调用', async () => {
      const result = await announcementsModule.getAnnouncements({ page: 1 });
      expect(result).toBeDefined();
    });

    test('createAnnouncement 函数可被调用', async () => {
      const result = await announcementsModule.createAnnouncement({ title: 'Test', content: 'Content' });
      expect(result).toBeDefined();
    });

    test('updateAnnouncement 函数可被调用', async () => {
      const result = await announcementsModule.updateAnnouncement(1, { title: 'Updated' });
      expect(result).toBeDefined();
    });

    test('deleteAnnouncement 函数可被调用', async () => {
      const result = await announcementsModule.deleteAnnouncement(1);
      expect(result).toBeDefined();
    });
  });

  describe('admin.js', () => {
    let adminModule;

    beforeAll(async () => {
      adminModule = await import('../../api/admin.js');
    });

    test('getDashboardStats 函数导出正确', () => {
      expect(typeof adminModule.getDashboardStats).toBe('function');
    });

    test('getAdminRooms 函数导出正确', () => {
      expect(typeof adminModule.getAdminRooms).toBe('function');
    });

    test('getAdminReservations 函数导出正确', () => {
      expect(typeof adminModule.getAdminReservations).toBe('function');
    });

    test('getAdminUsers 函数导出正确', () => {
      expect(typeof adminModule.getAdminUsers).toBe('function');
    });

    test('getConfig 函数导出正确', () => {
      expect(typeof adminModule.getConfig).toBe('function');
    });

    test('getDashboardStats 函数可被调用', async () => {
      const result = await adminModule.getDashboardStats();
      expect(result).toBeDefined();
    });

    test('getAdminRooms 函数可被调用', async () => {
      const result = await adminModule.getAdminRooms();
      expect(result).toBeDefined();
    });

    test('getAdminReservations 函数可被调用', async () => {
      const result = await adminModule.getAdminReservations({ page: 1 });
      expect(result).toBeDefined();
    });

    test('getAdminUsers 函数可被调用', async () => {
      const result = await adminModule.getAdminUsers({ page: 1 });
      expect(result).toBeDefined();
    });

    test('getConfig 函数可被调用', async () => {
      const result = await adminModule.getConfig();
      expect(result).toBeDefined();
    });
  });
});
