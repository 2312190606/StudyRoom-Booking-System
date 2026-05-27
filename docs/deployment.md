# 云服务部署说明

## 部署架构

```
┌─────────────────┐     ┌─────────────────┐
│   Vercel (前端)  │ --> │  Railway (后端) │
│  studyroom.ver  │     │   :8080 port    │
│    cel.app      │     │                 │
└─────────────────┘     └─────────────────┘
```

## 1. 后端部署 (Railway)

### 1.1 创建项目
1. 登录 [Railway](https://railway.app)
2. New Project → 选择 GitHub 仓库
3. 选择 `study-room-backend` 分支

### 1.2 配置环境变量

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `DATABASE_URL` | `mysql://user:pass@host:3306/study_room_db` | MySQL 连接字符串 |
| `STUDYROOM_JWT_SECRET` | 你的密钥 | JWT 签名密钥 |
| `PRODUCTION` | `true` | 标记生产环境 |

### 1.3 添加数据库
1. Railway 控制台 → Add Plugin → MySQL
2. 自动创建 `MYSQL_ROOT_PASSWORD` 等变量

### 1.4 部署
Railway 自动检测 `railway.toml` 并构建 Docker 镜像。

## 2. 前端部署 (Vercel)

### 2.1 创建项目
1. 登录 [Vercel](https://vercel.com)
2. Import GitHub 仓库
3. 选择 `frontend` 作为 Root Directory

### 2.2 配置环境变量

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `VITE_API_BASE_URL` | `https://studyroom-backend.up.railway.app/api` | 后端 API 地址 |

### 2.3 部署
Vercel 自动构建静态资源并部署。

## 3. 配置文件说明

### railway.toml
```toml
[build]
builder = "docker"
dockerfilePath = "backend/Dockerfile"

[deploy]
numReplicas = 1
healthCheckPath = "/health"
port = 8080

[env]
PRODUCTION = "true"
```

### vercel.json
```json
{
  "builds": [
    { "src": "frontend/**", "use": "@vercel/static" }
  ],
  "rewrites": [
    { "source": "/api/(.*)", "destination": "https://studyroom-backend.up.railway.app/api/$1" }
  ]
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

## 6. 本地开发

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