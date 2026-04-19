# AI 功能后端实现贡献说明

姓名：李泽亿
学号：2312190618
日期：2026-04-19

## 我完成的工作

### 1. AI 控制器开发
- [x] AIController（`controller/AIController.java`）
  - POST `/api/ai/chat` 问答端点
  - 构建符合 DeepSeek API 规范的请求体
  - 解析并返回 AI 生成的回答
  - 异常捕获与友好提示

### 2. 配置支持
- [x] RestTemplateConfig（`config/RestTemplateConfig.java`）
  - 配置 UTF-8 编码处理（解决中文乱码问题）
  - 设置连接超时 10秒
  - 设置读取超时 10秒
- [x] SecurityConfig 安全配置
  - 将 `/api/ai/**` 添加到匿名访问路径
  - 允许未登录用户使用 AI 客服功能

### 3. Docker 部署
- [x] 集成到 docker-compose.yml
- [x] 后端容器与 AI 服务联动

## 遇到的问题和解决

### 问题 1：中文请求返回 400 错误
**现象**：发送包含中文字符的请求时，后端返回 `HttpMessageNotReadableException: JSON parse error: Invalid UTF-8 middle byte`

**分析**：RestTemplate 默认使用 ISO-8859-1 编码解析响应，无法正确处理 UTF-8 中文

**解决**：在 RestTemplateConfig 中配置 StringHttpMessageConverter 使用 StandardCharsets.UTF_8

```java
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(10000);
    factory.setReadTimeout(10000);

    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    return restTemplate;
}
```

### 问题 2：DeepSeek API Key 无效
**现象**：最初配置的是阿里云百炼 API 地址，调用返回 401 Unauthorized

**分析**：用户提供的 API Key 是 DeepSeek 的，不是百炼的

**解决**：修改 API_URL 和 model 参数

```java
private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
requestBody.put("model", "deepseek-chat");
```

## PR 链接
- PR #X: https://github.com/2312190606/StudyRoom-Booking-System/compare

## 心得体会

通过本次 AI 功能后端实现，我学习了：

1. **第三方 API 集成**：从配置到调用，完整走通了 LLM API 集成流程
2. **字符编码处理**：深入理解了 HTTP 请求中字符编码的重要性，学会了正确配置 UTF-8 处理
3. **Spring Security 配置**：学会了如何灵活配置安全策略，放行特定路径
4. **异常处理**：为 AI 服务异常提供了友好的降级提示

该功能作为项目的 AI 能力延伸，为前端提供了稳定的智能问答服务，也为后续接入更多 AI 能力（如智能座位推荐、违约预测等）奠定了基础。
