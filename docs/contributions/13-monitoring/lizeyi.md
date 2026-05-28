# 监控配置贡献说明

姓名：李泽亿
学号：2312190618
日期：2026-05-28

## 我完成的工作

### 1. 日志配置
- [x] 结构化日志格式（使用 logstash-logback-encoder 输出 JSON 格式）
- [x] 日志级别配置（DEBUG/INFO 分级控制）
- [x] 日志滚动配置（按天滚动，保留 30 天）
- [x] 异步写入避免阻塞

### 2. 健康检查
- [x] /health 端点实现（增强版，返回 status、timestamp、application、port）
- [x] Actuator 健康检查端点（/actuator/health）
- [x] SecurityConfig 配置 /actuator/** 路径允许匿名访问

### 3. 指标收集
- [x] Spring Boot Actuator 集成
- [x] HTTP 请求指标（/actuator/metrics/http.server.requests）
- [x] JVM 内存指标（/actuator/metrics/jvm.memory.used）
- [x] CPU 使用率指标（/actuator/metrics/process.cpu.usage）

### 4. 文档
- [x] 编写 `docs/monitoring.md` 监控配置说明文档

## 修改文件列表

| 文件 | 说明 |
|------|------|
| `backend/pom.xml` | 添加 actuator、logstash-logback-encoder 依赖 |
| `backend/src/main/resources/logback-spring.xml` | 新建，配置 JSON 格式日志 |
| `backend/src/main/resources/application.yml` | 添加 management 配置 |
| `backend/src/main/java/.../controller/HealthController.java` | 增强 /health 端点 |
| `backend/src/main/java/.../config/SecurityConfig.java` | 开放 /actuator/** 路径 |
| `docs/monitoring.md` | 监控配置说明文档 |

## PR 链接
- 待提交后补充

## 遇到的问题和解决

1. **问题：Actuator 端点被 Security 拦截**
   - 解决：在 SecurityConfig 中添加 `/actuator/**` 到 permitAll 列表

2. **问题：JaCoCo 与 Java 23 不兼容导致测试失败**
   - 解决：升级 byte-buddy 版本并添加 `-Dnet.bytebuddy.experimental=true` JVM 参数

3. **问题：Controller 测试缺少 DataSource bean**
   - 解决：为测试类添加 `@MockBean DataSource dataSource`

## 心得体会

本次监控配置作业让我深入学习了 Spring Boot 的可观测性体系：

1. **日志管理**：通过 logstash-logback-encoder 实现结构化 JSON 日志，便于后续日志收集和分析（如 ELK 栈）

2. **健康检查**：区分了应用自定义的 /health 端点和 Spring Boot Actuator 的 /actuator/health 端点，前者返回简洁的状态信息，后者返回详细的组件健康状态

3. **指标收集**：利用 Micrometer（Actuator 内置）自动收集 HTTP 请求、JVM 内存等指标，无需额外代码

4. **Security 配置**：注意将监控端点加入白名单，否则会被认证拦截

这些监控能力为生产环境运维提供了基础保障。