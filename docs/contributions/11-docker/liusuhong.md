# Docker 部署贡献说明
姓名：刘苏鸿
学号：2312190606
日期：2026-05-15

## 我完成的工作
### 1. 前端 Dockerfile 编写
- [x] 前端 Dockerfile（多阶段构建：Node builder → Nginx 静态托管）
- [x] frontend/.dockerignore 文件

### 2. Nginx 配置
- [x] frontend/nginx.conf（SPA 路由支持、GZIP 压缩、缓存策略）

### 3. 自动化部署
- 选择：选项B（使用GitHub Actions）
- 具体内容：通过 GitHub Actions workflow 实现前端镜像自动构建并推送到 GHCR

## PR 链接
- PR #11: https://github.com/2312190606/StudyRoom-Booking-System/pull/11

## 遇到的问题和解决
1. 问题：Dockerfile 中未设置工作目录，导致 npm install 找不到 package.json
   解决：在 Dockerfile 中加入 `WORKDIR /app`，确保所有命令在正确目录下执行
2. 问题：Nginx 容器启动后访问前端页面刷新出现 404
   解决：在 nginx.conf 中配置 `try_files $uri $uri/ /index.html`，使所有路由回退到 index.html，解决 SPA 路由问题

## AI 使用情况
- 使用了哪些Prompt：如何编写前端多阶段 Dockerfile、nginx.conf 中 SPA 路由配置方法、.dockerignore 最佳实践
- AI 帮助解决了哪些问题：生成了初始 Dockerfile 与 nginx.conf 模板，并指导排查 Nginx 404 问题

## 心得体会
通过本次前端 Docker 化实践，掌握了多阶段构建的精髓：用 Node 镜像完成构建、再用轻量 Nginx 镜像托管静态资源，使最终镜像体积显著减小。同时深入理解了 nginx.conf 对 SPA 项目的重要性，以及 .dockerignore 对加速构建、缩减上下文体积的作用。
