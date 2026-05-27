# 云服务部署贡献说明
姓名：刘苏鸿
学号：2312190606
日期：2026-05-27

## 我完成的工作

### 1. 平台选择
- 前端使用平台：Vercel
- 后端使用平台：Railway

### 2. 部署配置
- [x] 配置文件编写（vercel.json）
- [x] 环境变量配置（VITE_BASE_API）
- [x] 自动部署配置

### 3. 问题解决
- 遇到的问题：前端登录请求返回"服务器错误"，API 请求未通过 Vercel 代理，而是直接请求后端 URL导致 405 Method Not Allowed
- 解决方案：
  1. 修改 .env.production 中 VITE_BASE_API 为 /api
  2. 在 Vercel 环境变量中设置 VITE_BASE_API=/api
  3. 配置 vercel.json rewrites 规则将 /api/* 请求代理到 Railway 后端

## 在线地址
- 前端：https://study-room-booking-system-ocun.vercel.app
- 后端：https://studyroom-booking-system-production.up.railway.app

## 心得体会
通过本次部署，我掌握了 Vercel 前端托管和 Railway 后端部署的配置方法，学会了排查 CORS、代理转发和环境变量配置问题。