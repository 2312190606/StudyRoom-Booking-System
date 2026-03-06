# 自习室座位预约系统 - API 接口文档

## 基础说明
- **基础路径 (Base URL)**: `/api`
- **认证方式**: Bearer Token (JWT), 放在请求头 `Authorization: Bearer <token>`
- **返回格式**: JSON (`{ "code": 200, "msg": "success", "data": {...} }`)

---

## 一、用户端 API

### 1.1 用户认证 (`/api/auth`)
- `POST /api/auth/register` - 用户注册（手机号/邮箱、密码）
- `POST /api/auth/login` - 用户登录，返回 JWT 
- `POST /api/auth/refresh` - 刷新 Token

### 1.2 首页与公共信息 (`/api/public`)
- `GET /api/public/carousels` - 获取首页轮播图
- `GET /api/public/announcements` - 获取系统公告列表
- `GET /api/public/announcements/{id}` - 获取公告详情

### 1.3 自习室与座位 (`/api/rooms`)
- `GET /api/rooms` - 分页获取自习室列表（支持按位置、楼层搜索筛选）
- `GET /api/rooms/{id}` - 获取自习室详情
- `GET /api/rooms/{id}/seats` - 获取指定自习室的座位可视化视图信息及状态

### 1.4 预约中心 (`/api/reservations`)
- `POST /api/reservations` - 提交预约请求（选定座位、开始时间、结束时间/时长）
- `GET /api/reservations/me` - 获取我的预约列表（包含历史记录及当前状态）
- `GET /api/reservations/{id}` - 获取预约详情
- `PUT /api/reservations/{id}/cancel` - 取消预约
- `PUT /api/reservations/{id}/extend` - 预约延后（限1次，延后30分钟）
- `POST /api/reservations/{id}/check-in` - 签到验证（传入定位信息）

### 1.5 个人中心 (`/api/user`)
- `GET /api/user/profile` - 获取个人信息
- `PUT /api/user/profile` - 修改个人资料（头像、昵称、手机号等）
- `PUT /api/user/password` - 修改登录密码
- `GET /api/user/stats` - 获取学习时长统计（按日/周/月进行图表可视化数据）
- `GET /api/user/violations` - 获取违约记录列表

### 1.6 常用座位配置 (`/api/favorites`)
- `GET /api/favorites` - 获取我的常用座位列表
- `POST /api/favorites` - 添加常用自习室和座位
- `DELETE /api/favorites/{id}` - 取消收藏
- `POST /api/favorites/{id}/quick-reserve` - 一键预约收藏座位

---

## 二、管理员端 API (`/api/admin`)

### 2.1 仪表盘统计 (`/api/admin/dashboard`)
- `GET /api/admin/dashboard/stats` - 获取今日预约量、用户总数等核心数字指标
- `GET /api/admin/dashboard/utilization` - 获取座位利用率及时段分布数据
- `GET /api/admin/dashboard/users-trend` - 获取新增用户曲线数据

### 2.2 自习室管理 (`/api/admin/rooms`)
- `GET /api/admin/rooms` - 获取自习室列表（后台管理用）
- `POST /api/admin/rooms` - 新增自习室
- `PUT /api/admin/rooms/{id}` - 更新自习室信息
- `DELETE /api/admin/rooms/{id}` - 删除自习室（及批量删除）

### 2.3 座位管理 (`/api/admin/seats`)
- `POST /api/admin/rooms/{id}/seats/batch` - 批量新增/生成某自习室座位
- `PUT /api/admin/seats/{id}` - 修改单个座位属性（编号、靠窗、电源、状态等）
- `DELETE /api/admin/seats` - 批量删除座位
- `PUT /api/admin/seats/status` - 批量修改座位状态（如设置为维修中）

### 2.4 预约记录管理 (`/api/admin/reservations`)
- `GET /api/admin/reservations` - 获取所有预约记录（支持多条件高级筛选，如用户、时间、状态）
- `PUT /api/admin/reservations/{id}/cancel` - 后台强制取消预约
- `GET /api/admin/reservations/export` - 导出 Excel 格式的预约数据报表

### 2.5 用户管理 (`/api/admin/users`)
- `GET /api/admin/users` - 获取所有用户信息列表
- `GET /api/admin/users/{id}` - 查看用户详情（含预约与违约记录）
- `PUT /api/admin/users/{id}/status` - 禁用/启用用户账号

### 2.6 公告与轮播图管理 (`/api/admin/content`)
- `POST /api/admin/announcements` - 发布新公告
- `PUT /api/admin/announcements/{id}` - 编辑系统公告
- `PUT /api/admin/announcements/{id}/status` - 上下架公告
- `DELETE /api/admin/announcements/{id}` - 删除公告
- `POST /api/admin/carousels` - 上传并添加轮播图
- `PUT /api/admin/carousels/sort` - 更新轮播图排序
- `PUT /api/admin/carousels/{id}/status` - 启用禁用轮播图

### 2.7 系统配置管理 (`/api/admin/config`)
- `GET /api/admin/config` - 获取当前系统规则配置
- `PUT /api/admin/config` - 更新规则配置（预约时段、最短/最长时长、最晚取消时间、违约设置）
