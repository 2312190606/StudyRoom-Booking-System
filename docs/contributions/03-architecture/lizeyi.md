# 软件架构设计贡献说明
姓名：李泽亿
学号：2312190618
日期：2026-03-18

## 我完成的工作

### 1. 架构设计
- [x] 后端架构设计 (基于 Spring Boot 3.x 的分层架构)
- [x] 数据库设计 (详细表结构、主外键约束、索引优化)
- [x] 系统交互流程设计 (基于 RESTful API 规范)

### 2. 技术选型
- **后端架构设计**: 选用 Java 17 + Spring Boot 3.x，利用新版特性提升性能与安全性；引入 MyBatis-Plus 简化持久层开发；选用 Spring Security + JWT 实现无状态鉴权。
- **数据库选择**: 选用 MySQL 8.x 作为主存储，Redis 作为高性能缓存及并发控制工具（处理预约锁）。

### 3. 环境搭建
- [x] 后端项目初始化 (Maven 依赖配置、Boot 启动类及标准目录结构搭建)
- [x] 数据库连接配置 (多环境 application-dev.yml / application-prod.yml 隔离)
- [x] gemini.md 编写 (AI 辅助开发规范及技术栈约束)

### 4. 文档编写
- [x] backend.md (后端开发规范与运行指南)
- [x] database.md (含 Mermaid ER 图与表设计细节)
- [x] api.md (前后端接口契约定义)
- [x] gemini.md (项目 AI 辅助开发配置)

## PR 链接
- Branch: [feature/lzy-backend-docs](https://github.com/2312190606/StudyRoom-Booking-System/tree/feature/lzy-backend-docs)

## 遇到的问题和解决
1. 问题：初期 Mermaid 语法在部分 Markdown 编辑器下无法显示 ER 图。
   解决：优化了 Mermaid 语法，补全了所有引用的实体定义，并移除了可能引发解析错误的复杂字段注释。
2. 问题：后端初始化时需兼容多种部署环境（本地与 Docker）。
   解决：采用 Spring Profiles 机制（dev/prod）进行配置隔离。

## 心得体会
在这次架构设计过程中，我深刻理解了“契约先行”的重要性。通过先定义 `api.md` 和 `database.md`，前后端的协作边界变得非常清晰。同时，利用 `gemini.md` 建立 AI 开发规范，能够极大提升后续编码阶段的自动化效率和代码一致性。
