# CI/CD 贡献说明

刘苏鸿 2312190606 / 2026-04-29

## 一、我完成的工作

### 1. 工作流相关
- [x] `.github/workflows/ci.yml` - CI 流水线配置
- [x] Codecov backend / frontend flag
- [x] README CI 徽章

### 2. 代码适配
- [x] ESLint 配置
- [x] 覆盖率 > 60%

### 3. 可选项
- [ ] Dependabot
- [ ] CodeRabbit AI
- [ ] act

## 二、PR 链接

- PR #XX：待创建

## 三、CI 运行链接

- https://github.com/2312190606/StudyRoom-Booking-System/actions

## 四、心得体会

1. **GitHub Actions 配置**：学会了创建 `.github/workflows/ci.yml` 配置文件，配置 backend 和 frontend 两个 job 实现分离 CI

2. **ESLint 集成**：为 Vue 3 项目配置 ESLint v10，并在 CI 中运行 lint 检查

3. **Codecov Flags**：使用 flags 区分 backend 和 frontend 覆盖率报告

4. **CI/CD 流水线**：实现了代码提交后自动运行测试、lint、覆盖率报告的完整流水线
