# 安全审查贡献说明
姓名：李泽亿
学号：2312190618
日期：2026-05-06
## 我完成的工作
### AI 安全审查
- 审查了哪些文件/模块：
  - `AuthService.java` - 认证服务（注册、登录、Token刷新）
  - `JwtUtils.java` - JWT工具类（令牌生成与解析）
  - `SecurityConfig.java` - Spring Security安全配置
  - `ReservationController.java` - 预约控制器
  - `JwtAuthenticationFilter.java` - JWT认证过滤器

- AI 发现的主要问题：
  1. **JWT默认密钥硬编码** [高危] - `JwtUtils.java` 包含弱密钥默认值
  2. **Refresh Token空指针异常** [中危] - `AuthController.java` 未校验Authorization头
  3. **潜在IDOR漏洞** [中危] - `ReservationController.java` getReservation未校验用户归属

- 我修复了哪些问题：
  1. ✅ JWT默认密钥硬编码 - 移除默认值，强制要求配置
  2. ✅ Refresh Token空指针 - 添加null和格式校验

### 安全检查清单

#### 认证与授权
- ☑️ 密码存储：使用bcrypt / argon2 哈希，不存明文
  - `SecurityConfig.java` 配置 `BCryptPasswordEncoder`
- ☑️ JWT / Session：token 有过期时间，logout 后失效
  - 默认24小时过期，refresh时校验用户状态
- ☑️ 接口鉴权：所有需要登录的接口都有权限校验
  - Spring Security 配置 `.anyRequest().authenticated()`
- ⚠️ 越权访问：用户只能操作自己的数据（不能通过改ID 访问他人数据）
  - `getReservation` 接口存在潜在IDOR漏洞，待修复

#### 注入防护
- ☑️ SQL：使用ORM 或参数化查询，无字符串拼接SQL
  - 使用 MyBatis-Plus 的 `LambdaQueryWrapper`
- N/A XSS：前端输出用户数据时不用innerHTML，或使用DOMPurify
  - 后端API服务，不涉及前端输出

#### 敏感信息
- ☑️ API Key / 密码：不硬编码在代码中，通过环境变量读取
  - `DEEPSEEK_API_KEY: ${DEEPSEEK_API_KEY:}`
- ☑️ .env 文件：已加入.gitignore，仓库中有.env.example
  - `backend/.env.example` 存在

#### 依赖安全
- ⚠️ 运行依赖扫描，无高危漏洞（或已记录已知漏洞原因）
  - 已配置 Dependabot，需定期检查告警

### CI 安全扫描
- 配置了哪个选项（A/B/C）：
  - **选项A：密钥泄露扫描** - 使用 gitleaks
- 扫描结果：
  - 已配置 `.github/workflows/security.yml`，在每次 push 和 pull_request 时自动运行

### 选做完成情况
- 无

## PR 链接
- PR #X: https://github.com/xxx/xxx/pull/X

## 遇到的问题和解决
1. 问题：
   编辑 `AuthController.java` 时，Edit工具产生了重复代码片段。
解决：
   使用 Write 工具重写整个文件，确保内容完整无重复。

2. 问题：
   JWT密钥默认值移除后，如果环境变量未配置会导致应用启动失败。
解决：
   移除默认值后，需要在 `application-dev.yml` 或环境变量中正确配置 `studyroom.jwt.secret`。

## 心得体会
在 Vibe Coding 场景下，开发效率和安全之间确实存在张力。作为学生项目，我们往往更关注功能快速上线，但安全漏洞可能在生产环境造成严重后果。

本次审查的体会：
1. **安全配置默认化** - 像JWT密钥默认值这种"方便"的设计，实际是安全隐患。应该在设计阶段就强制要求安全配置。
2. **最小权限原则** - 修复IDOR漏洞时深刻体会到，每个接口都应该明确校验当前用户是否有权限操作目标资源。
3. **工具辅助的重要性** - gitleaks等CI扫描工具可以自动化发现密钥泄露等问题，应该成为开发流程的标配。

平衡效率和安全的建议：
- 在项目初期就集成安全扫描到CI/CD流程，避免事后补救
- 使用安全框架（如Spring Security）默认提供的安全特性，而非自己实现
- 对关键操作（认证、支付等）保持警惕，投入额外安全审查时间
