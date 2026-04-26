# 软件测试贡献说明

姓名：李泽亿  学号：2312190618  角色：后端  日期：2026-04-26

## 完成的测试工作

### 一、测试文件

#### Controller 层（API 接口测试）
- `backend/src/test/java/com/example/studyroom/controller/AuthControllerTest.java` - 7 个测试
- `backend/src/test/java/com/example/studyroom/controller/RoomControllerTest.java` - 6 个测试
- `backend/src/test/java/com/example/studyroom/controller/ReservationControllerTest.java` - 11 个测试
- `backend/src/test/java/com/example/studyroom/controller/admin/AdminRoomControllerTest.java` - 9 个测试

#### Service 层（单元测试）
- `backend/src/test/java/com/example/studyroom/service/AuthServiceTest.java` - 8 个测试
- `backend/src/test/java/com/example/studyroom/service/ReservationServiceTest.java` - 16 个测试
- `backend/src/test/java/com/example/studyroom/service/UserServiceTest.java` - 16 个测试
- `backend/src/test/java/com/example/studyroom/service/RoomServiceTest.java` - 4 个测试
- `backend/src/test/java/com/example/studyroom/service/AdminServiceTest.java` - 13 个测试
- `backend/src/test/java/com/example/studyroom/service/PublicServiceTest.java` - 4 个测试

### 二、测试清单

| 测试类型 | 数量 | 说明 |
|---------|------|------|
| **API 接口测试** | 33 | Controller 层，验证 HTTP 请求/响应 |
| **单元测试（Mock）** | 61 | Service 层，使用 `@Mock` + `@InjectMocks` |
| **总计** | **94** | 全部通过 |

#### 正常情况测试
- 注册成功、登录成功、刷新Token成功
- 创建预约成功、取消预约成功、签到成功、延长预约成功、结束学习成功
- 获取自习室列表成功、获取座位列表成功、获取我的预约列表成功
- 管理员：新增自习室、更新自习室、删除自习室、批量新增座位、批量更新座位状态

#### 异常情况测试
- 用户名已存在、密码错误、账号被禁用、Token 无效
- 预约时间冲突、座位已占用、座位不可用
- 预约状态不可操作（无法取消、无法签到、无法延长、无法结束）
- 信誉分边界（最高 100 分、最低 0 分）

### 三、Mock 使用

- 使用 `@Mock` 注解 Mock 数据库 Mapper 层（`UserMapper`、`ReservationMapper`、`SeatMapper`、`StudyRoomMapper` 等）
- 使用 `@InjectMocks` 注入待测试的 Service
- 使用 `@ExtendWith(MockitoExtension.class)` 启用 Mockito JUnit 5 扩展
- 使用 `@WebMvcTest` + `@MockBean` 进行 Controller 层测试
- 使用 `@AutoConfigureMockMvc(addFilters = false)` 禁用安全过滤器进行 API 测试

### 四、覆盖率

| 模块                 | 覆盖率 |
|----------------------|--------|
| service              | **60%** |
| controller           | 27%    |
| controller.admin     | 32%    |
| common               | 34%    |

- 核心模块（service）覆盖率达到 60% 要求

### 五、遇到的问题和解决

#### 问题 1：Java 23 与 Mockito ByteBuddy 不兼容
- **原因**：Byte Buddy 1.14.x 最高支持 Java 22
- **解决**：降级使用 Java 17 (jdk-17.0.19)，配置环境变量 `JAVA_HOME`

#### 问题 2：@WebMvcTest 加载 SecurityConfig 依赖失败
- **原因**：`JwtAuthenticationFilter` 依赖 `JwtUtils` Bean，Context 加载失败
- **解决**：添加 `@MockBean private JwtUtils jwtUtils;` 和 `@AutoConfigureMockMvc(addFilters = false)`

#### 问题 3：Controller 测试返回 403/401
- **原因**：Spring Security 拦截请求
- **解决**：使用 `@AutoConfigureMockMvc(addFilters = false)` 禁用安全过滤器

#### 问题 4：JaCoCo 执行数据文件丢失
- **原因**：Windows 路径特殊字符 + Surefire 插件 argLine 配置问题
- **解决**：配置 JaCoCo `destFile` 到 `C:/temp/jacoco.exec`，使用 `@{argLine}` 合并参数

### 六、AI 辅助

- 使用工具：Claude Code
- Prompt 示例：
  - "为 AuthService 创建单元测试，覆盖注册、登录、刷新Token的正常和异常场景"
  - "修复 Controller 测试中的 403 错误，使用 @AutoConfigureMockMvc(addFilters = false)"
  - "解决 JaCoCo 覆盖率数据文件丢失问题"

## PR 链接
- 本地测试通过，PR：https://github.com/2312190606/StudyRoom-Booking-System/compare/feature/lzy-backend-docs?expand=1

## 心得体会

通过本次测试工作，深入理解了 Spring Boot 单元测试的最佳实践：
- 使用 `@ExtendWith(MockitoExtension.class)` 实现真正的单元测试，隔离数据库依赖
- `@Mock` 和 `@InjectMocks` 的配合使用让测试更加独立
- Java 23 虽然是新版本，但部分生态（如 Mockito）尚未完全兼容，选择 LTS 版本更为稳妥
- 测试分层：单元测试关注业务逻辑，API 测试需要更完整的集成环境
- `@WebMvcTest` 结合 `@MockBean` 可以方便地测试 Controller 层，但需要注意 Spring Security 配置
- JaCoCo 覆盖率统计需要正确配置 agent 参数，在 Windows 环境下需要特别注意路径问题
