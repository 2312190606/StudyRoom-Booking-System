# 安全审查报告

**审查日期**: 2026-05-06
**审查人**: AI 安全审查
**项目**: StudyRoom 后端服务

---

## 第一步：AI 辅助安全审查

### 审查代码范围
- 认证服务: `AuthService.java`
- JWT 工具类: `JwtUtils.java`
- 安全配置: `SecurityConfig.java`
- 预约控制器: `ReservationController.java`

### 发现的问题

#### 问题1: JWT 默认密钥硬编码 [高危]

**文件**: `JwtUtils.java:20`

**问题描述**: JWT 密钥在配置中包含硬编码的默认弱密钥，如果环境变量未配置，应用会使用不安全的默认密钥。

**原代码**:
```java
@Value("${studyroom.jwt.secret:studyroom_secret_key_12345678901234567890}")
private String secret;
```

**修复后**:
```java
@Value("${studyroom.jwt.secret}")
private String secret;
```

**风险**: 攻击者可能利用默认密钥伪造任意用户 JWT token。

---

#### 问题2: Refresh Token 接口空指针异常 [中危]

**文件**: `AuthController.java:42-46`

**问题描述**: `@RequestHeader("Authorization")` 在缺少 Authorization 头时传入 null，可能导致 NPE。

**原代码**:
```java
@PostMapping("/refresh")
public Result<String> refreshToken(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    return Result.success(authService.refreshToken(token));
}
```

**修复后**:
```java
@PostMapping("/refresh")
public Result<String> refreshToken(@RequestHeader("Authorization") String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new BaseException("无效的认证头");
    }
    String token = authHeader.replace("Bearer ", "");
    return Result.success(authService.refreshToken(token));
}
```

**风险**: 未授权用户可能通过构造特殊请求触发 500 错误。

---

#### 问题3: 潜在 IDOR 漏洞 - 预约详情接口 [中危]

**文件**: `ReservationController.java:53-56`

**问题描述**: `getReservation` 方法未校验当前用户是否有权限查看该预约，可能导致越权访问。

```java
@GetMapping("/{id}")
public Result<Reservation> getReservation(@PathVariable Long id) {
    return Result.success(reservationService.getReservationById(id));
}
```

**建议修复**: 在 `ReservationService.getReservationById` 中添加用户归属校验。

---

## 第二步：安全检查清单

### 认证与授权

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 密码存储使用 bcrypt | ✅ | `SecurityConfig` 配置 `BCryptPasswordEncoder` |
| JWT 有过期时间 | ✅ | 默认 24 小时过期 |
| 接口鉴权 | ✅ | Spring Security 配置所有请求需认证 |
| 越权访问防护 | ⚠️ | 部分接口（如 getReservation）需加强校验 |

### 注入防护

| 检查项 | 状态 | 说明 |
|--------|------|------|
| SQL 参数化查询 | ✅ | 使用 MyBatis-Plus 的 LambdaQueryWrapper |
| XSS 防护 | N/A | 后端 API，不涉及前端输出 |

### 敏感信息

| 检查项 | 状态 | 说明 |
|--------|------|------|
| API Key/密码不硬编码 | ✅ | 使用环境变量 `${DEEPSEEK_API_KEY:}` |
| .env 文件已加入 .gitignore | ✅ | `.env.example` 作为模板 |

### 依赖安全

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 依赖扫描 | ⚠️ | 已配置 Dependabot，需定期检查告警 |

---

## 第三步：CI 自动化安全扫描

已配置 GitHub Actions 安全扫描: `.github/workflows/security.yml`

使用 gitleaks 进行密钥泄露扫描，在每次 push 和 pull_request 时自动运行。

---

## 修复记录

| 日期 | 问题 | 修复文件 | 状态 |
|------|------|----------|------|
| 2026-05-06 | JWT 默认密钥硬编码 | `JwtUtils.java` | ✅ 已修复 |
| 2026-05-06 | Refresh Token 空指针 | `AuthController.java` | ✅ 已修复 |
| 2026-05-06 | IDOR 漏洞（预约详情） | `ReservationController.java` | ⚠️ 待修复 |

---

## 结论

本次安全审查发现 2 处已修复的高/中危漏洞，发现 1 处待处理的 IDOR 漏洞。项目整体安全状况良好，密码存储、SQL 注入防护等关键机制已正确实现。建议后续修复 IDOR 漏洞并添加接口限流防止暴力破解。
