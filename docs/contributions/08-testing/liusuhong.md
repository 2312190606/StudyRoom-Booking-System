# 测试工作贡献说明

姓名：刘苏鸿
学号：2312190606
日期：2026-04-25

## 我完成的工作

### 1. Jest 单元测试框架搭建

- [x] Jest 配置文件 (`jest.config.cjs`)
  - 配置 `jsdom` 测试环境
  - 配置 `@vue/vue3-jest` 转换 Vue 单文件组件
  - 配置 `babel-jest` 转换 JavaScript 文件
  - 设置 `setupFilesAfterEnv` 加载测试环境配置

- [x] Jest Setup 文件 (`src/tests/unit/setup.js`)
  - Mock `localStorage` 模拟浏览器存储
  - Mock `vant` UI 组件库
  - Mock `axios` HTTP 客户端
  - Mock `vue-router` 路由系统
  - Polyfill `import.meta.env` 支持 Vite 环境变量

### 2. 单元测试文件开发

- [x] `api-source-coverage.spec.js` - API 源文件覆盖率测试
  - 测试 `auth.js` 所有导出函数（register、login、refreshToken）
  - 测试 `user.js` 所有导出函数（getUserProfile、updateUserProfile、updatePassword 等）
  - 测试 `reservations.js` 所有导出函数（createReservation、cancelReservation、checkIn 等）
  - 测试 `rooms.js` 所有导出函数（getRooms、getRoomDetail、getRoomSeats）
  - 测试 `ai.js` 导出函数（askAI）
  - 测试 `announcements.js` 所有导出函数
  - 测试 `admin.js` 所有导出函数（20 个管理 API 函数）

- [x] `client-interceptor.spec.js` - axios 拦截器覆盖率测试
  - 测试请求拦截器的 token 自动注入逻辑
  - 测试响应拦截器的数据解包逻辑
  - 测试错误处理和 Toast 提示

- [x] `api-coverage.spec.js` - 业务逻辑覆盖率测试
  - 预约状态常量测试（PENDING、IN_USE、CANCELLED、COMPLETED）
  - 信誉分规则测试（初始分、签到奖励、取消惩罚）
  - 座位状态枚举测试
  - 时间验证和时长计算测试
  - API 路径常量测试
  - 错误码定义测试

- [x] `AIChatButton.spec.js` - AI 聊天按钮组件测试

- [x] `reservations.spec.js` - 预约功能测试

- [x] `api.spec.js` - API 模块签名测试

### 3. Cypress E2E 测试开发

- [x] `e2e/specs/login.cy.js` - 登录页面测试
- [x] `e2e/specs/register.cy.js` - 注册页面测试
- [x] `e2e/specs/home.cy.js` - 首页测试
- [x] `e2e/specs/profile.cy.js` - 个人中心测试
- [x] `e2e/specs/reservations.cy.js` - 预约页面测试
- [x] `e2e/specs/aichat.cy.js` - AI 聊天测试
- [x] `e2e/support/commands.js` - 自定义 Cypress 命令
- [x] `e2e/support/index.js` - Cypress 配置文件

### 4. GitHub Actions CI/CD 配置

- [x] `.github/workflows/test.yml` - 测试工作流
  - Node.js 20 环境配置
  - 自动安装依赖
  - 运行覆盖率测试
  - 上传 coverage 报告
  - 集成 Codecov

### 5. Codecov 覆盖率配置

- [x] `codecov.yml` - Codecov 配置文件
  - 设置覆盖率精度和四舍五入规则
  - 配置 API 模块覆盖率目标（30%）
  - 配置 comment 布局

### 6. Babel 转译配置

- [x] `babel.config.cjs` - Babel 配置文件
  - 配置 `@babel/preset-env` 支持现代 JavaScript
  - 配置 `@babel/plugin-syntax-import-meta` 支持 `import.meta`
  - 配置 `babel-plugin-transform-vite-meta-env` 转换 Vite 环境变量

## 测试执行方式

### 单元测试

```bash
cd frontend
npm install
npm test              # 运行所有测试
npm run test:watch    # 监听模式运行
npm run test:coverage # 生成覆盖率报告
```

### E2E 测试

```bash
# 终端1：启动开发服务器
npm run dev

# 终端2：运行 E2E 测试
npm run cypress:run   # 无头模式运行
npm run cypress:open  # 打开 Cypress GUI
```

## 测试结果

### 最新覆盖率报告

| 指标 | 数值 |
|------|------|
| 测试套件 | 6 passed |
| 测试用例 | 221 passed |
| API 覆盖率 | **83.33%** |
| 分支覆盖率 | 38.88% |
| 函数覆盖率 | 95.91% |

### 各文件覆盖率

| 文件 | 覆盖率 |
|------|--------|
| auth.js | 100% |
| ai.js | 100% |
| reservations.js | 100% |
| rooms.js | 100% |
| user.js | 100% |
| announcements.js | 100% |
| admin.js | 100% |
| client.js | 47.61% |

## 遇到的问题和解决

### 问题 1：axios mock 返回 undefined

**现象**：`TypeError: Cannot read properties of undefined (reading 'request')`

**分析**：Jest mock 的 axios 没有正确模拟 `interceptors.request.use` 方法

**解决**：修改 mock 结构，确保 `axios.create()` 返回的实例包含完整的 `interceptors` 对象

```javascript
jest.mock('axios', () => ({
  __esModule: true,
  default: {
    create: jest.fn(() => mockAxiosInstance)
  }
}));
```

### 问题 2：`import.meta.env` 在 Jest 中无法解析

**现象**：`SyntaxError: Cannot use 'import.meta' outside a module`

**分析**：Jest 默认不支持 Vite 的 `import.meta.env` 语法

**解决**：
1. 安装 babel 插件：`@babel/plugin-syntax-import-meta` 和 `babel-plugin-transform-vite-meta-env`
2. 在 `babel.config.cjs` 中配置插件
3. 在 `setup.js` 中 polyfill `import.meta`

### 问题 3：覆盖率收集范围过大导致百分比偏低

**现象**：即使 API 模块全部覆盖，整体覆盖率仍然只有 50%

**分析**：`collectCoverageFrom` 配置包含了 views、components 等未测试的文件

**解决**：修改 `jest.config.cjs`，只收集 API 文件的覆盖率

```javascript
collectCoverageFrom: [
  'src/api/*.js',
  '!src/tests/**',
  // ... 其他排除
],
```

### 问题 4：client.js 拦截器代码难以覆盖

**现象**：拦截器回调函数在 mock 中被注册但从未真正执行

**分析**：拦截器逻辑在 `axios.create()` 调用时通过闭包保存，普通 mock 无法触发

**解决**：创建专门的 `client-interceptor.spec.js`，通过引用保存的回调函数直接调用测试

## PR 链接

- PR #XX：https://github.com/2312190606/StudyRoom-Booking-System/pull/XX

## 心得体会

通过本次测试工作，我学习了：

1. **测试框架搭建**：从零配置 Jest + Vue3 测试环境，深入理解了 Babel 转译和 Jest 转换器的工作原理
2. **覆盖率分析**：学会了分析覆盖率报告，识别未覆盖的代码分支，针对性编写测试用例
3. **Mock 策略**：掌握了 Jest mock 的多种方式（`jest.mock`、`moduleNameMapper`、setup 文件），学会了处理 ES Module 和 `import.meta` 的 polyfill
4. **CI/CD 集成**：配置了 GitHub Actions 自动运行测试和 Codecov 覆盖率报告，实现了测试的持续集成
5. **E2E 测试**：使用 Cypress 编写端到端测试，学会了自定义命令和页面对象模式
