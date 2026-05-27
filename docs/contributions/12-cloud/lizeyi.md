# 云服务部署贡献说明
姓名：李泽亿
学号：2312190618
日期：2026-05-27

## 我完成的工作
### 1. 平台选择
- 使用平台：Railway（后端）+ Vercel（前端）
### 2. 部署配置
- [x] 配置文件编写
  - 创建 `backend/railway.toml` 配置 Railway 构建
  - 创建 `frontend/vercel.json` 配置 Vercel 构建和 API 代理
- [x] 环境变量配置
  - Railway: DATABASE_URL, STUDYROOM_JWT_SECRET, PRODUCTION
  - Vercel: 无需额外配置（API 通过 rewrites 代理）
- [x] 自动部署配置
  - Railway 和 Vercel 均支持 GitHub 推送自动部署

### 3. 问题解决
- 遇到的问题：Railway 无法识别 Dockerfile
- 解决方案：将 railway.toml 放入 `backend/` 目录，并在 Railway 控制台设置 Root Directory 为 `backend`
- 遇到的问题：Vercel npm 依赖安装失败（peer eslint 版本冲突）
- 解决方案：使用 `--legacy-peer-deps` 参数或 `installCommand` 配置
- 遇到的问题：部署后页面 404（SPA 路由问题）
- 解决方案：添加 `/index.html` 重写规则支持 Vue Router History 模式
- 遇到的问题：API 请求 404（后端 URL 配置错误）
- 解决方案：确认 Railway 后端实际 URL 并更新 vercel.json 中的 rewrite 目标地址

## PR 链接
- （待 GitHub PR 创建后填写）

## 在线地址
- 前端：https://studyroom-booking-system.vercel.app
- 后端：https://studyroom-booking-system-production.up.railway.app

## 心得体会
本次云服务部署实践完成了前后端分离架构的线上部署。通过 Railway 部署 Spring Boot 后端服务，Vercel 部署 Vue 3 前端应用，利用 Vercel 的 rewrites 功能实现 API 请求代理。

主要收获：
1. 理解了两类云平台的不同定位：Vercel 擅长静态/前端托管，Railway 适合容器化后端服务
2. 学会了处理 SPA 应用的路由重写问题
3. 掌握了 Docker 多阶段构建在远程 CI 环境中的配置方法
4. 学会了排查依赖版本冲突和构建失败问题