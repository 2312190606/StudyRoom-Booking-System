# 自习室座位预约系统

[![codecov](https://codecov.io/gh/2312190606/StudyRoom-Booking-System/branch/main/graph/badge.svg)](https://codecov.io/gh/2312190606/StudyRoom-Booking-System)
[![Test Coverage](https://github.com/2312190606/StudyRoom-Booking-System/actions/workflows/test.yml/badge.svg)](https://github.com/2312190606/StudyRoom-Booking-System/actions)
![Jest Tests](https://img.shields.io/badge/jest-178%20tests%20passed-brightgreen)

## 团队成员

| 姓名    | 学号        | 分工 |
|-------|-----------|------|
| 刘苏鸿   | 2312190606 | 前端开发 |
| 李泽亿   | 2312190618 | 后端开发 |

## 项目简介

自习室座位预约系统是一款面向高校学生和考研人群的移动端 Web 应用，旨在解决图书馆、自习室"一座难求"和座位资源分配不均的问题。系统提供实时座位状态查看、在线预约、扫码签到、学习时长统计等功能，帮助学生高效规划学习时间，提高自习室座位利用率。目标用户主要为在校大学生和需要长期使用自习室的学习者。

## 技术栈（初步规划）

- 前端：Vue 3 + Vant UI + Pinia + Axios
- 后端：Java + Spring Boot + MyBatis-Plus + JWT
- 数据库：MySQL

## Figma

 - 链接：https://iso-ruby-33600287.figma.site/

## 测试

### 单元测试 (Jest)

```bash
cd frontend
npm install
npm test
```

### E2E 测试 (Cypress)

```bash
# 终端1：启动开发服务器
npm run dev

# 终端2：运行 E2E 测试
npm run cypress:run
```

### 覆盖率报告

运行 `npm run test:coverage` 后，在浏览器中打开 `frontend/coverage/index.html` 查看详细覆盖率报告。

| 指标 | 数值 |
|------|------|
| 测试套件 | 5 passed |
| 测试用例 | 178 passed |
| API 覆盖率 | 50% |
| 覆盖文件 | API 核心源码 |
