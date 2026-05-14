#!/bin/bash
set -e

echo "🚀 开始部署..."

# Check if secrets directory exists
if [ ! -d "secrets" ]; then
    echo "⚠️  secrets 目录不存在，正在创建..."
    mkdir -p secrets
    echo "dev123" > secrets/db_password.txt
    echo "dev123" > secrets/mysql_root_password.txt
    echo "✅ secrets 目录已创建"
fi

# Build and start services
echo "📦 正在构建镜像..."
docker compose -f compose.prod.yaml build

echo "🚀 正在启动服务..."
docker compose -f compose.prod.yaml up -d

# Wait for health checks
echo "⏳ 等待服务就绪..."
sleep 30

# Check service status
echo "📊 服务状态:"
docker compose -f compose.prod.yaml ps

# Show logs
echo "📝 最近日志:"
docker compose -f compose.prod.yaml logs --tail=20

echo "✅ 部署完成！"
echo "   前端地址: http://localhost"
echo "   后端地址: http://localhost:8080"
