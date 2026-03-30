// src/mocks/handlers.js
import { http, HttpResponse } from 'msw';

// 简单封装通用的返回格式以匹配项目定义的 { code: 200, msg: 'success', data: ... }
function success(data = null, msg = 'success', code = 200) {
  return HttpResponse.json({ code, msg, data });
}

export const handlers = [
  // 用户登录
  http.post('/api/auth/login', async ({ request }) => {
    const { username, password } = await request.json();
    if (username === 'admin' && password === '123456') {
      return success({ token: 'mock-jwt-token-abcd1234' });
    }
    return HttpResponse.json({ code: 400, msg: '账号或密码错误', data: null });
  }),
  
  // 自习室列表
  http.get('/api/rooms', ({ request }) => {
    return success({
      total: 2,
      records: [
        {
          id: 1,
          name: '第一自习室',
          location: '图书馆 1 楼',
          floor: 1,
          capacity: 100,
          availableSeats: 45,
          status: 0
        },
        {
          id: 2,
          name: '考研自习室',
          location: '教学楼 3 栋',
          floor: 3,
          capacity: 50,
          availableSeats: 5,
          status: 0
        }
      ]
    });
  }),
  
  // 指定自习室座位
  http.get('/api/rooms/:id/seats', ({ params }) => {
    const roomId = params.id;
    // 模拟返回网格座位布局
    return success([
      { id: 101, seatNo: 'A01', rowNo: 1, colNo: 1, status: 0 },
      { id: 102, seatNo: 'A02', rowNo: 1, colNo: 2, status: 1 },
      { id: 103, seatNo: 'A03', rowNo: 1, colNo: 3, status: 0 },
      { id: 104, seatNo: 'A04', rowNo: 1, colNo: 4, status: 2 }
    ]);
  }),
  
  // 创建预约
  http.post('/api/reservations', async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json({ code: 201, msg: '预约成功', data: { id: 999, ...body } });
  }),
  
  // 个人信息
  http.get('/api/user/profile', () => {
    return success({
      id: 1,
      username: 'admin',
      phone: '13812345678',
      avatar: 'https://via.placeholder.com/150'
    });
  }),
  
  // 我的预约
  http.get('/api/reservations/me', () => {
    return success({
      total: 1,
      records: [
        {
          id: 999,
          roomName: '第一自习室',
          seatNo: 'A01',
          startTime: '2026-03-26T08:00:00Z',
          endTime: '2026-03-26T12:00:00Z',
          status: 'PENDING'
        }
      ]
    });
  }),

  // 获取仪表盘
  http.get('/api/admin/dashboard/stats', () => {
    return success({
      reservationsTody: 156,
      totalUsers: 2450,
      utilizationRate: '85%'
    });
  })
];
