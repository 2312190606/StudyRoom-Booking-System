# 后端开发贡献说明
姓名：厉泽毅
学号：2312190606
日期：2026-04-05
## 我完成的工作
### API 实现
- [x] 用户认证 API（注册 / 登录 / JWT 校验）
- [x] 业务资源 1 CRUD：座位预约管理 (Reservation Controller)
- [x] 业务资源 2 CRUD：自习室信息配置 (Study Room Controller)
- [x] 统一错误响应 (GlobalExceptionHandler 封装)
### 数据库
- [x] 数据模型定义（基于 MyBatis-Plus 的 Entity 实体类）
- [x] ORM 配置 (MyBatis-Plus 配置与数据库连接池优化)
- [x] 数据库迁移脚本 (schema.sql 初始化脚本编写)
### 部署
- [x] Dockerfile 编写 (采用 Maven 多阶段构建，提高镜像安全性与效率)
- [x] docker-compose.yml 配置 (编排 后端、MySQL、Redis 三个容器)
- [x] 本地联调验证 (解决端口冲突并验证容器间通讯正常)

## PR 链接
- PR #1: https://github.com/2312190606/StudyRoom-Booking-System/tree/feature/lzy-backend-docs

## 遇到的问题和解决
1. **问题：OpenJDK 基础镜像不可用**
   - **描述**：构建 Docker 镜像时提示 `openjdk:17-jdk-slim: not found`，原因是该镜像已被官方弃用。
   - **解决**：更换为 `eclipse-temurin:17-jre-jammy` 镜像，更精简且持续维护。
2. **问题：宿主机 MySQL 端口冲突**
   - **描述**：启动 Docker 时提示 `3306` 端口被占用（本地已安装 MySQL 服务）。
   - **解决**：修改 `docker-compose.yml`，将宿主机映射端口改为 `3307`，避免直接冲突，内部通讯仍保持 `3306`。

## 心得体会
在本次开发工作中，我深刻体会到了 **Docker 化部署** 的巨大优势。通过编写 `Dockerfile` 和 `docker-compose.yml`，我实现了项目的“一键环境搭建”，极大地提高了团队协作和部署的效率。同时，我也学习了如何使用 **MyBatis-Plus** 高效地处理数据库业务，并结合 **Spring Security + JWT** 实现了更安全的认证机制。面对镜像过时和端口冲突等实际部署问题，我学会了通过查看日志和排查宿主机状态来定位并解决问题。
