# 云服务部署说明

## 部署架构

```
┌─────────────────┐     ┌─────────────────────────────────────┐
│   Vercel (前端)  │ --> │  Railway (后端 + MySQL)              │
│  *.vercel.app   │     │  studyroom-booking-system-prod.up.  │
│                 │     │  railway.app :8080                  │
└─────────────────┘     └─────────────────────────────────────┘
```

## 1. 后端部署 (Railway)

### 1.1 创建项目
1. 登录 [Railway](https://railway.app)
2. New Project → Deploy from GitHub repo
3. 选择 GitHub 仓库

### 1.2 配置根目录
在 Project Settings → Root Directory 设置为 `backend`

### 1.3 添加 MySQL 数据库
1. Railway 控制台 → Add Plugin → MySQL
2. 等待 MySQL 创建完成
3. 在 Variables 页面获取自动生成的 `MYSQL_*` 变量

### 1.4 配置环境变量

在 Project Settings → Variables 添加：

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `DATABASE_URL` | `mysql://root:{密码}@{主机}:3306/railway` | MySQL 连接字符串（需拼接） |
| `STUDYROOM_JWT_SECRET` | `StudyRoomProductionSecretKey2026VeryLong` | JWT 密钥（至少32字符） |
| `PRODUCTION` | `true` | 标记生产环境 |

### 1.5 初始化数据库
1. MySQL 插件 → Connection → SQL Editor
2. 复制 `backend/src/main/resources/init.sql` 内容并执行

### 1.6 部署
触发 Deploy，Railway 自动构建 Docker 镜像。

### 1.7 验证
访问 `https://studyroom-booking-system-production.up.railway.app/health`，返回 `{"status":"UP"}` 表示成功。

## 2. 前端部署 (Vercel)

### 2.1 创建项目
1. 登录 [Vercel](https://vercel.com)
2. Import GitHub 仓库
3. Root Directory 设置为 `frontend`

### 2.2 配置文件
在 `frontend/vercel.json` 配置：

```json
{
  "installCommand": "npm install --legacy-peer-deps",
  "rewrites": [
    { "source": "/api/(.*)", "destination": "https://studyroom-booking-system-production.up.railway.app/api/$1" },
    { "source": "/(.*)", "destination": "/index.html" }
  ],
  "buildCommand": "npm run build"
}
```

### 2.3 部署
Vercel 自动构建并部署。

### 2.4 验证
访问 Vercel 提供的域名，确认页面正常显示且 API 请求正常。

## 3. 配置文件说明

### railway.toml
位置：`backend/railway.toml`
```toml
[build]
dockerfilePath = "Dockerfile"

[deploy]
numReplicas = 1
healthCheckPath = "/health"
port = 8080
```

### vercel.json
位置：`frontend/vercel.json`
```json
{
  "installCommand": "npm install --legacy-peer-deps",
  "rewrites": [
    { "source": "/api/(.*)", "destination": "https://studyroom-booking-system-production.up.railway.app/api/$1" },
    { "source": "/(.*)", "destination": "/index.html" }
  ],
  "buildCommand": "npm run build"
}
```

## 4. 自动部署

两个平台都支持 Git 推送自动部署：
- 推送到 `main` 分支自动触发部署
- 无需手动操作

## 5. 域名配置（可选）

### Vercel
- 默认提供 `*.vercel.app` 域名
- 可在 Settings → Domains 添加自定义域名

### Railway
- 默认提供 `*.up.railway.app` 域名
- 可在 Settings → Networking 添加自定义域名
- 自动启用 HTTPS

## 6. 常见问题

| 问题 | 解决 |
|------|------|
| Railway 构建失败 | 确认 Root Directory 为 `backend`，railway.toml 放在 `backend/` 内 |
| Vercel npm 依赖冲突 | 使用 `--legacy-peer-deps` 参数 |
| 页面 404 | 确认 vercel.json 中 rewrite 规则正确 |
| 后端无法连接数据库 | 检查 DATABASE_URL 格式是否正确 |

## 7. 本地开发

后端本地运行：
```bash
cd backend
DATABASE_URL=mysql://... STUDYROOM_JWT_SECRET=xxx ./mvnw spring-boot:run
```

前端本地运行：
```bash
cd frontend
VITE_API_BASE_URL=http://localhost:8080/api npm run dev
```