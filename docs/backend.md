# 自习室座位预约系统 - 后端开发文档

## 一、模块功能

后端模块功能与前端视图结构相对应，主要为用户端与管理员端提供数据接口与业务逻辑支撑。

### 1. 用户端功能支撑
- **认证模块**: 处理用户注册、登录，提供 JWT 令牌发放与刷新机制。
- **公共资源模块**: 加载轮播图与系统公告。
- **自习室资源模块**: 返回可供选择的自习室列表，及带状态的可视化座位地图数据。
- **预约引擎**: 
  - 处理用户的预约请求（含时间校验与防并发超卖选座锁机制）。
  - 提供当前及历史记录查询。
  - 处理取消与延后逻辑。
- **签到服务**: 接收经纬度入参，结合内置定位算法校验用户的签到有效性。
- **用户档案域**: 更新基本资料与密码，进行用户学习时长的数据统计聚合。
- **收藏组件**: 管理用户的常用自习室和座位配置。

### 2. 管理员端功能支撑
- **数据仪表盘计算**: 基于预约数据源进行聚合，生成统计指标数据（单日量、利用率及增长曲线）。
- **实体资源管理**: 支持对自习室与个别座位属性的增删改查。
- **预约运维**: 查询全体活动记录与取消问题场次；支持导出数据（如 Excel 格式）。
- **用户运维**: 全局查询与状态管控（封禁/解封）。
- **内容发布管理**: 公告与轮播图数据的增删改及展播状态控制。
- **参数动态配置**: 提供系统全局配置字（营业时间、违约判定等）的保存与加载服务。

---

## 二、技术选型

- **核心应用框架**: Spring Boot 3.x
- **开发基础语言**: Java 17+
- **鉴权与安全机制**: Spring Security + JWT
- **数据库存储**: MySQL 8.x
- **持久化操作层**: MyBatis-Plus
- **数据缓存与锁**: Redis (用于缓存公告、自习室列表及实现低延迟并发预约控制)
- **调度引擎**: Spring Task / Quartz (处理预约自动到期、逾期违约扫描等定时逻辑)
- **脚手架与工具类库**:
  - `Hutool` (集合运算、日期计算、加密解密工具)
  - `MapStruct` (提供实体 Entity 与传输层 DTO 之间的快速映射)
  - `Lombok` (极简 POJO 代码构建)
  - `Alibaba EasyExcel` (针对后台数据导出的高性能 Excel 工具)

---

## 三、目录结构

```text
study-room-backend/
├── src/
│ ├── main/
│ │ ├── java/com/example/studyroom/
│ │ │ ├── StudyRoomApplication.java    # 项目启动类
│ │ │ ├── config/                      # 系统级配置 (Security, Redis, MyBatisPlus, MVC等)
│ │ │ ├── controller/                  # REST API 端点 (分为 /admin 与 /app 端)
│ │ │ ├── service/                     # 业务接口逻辑 (Service 及其 impl 实现层)
│ │ │ ├── mapper/                      # 数据库映射接口 (MyBatis Mapper)
│ │ │ ├── model/
│ │ │ │   ├── entity/                  # 数据库表映射实体类
│ │ │ │   ├── dto/                     # 数据传输对象 (请求体与响应体)
│ │ │ │   └── vo/                      # 视图展现对象
│ │ │ ├── common/
│ │ │ │   ├── exception/               # 全局异常捕获及自定义异常
│ │ │ │   ├── lock/                    # 并发控制组件 (如 Redis 分布式锁)
│ │ │ │   └── result/                  # 统一通用响应封装类
│ │ │ ├── security/                    # 安全与 JWT 认证校验过滤器
│ │ │ └── task/                        # 定时任务处理 (释放资源、违约计算等)
│ │ ├── resources/
│ │ │ ├── application.yml              # 通用核心配置
│ │ │ ├── application-dev.yml          # 开发环境配置
│ │ │ ├── application-prod.yml         # 生产环境配置
│ │ │ └── mapper/                      # XML SQL 动态语句映射文件
│ ├── test/                            # 单元与集成测试目录
├── pom.xml                            # Maven 依赖声明与打包配置
└── README.md
```

---

## 四、运行方式

### 1. 环境准备
- **JDK 环境**: Java 17 或以上版本
- **构建工具**: Maven 3.6+
- **数据库**: MySQL 8.0+
- **缓存服务器**: Redis 6.0+

### 2. 数据库与环境初始化
启动前，请准备好以下依赖中间件：
1. 创建 MySQL 数据库（如 `study_room_db`），并执行项目根目录下的 SQL 脚本完成表结构和基础数据导入。
2. 启动 Redis 服务端，默认使用 `127.0.0.1:6379`（无密码）。
3. 调整 `src/main/resources/application-dev.yml` 中的配置项：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/study_room_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
       username: root
       password: your_database_password
     data:
       redis:
         host: 127.0.0.1
         port: 6379
   ```

### 3. 项目编译与运行

**方式 A: IDEA 等宿主 IDE 运行**
1. 用 IntelliJ IDEA 打开工程目录，等待 Maven 拉取完所有依赖。
2. 找到 `src/main/java/.../StudyRoomApplication.java`。
3. 右键点击选择 `Run 'StudyRoomApplication'`。

**方式 B: 命令行运行**
打开终端进入项目根目录：
```bash
# 1. 编译并跳过测试
mvn clean install -DskipTests

# 2. 运行 Spring Boot 程序
mvn spring-boot:run
```

当控制台输出类似 `Started StudyRoomApplication in X.XXX seconds` 时，代表启动成功。默认接口基地址为: `http://localhost:8080/api`。

### 4. 生产环境部署打包
执行 Maven 构建生成 Jar 包：
```bash
mvn clean package -DskipTests
```
将 `target/study-room-backend-1.0.jar` 上传至服务器，使用如下命令在后台挂载运行：
```bash
nohup java -jar study-room-backend-1.0.jar --spring.profiles.active=prod > server.log 2>&1 &
```
