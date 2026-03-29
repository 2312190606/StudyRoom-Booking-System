# API 设计与实现贡献说明
姓名：李泽亿
学号：2312190618
日期：2026-03-25

## 我完成的工作
### 1. API 设计
- [x] 用户认证 API (注册、登录、Token 鉴权)
- [x] 业务资源 API (自习室、座位、预约管理、个人中心)
- [x] 查询接口设计 (分页查询、多重筛选逻辑)
### 2. 文档编写
- [x] API 接口定义同步 (更新并对齐 docs/api.md)
- [x] API 使用说明 (代码注释与分层设计文档)
### 3. 前端实现
- [ ] HTTP 客户端配置 (待后续集成)
- [ ] API 调用函数封装 (待后续集成)
- [ ] Mock 数据配置 (待后续集成)
### 4. 后端实现
- [x] API 路由定义 (Spring Boot Controller 层)
- [x] 业务逻辑处理 (Service 层核心算法与事务管理)
- [x] 错误处理 (全局异常拦截与统一 Result 返回)
### 5. 测试
- [x] Postman 接口冒烟测试
- [x] 核心业务逻辑验证 (防冲突预约算法测试)
- [x] 测试用例数量：超过 15 个核心接口验证
## PR 链接
- PR Branch:https://github.com/2312190606/StudyRoom-Booking-System/compare/feature/lzy-backend-docs?expand=1
## 遇到的问题和解决
1. 问题：JJWT 0.12.3 的新版 API 在部分 IDE 插件下存在不兼容的语法警告。
   解决：回退并适配到更为通用的 JJWT "Classic" API (0.11.x 兼容风格)，确保了代码在不同构建环境下的稳定性和兼容性。
2. 问题：高并发场景下座位预约的冲突检测。
   解决：在 `ReservationService` 中实现了严密的时段重叠校验逻辑，并结合数据库事务保证了预约操作的原子性。
## 心得体会
在本次 API 实现过程中，我深入实践了 RESTful 架构风格和 Spring Boot 3 的高级特性。通过将业务逻辑解耦到 Service 层，并利用 MyBatis-Plus 简化持久层操作，极大地提升了开发效率。特别是安全模块的整合，让我对 JWT 无状态鉴权和 Spring Security 的过滤器链有了更深刻的理解。
