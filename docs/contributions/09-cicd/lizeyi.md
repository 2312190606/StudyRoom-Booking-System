# CI/CD 配置贡献说明
姓名：李泽亿  学号：2312190618  角色：后端  日期：2026-04-29

## 完成的工作
### 工作流相关
- [x] 参与编写/审查 `.github/workflows/ci.yml`
  - [x] 配置 Codecov 覆盖率上传（backend/frontend flag）
  - [x] 添加 README 状态徽章
- [x] 修复后端 Codecov 覆盖率显示 unknown 问题
  - [x] 修复 `ReservationServiceTest.java` 缺失的 `UserService` import 导致编译失败
  - [x] 调整 JaCoCo `prepare-agent` 绑定阶段从 `test` 改为 `initialize`，确保 agent 在测试前附着

### 代码适配
- [x] 本地测试命令与 CI 一致（`mvn test`），使用 H2 内存数据库，无需额外配置
- [x] 核心覆盖率达标（> 60%）
- [x] 新增后端测试用例，覆盖以下模块：
  - `FavoriteService` + `FavoriteController`（收藏功能）
  - `UserController`（个人中心）
  - `PublicController`（公共信息）
  - `AIController`（AI 客服）
  - `AdminUserController`、`AdminReservationController`、`AdminDashboardController`、`AdminContentController`、`AdminConfigController`（后台管理）


## PR 链接
- PR: https://github.com/2312190606/StudyRoom-Booking-System/pull/X

## CI 运行链接
- https://github.com/2312190606/StudyRoom-Booking-System/actions

## 遇到的问题和解决
1. **问题**：后端 Codecov 显示 unknown，JaCoCo 覆盖率报告未生成
   - **原因**：`ReservationServiceTest.java` 缺少 `UserService` import，导致编译失败；surefire 静默跳过失败测试，无 jacoco.exec 输出
   - **解决**：添加缺失的 import，修复后覆盖率正常显示
2. **问题**：所有 Controller 测试在 Spring 上下文加载时报错
   - **原因**：部分 `@WebMvcTest` 测试缺少 `JwtAuthenticationFilter` 和 `RestTemplate` 的 MockBean 声明
   - **解决**：为所有 Controller 测试添加 `@MockBean JwtAuthenticationFilter` 和必要的依赖 mock
3. **问题**：JaCoCo agent 在测试阶段未能正确附着到 JVM
   - **原因**：`prepare-agent` goal 绑定到 `test` 阶段，执行顺序不确定
   - **解决**：将 `prepare-agent` 绑定到 `initialize` 阶段，确保在测试运行前就附着

## 心得体会
通过这次修复，深刻理解了 JaCoCo 覆盖率收集的原理——agent 必须在测试启动前附着到 JVM，否则无法收集数据。对于 Spring Boot 的 `@WebMvcTest` 切片测试，必须显式 mock 所有控制器依赖的 Bean（包括 Filter 和 RestTemplate 等），否则 Spring 上下文加载失败。测试覆盖率不仅是指标，更是代码质量的保障，新增测试覆盖了之前未验证的 Controller 层逻辑，提高了整体稳定性。
