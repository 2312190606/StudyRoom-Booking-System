# 监控配置说明

## 概述

本项目为自习室座位预约系统配置了完整的可观测性监控体系，包括结构化日志、健康检查、指标收集和错误追踪。

## 1. 日志管理

### 结构化日志配置

使用 Logstash Logback Encoder 将日志输出为 JSON 格式，便于日志收集和分析。

**配置文件**: `backend/src/main/resources/logback-spring.xml`

- **控制台输出**: JSON 格式，包含 traceId、userId 等 MDC 字段
- **文件输出**: 按日期和大小滚动，保留 30 天最多 3GB
- **日志级别**: DEBUG 级别记录业务日志

### 使用示例

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public void someMethod() {
        log.info("User logged in, userId={}", userId);
        log.error("Failed to process request", exception);
    }
}
```

## 2. 健康检查端点

### `/health` 端点

返回服务健康状态:

```json
{
  "status": "UP",
  "timestamp": "2026-05-28T10:30:00Z",
  "version": "1.0.0",
  "application": "study-room-backend"
}
```

**配置位置**: `backend/src/main/java/.../controller/HealthController.java`

## 3. 基础指标收集

### Actuator + Prometheus 端点

通过 Spring Boot Actuator 和 Micrometer 收集指标:

| 端点 | 说明 |
|------|------|
| `/actuator/health` | 健康检查 |
| `/actuator/info` | 应用信息 |
| `/actuator/metrics` | 所有指标 |
| `/actuator/prometheus` | Prometheus 格式指标 |

### 主要指标

- HTTP 请求计数和响应时间
- JVM 内存和 GC 指标
- 数据库连接池指标
- Redis 连接指标

**配置位置**: `backend/src/main/resources/application.yml`

## 4. 错误追踪 (可选)

### Sentry 集成

配置 Sentry DSN 可启用错误追踪:

```bash
export SENTRY_DSN=https://xxx@sentry.io/xxx
```

**依赖**: `sentry-spring-boot-starter`

## 5. 目录结构

```
backend/
├── src/main/
│   ├── java/com/example/studyroom/
│   │   ├── config/
│   │   │   └── MetricsConfig.java    # 指标配置
│   │   └── controller/
│   │       └── HealthController.java # 健康检查
│   └── resources/
│       ├── logback-spring.xml        # 日志配置
│       └── application.yml           # 监控配置
```

## 6. 测试验证

```bash
# 健康检查
curl http://localhost:8080/health

# Prometheus 指标
curl http://localhost:8080/actuator/prometheus
```