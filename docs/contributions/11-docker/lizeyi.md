# Docker 部署贡献说明
姓名：李泽亿
学号：1951515
日期：2026-05-15

## 我完成的工作
### 1. Dockerfile 编写
- [x] 后端Dockerfile（多阶段构建：Maven builder → Eclipse Temurin JRE）
- [x] .dockerignore 文件

### 2. Compose 配置
- [x] 开发环境compose.yaml（frontend、backend、mysql、redis）
- [x] 生产环境compose.prod.yaml（含 secrets 配置）
- [x] 健康检查配置（backend、mysql、redis）

### 3. 自动化部署
- 选择：选项B（使用GitHub Actions）
- 具体内容：通过 GitHub Actions workflow 实现自动构建并推送镜像到 GHCR

## PR 链接
- PR #X: https://github.com/xxx/xxx/pull/X

## 遇到的问题和解决
1. 问题：多阶段构建时每次改动代码都会重新下载依赖，构建时间过长
   解决：将 pom.xml 单独复制后先执行 dependency:go-offline，再复制源码，利用 Docker 缓存加速构建
2. 问题：生产环境数据库密码直接写在 compose 文件中不安全
   解决：使用 Docker secrets 功能，将密码保存到文件中并通过 secrets 挂载

## AI 使用情况
- 使用了哪些Prompt：如何优化 Docker 多阶段构建、使用 Docker secrets 管理敏感信息、Docker Compose 健康检查配置
- AI 帮助解决了哪些问题：提供了非 root 用户运行容器的安全最佳实践、解答了 secrets 和环境变量的区别及适用场景

## 心得体会
通过本次 Docker 部署实践，我掌握了多阶段构建 Dockerfile 的编写技巧，学会了通过 builder 阶段优化镜像大小。同时熟悉了 Docker Compose 的配置，包括服务依赖、健康检查和 secrets 管理。这次经历让我对容器化部署有了更深入的理解。