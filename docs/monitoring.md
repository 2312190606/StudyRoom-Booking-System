# 监控配置说明

## 概述

本项目为自习室座位预约系统后端配置了完整的监控体系，包括日志管理、健康检查和指标收集。

## 1. 日志管理

### 结构化日志

使用 `logstash-logback-encoder` 输出 JSON 格式日志，便于日志收集和分析。

**配置文件：** `backend/src/main/resources/logback-spring.xml`

**日志格式示例：**
```json
{
  "@timestamp": "2026-05-28T12:00:00.000+08:00",
  "level": "INFO",
  "message": "用户登录成功",
  "logger": "com.example.studyroom.service.AuthService",
  "application": "study-room-backend",
  "userId": 1,
  "traceId": "abc123"
}
```

**日志特性：**
- 控制台输出 JSON 格式（方便容器日志收集）
- 文件输出按天滚动，保留 30 天
- 异步写入避免阻塞
- 包含 MDC 上下文（userId、traceId）

### 日志级别

| 包名 | 级别 |
|------|------|
| com.example.studyroom | DEBUG |
| org.springframework | INFO |
| org.mybatis | INFO |

## 2. 健康检查端点

### 端点路径

- 自定义端点：`GET /health`
- Actuator 端点：`GET /actuator/health`

### 响应示例

```json
{
  "status": "UP",
  "timestamp": "2026-05-28T12:00:00.000Z",
  "application": "study-room-backend",
  "port": "8080"
}
```

### actuator 健康检查详情

通过 `/actuator/health` 可以查看各组件健康状态：
- Database（MySQL）连接状态
- Redis 连接状态
- Disk 空间
- JVM 状态

## 3. 指标收集

### 可用端点

| 端点 | 说明 |
|------|------|
| `/actuator/metrics` | 所有指标列表 |
| `/actuator/metrics/http.server.requests` | HTTP 请求指标 |
| `/actuator/metrics/jvm.memory.used` | JVM 内存使用 |
| `/actuator/metrics/process.cpu.usage` | CPU 使用率 |

### 关键指标

**HTTP 请求指标：**
```bash
curl http://localhost:8080/actuator/metrics/http.server.requests
```

**响应示例：**
```json
{
  "name": "http.server.requests",
  "measurements": [
    {"statistic": "COUNT", "value": 1523},
    {"statistic": "TOTAL_TIME", "value": 45.32},
    {"statistic": "MAX", "value": 0.235}
  ],
  "availableTags": [
    {"tag": "method", "values": ["GET", "POST"]},
    {"tag": "status", "values": ["200", "400", "500"]}
  ]
}
```

**指标说明：**
- `COUNT`: 请求次数
- `TOTAL_TIME`: 总响应时间（秒）
- `MAX`: 最大响应时间（秒）

### 常用查询

```bash
# 查看 500 错误数
curl "http://localhost:8080/actuator/metrics/http.server.requests?tag=status:500"

# 查看特定端点响应时间
curl "http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/auth/login"
```

## 4. 告警规则（可选扩展）

### Prometheus + AlertManager 告警规则示例

```yaml
groups:
  - name: studyroom-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status="500"}[5m]) > 0.05
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"

      - alert: ServiceDown
        expr: up{job="studyroom-backend"} == 0
        for: 30s
        labels:
          severity: critical
        annotations:
          summary: "Service is down"
```

## 5. 依赖配置

### Maven 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DEEPSEEK_API_KEY` | DeepSeek API 密钥 | - |

## 6. 访问地址汇总

| 服务 | 地址 |
|------|------|
| 健康检查 | `GET /health` |
| Actuator 健康 | `GET /actuator/health` |
| 指标 | `GET /actuator/metrics` |
| 日志文件 | `logs/application.json` |