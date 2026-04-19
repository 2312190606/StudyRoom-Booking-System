# AI 功能模块说明

## 功能说明

### 1. 使用的 AI 模型

- **模型名称**：DeepSeek Chat
- **API 提供商**：DeepSeek
- **API 地址**：`https://api.deepseek.com/v1/chat/completions`
- **模型标识**：`deepseek-chat`

### 2. 实现的功能

#### AI 智能客服问答

**功能描述**：为"静学自习室座位预约系统"提供智能客服功能，用户可通过自然语言提问获取关于自习室预约、签到、信誉分等问题的智能解答。

**核心特性**：
- 对话式交互：用户输入问题，AI 返回智能回答
- 专业知识库：基于静学自习室业务场景定制
- 实时响应：10秒超时控制
- 降级策略：API 调用失败时提供关键词匹配回复

**知识库内容**：
- 系统基本信息（名称、功能、开放时间 7:00-22:30）
- 预约规则（最长4小时，提前30分钟取消）
- 信誉分制度（初始100分，签到+5分，违约-10分，低于60分禁用）
- 座位类型（普通座位、靠窗座位、带电源座位）
- 违约行为定义

**接入位置**：
- 前端：`src/components/AIChat.vue`
- 后端：`controller/AIController.java`

---

## 安全配置

### API Key 管理

**重要提示**：API Key 不会提交到代码仓库，使用环境变量管理。

1. **创建 .env 文件**
   在 `backend/` 目录下创建 `.env` 文件：
   ```
   DEEPSEEK_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

2. **修改 .gitignore**
   确保 `.env` 文件被忽略：
   ```
   .env
   ```

3. **读取环境变量**
   后端代码从系统环境变量读取 API Key：
   ```java
   private static final String API_KEY = System.getenv("DEEPSEEK_API_KEY");
   ```

4. **Docker 环境变量**
   在 `docker-compose.yml` 中配置：
   ```yaml
   environment:
     - DEEPSEEK_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

---

## 技术实现

### 后端接口

```
POST /api/ai/chat
Content-Type: application/json

Request:
{
  "question": "如何预约座位"
}

Response:
{
  "code": 200,
  "msg": "success",
  "data": "您好！欢迎使用静学自习室..."
}
```

### 前端组件

- **AIChat.vue**：基于 Vant Popup 的底部弹出式聊天界面
- **ai.js**：封装 `/api/ai/chat` 接口调用

### 编码处理

RestTemplate 配置 UTF-8 编码，解决中文乱码问题：

```java
restTemplate.getMessageConverters().add(1, 
    new StringHttpMessageConverter(StandardCharsets.UTF_8));
```

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `backend/controller/AIController.java` | AI 控制器 |
| `backend/config/RestTemplateConfig.java` | HTTP 客户端配置 |
| `backend/config/SecurityConfig.java` | 安全配置（放行 /api/ai） |
| `backend/.env` | API Key 环境变量（不提交） |
| `frontend/src/components/AIChat.vue` | AI 聊天组件 |
| `frontend/src/api/ai.js` | AI API 调用封装 |

---

## 后续扩展

该模块可扩展的 AI 功能：

1. **智能座位推荐**：根据用户历史预约记录推荐合适座位
2. **违约风险预测**：预测用户违约概率，提前发送提醒
3. **人流量预测**：基于历史数据预测各时段座位使用率
4. **异常行为检测**：检测刷号、恶意预约等异常行为
