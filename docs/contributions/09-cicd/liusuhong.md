# CI/CD 配置贡献说明

刘苏鸿 2312190606 / 2026-04-29

## 一、完成的 CI/CD 工作

### 1. GitHub Actions CI 配置
- [x] 完成
- `.github/workflows/ci.yml` - CI 流水线配置文件
  - backend job (Maven + Java 17 + Jacoco)
  - frontend job (Node.js 20 + ESLint + Jest)

### 2. Lint 配置
- [x] 完成
- `frontend/.eslintrc.js` - ESLint 配置文件
- `frontend/package.json` - 添加 lint 脚本

### 3. Codecov 配置
- [x] 完成
- backend flag (Maven Jacoco)
- frontend flag (Jest coverage)

### 4. README CI 徽章
- [x] 完成
- CI 徽章
- Backend Coverage 徽章
- Frontend Coverage 徽章

## 二、PR 链接

- PR #XX：https://github.com/2312190606/StudyRoom-Booking-System/pull/XX

## 三、CI 运行链接

- https://github.com/2312190606/StudyRoom-Booking-System/actions

## 四、心得体会

1. **GitHub Actions 配置**：学会了创建 `.github/workflows/ci.yml` 配置文件，配置多个 job 实现前后端分离 CI

2. **ESLint 集成**：为 Vue 3 项目配置 ESLint，并在 CI 中运行 lint 检查

3. **Codecov Flags**：使用 flags 区分 backend 和 frontend 覆盖率报告

4. **CI/CD 流水线**：实现了代码提交后自动运行测试、lint、覆盖率报告的完整流水线
