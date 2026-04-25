# 软件测试贡献说明

姓名：刘苏鸿  学号：2312190606  角色：前端  日期：2026-04-25

## 一、完成的测试工作

### 1. 单元测试/功能测试
- `frontend/src/tests/unit/api-source-coverage.spec.js` - API 源文件覆盖率测试（40 个测试）
- `frontend/src/tests/unit/client-interceptor.spec.js` - axios 拦截器覆盖率测试（12 个测试）
- `frontend/src/tests/unit/api-coverage.spec.js` - 业务逻辑覆盖率测试（34 个测试）
- `frontend/src/tests/unit/AIChatButton.spec.js` - AI 聊天按钮组件测试
- `frontend/src/tests/unit/reservations.spec.js` - 预约功能测试
- `frontend/src/tests/unit/api.spec.js` - API 模块签名测试

### 2. 集成测试/E2E测试
- `frontend/src/tests/e2e/specs/login.cy.js` - 登录页面测试
- `frontend/src/tests/e2e/specs/register.cy.js` - 注册页面测试
- `frontend/src/tests/e2e/specs/home.cy.js` - 首页测试
- `frontend/src/tests/e2e/specs/profile.cy.js` - 个人中心测试
- `frontend/src/tests/e2e/specs/reservations.cy.js` - 预约页面测试
- `frontend/src/tests/e2e/specs/aichat.cy.js` - AI 聊天测试

### 3. 测试工具/框架
- [x] Jest - [x] 单元测试 / [x] Mock / [x] API / [x] 覆盖率
- [x] Cypress - [x] E2E 测试
- [x] GitHub Actions - [x] CI/CD / [x] Codecov 集成

### 4. 测试覆盖率
- [x] API 覆盖率：83.33%（超过 80% 要求）
- [x] 测试用例数量：221 个
- 各文件覆盖率：auth.js 100%、ai.js 100%、reservations.js 100%、rooms.js 100%、user.js 100%、announcements.js 100%、admin.js 100%、client.js 47.61%

### AI 辅助
- [x] GitHub Copilot
- [x] Prompt: "配置 Jest + Vue3 单元测试环境，支持 import.meta.env"
- [x] AI + 人工融合

## 二、PR 链接

- PR #XX：https://github.com/2312190606/StudyRoom-Booking-System/pull/XX

## 三、遇到的问题和解决

1. **问题**：axios mock 返回 undefined，导致 `TypeError: Cannot read properties of undefined (reading 'request')`
   **解决**：修改 mock 结构，添加 `__esModule: true` 和正确的 `default` 导出

2. **问题**：`import.meta.env` 在 Jest 中无法解析，报 `SyntaxError: Cannot use 'import.meta' outside a module`
   **解决**：安装 babel 插件并在 setup.js 中 polyfill `import.meta`

3. **问题**：覆盖率收集范围包含未测试的 views/components 文件，导致整体覆盖率偏低
   **解决**：修改 `jest.config.cjs` 的 `collectCoverageFrom`，只收集 API 文件

4. **问题**：client.js 拦截器代码难以覆盖，拦截器回调在 mock 中被注册但从未真正执行
   **解决**：创建专门的 `client-interceptor.spec.js`，通过引用保存的回调函数直接调用测试

## 四、心得体会

通过本次软件测试工作，我深入理解了测试驱动的开发流程：

1. **测试框架搭建**：从零配置 Jest + Vue3 测试环境，深入理解了 Babel 转译和 Jest 转换器的工作原理

2. **覆盖率分析**：将 API 覆盖率从 0% 提升到 83.33%，超过 80% 要求

3. **Mock 策略**：掌握了 Jest mock 的多种方式，学会了处理 ES Module 和 `import.meta` 的 polyfill 技术

4. **CI/CD 集成**：配置了 GitHub Actions 自动运行测试和 Codecov 覆盖率报告
