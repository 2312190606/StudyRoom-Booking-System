# StudyRoom AI 辅助开发配置 (gemini.md)

## 1. 项目规则 (Project Rules)

### 技术栈 (Tech Stack)
- **前端**: Vue 3 + Vant 4.x + Pinia + Axios + Vite
- **后端**: Java 17 + Spring Boot 3.x + MyBatis-Plus + Spring Security + JWT
- **数据库**: MySQL 8.x + Redis
- **部署**: Docker Compose

### 目录结构 (Directory Structure)
- **前端 (frontend/src/)**:
  - `api/`: API 接口定义。
  - `components/`: 可复用 UI 组件。
  - `views/`: 路由页面组件。
  - `stores/`: Pinia 状态管理。
  - `utils/`: 工具函数。
- **后端 (backend/src/main/java/...)**:
  - `controller/`: REST API 端点。
  - `service/`: 业务逻辑接口及实现。
  - `mapper/`: MyBatis 映射。
  - `model/entity/`: 数据库表映射实体。
  - `model/dto/`: 数据传输对象。
  - `model/vo/`: 视图展示对象。

### 代码规范 (Code Standards)
- **前端组件**: 必须使用 Vue 3 组合式 API (Composition API) 和 `<script setup>`。
- **样式处理**: 全面使用 Tailwind CSS 类名（配合 Vant UI），**禁止**内联样式。
- **统一响应**: API 响应必须符合后端定义好的 `Result` 通用封装类。
- **代码重构**: 优先复用 `components/` 下的现有组件。
- **类型安全**: **禁止**使用 `any` 类型。

### 开发禁令 (Prohibitions)
- **禁止** 使用 `any`。
- **禁止** 使用内联 CSS 样式。
- **禁止** 直接操作 DOM。
- **禁止** 修改系统配置文件 (除非在任务中明确要求)。

---

## 2. 核心功能说明 (Core Features)

本项目为“自习室座位预约系统”，旨在优化资源利用率。

- **用户侧**: 座位可视化浏览、在线预约(含时间段选择)、定位签到、学习时长统计、常用座位管理。
- **管理侧**: 数据可视化仪表盘、自习室与座位动态配置、预约记录运维、用户状态管理、黑名单/违约处理、公告与轮播图管理。

## 3. 开发指引 (Development Guidelines)

1. **State Management**: 前端建议使用 Pinia，确保状态逻辑清晰且持久化处理得当。
2. **Form Validation**: 推荐在后端使用 Spring Validation 并在前端进行相应校验。
3. **Database Migration**: 遵循项目的 SQL 初始化脚本进行表结构维护。
4. **Consistency**: 确保前端 API 调用与后端 Controller 路径及 DTO 结构严密匹配。

---

## 4. 辅助指令 (Assistant Instructions)

在进行代码生成或方案设计时，请务必遵守上述规则：
- 优先搜索 `src/components/` 下的现有组件进行复用。
- 在编写 API 接口前，先对齐后端 DTO/VO 数据模型。
- 创建新功能模块时，确保同时考虑业务逻辑的安全性（Spring Security）。
