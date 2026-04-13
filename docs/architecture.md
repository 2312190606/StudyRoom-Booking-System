# 自习室座位预约系统 - 架构设计文档

本文档基于各个需求文件与业务设计（README、前/后端文档、API规范及设计稿），描述了自习室座位预约系统的整体架构设计，包含了前端页面与组件结构、后端服务模块划分、核心数据库交互（ER模型），以及典型的预约业务交互流程。

---

## 1. 前端架构（页面/组件结构）

前端总体采用 **Vue 3 + Vite** 构建，通过 **Pinia** 进行全局状态管理，并且**基于 Axios 封装 HTTP 请求**。根据目标受众与生态的不同，视图层划分为两个端：
- **移动端（用户前台）**：基于 **Vant 4.x** 组件库进行定制设计，服务于广大学生和考研用户群体，包含在线选座可视化渲染、扫码定位签到、学习时长统计以及快捷预约等高频交互环境。
- **PC 端（管理后台）**：基于 **Element Plus** 搭建，面向自习室系统管理员，提供基于 ECharts 的数据仪表盘、灵活的拖拽式网格布局管理以及复杂的运营数据检索与处置功能。

```mermaid
graph TD
    A["前端架构层 (Vue 3 + Vite)"]
    
    A --> B["前台移动端系统 (基于 Vant UI)"]
    A --> C["后台 PC 端系统 (基于 Element Plus)"]
    
    %% 前台结构
    B --> B1["认证与鉴权 (/mobile)"]
    B --> B2["系统大厅 (/mobile)"]
    B --> B3["核心预约 (/mobile)"]
    B --> B4["个人中心 (/mobile)"]
    
    B1 -.-> B11("登录/注册")
    B2 -.-> B21("公告轮播展示")
    B2 -.-> B22("自习室搜索与列表")
    B3 -.-> B31("可视化网格座位图与状态")
    B3 -.-> B32("预约提交与确认")
    B4 -.-> B41("学习时长图表可视化")
    B4 -.-> B42("履约/违约记录查看")
    B4 -.-> B43("常用座位配置与一键预约")
    
    %% 后台结构
    C --> C1["仪表盘看板 (/admin)"]
    C --> C2["资源调度管控 (/admin)"]
    C --> C3["运营与用户运维 (/admin)"]
    C --> C4["平台设置管控 (/admin)"]

    C1 -.-> C11("预约量与利用率/趋势图")
    C2 -.-> C21("自习区域与开放情况管控")
    C2 -.-> C22("拖拽座位可视化排布管理")
    C3 -.-> C31("全量预约记录检视与报表导出")
    C3 -.-> C32("用户信用评价与违规封禁")
    C4 -.-> C41("预约规则阈值配置")
    C4 -.-> C42("轮播图与走马灯公告上下架")

    classDef default fill:#f9f9f9,stroke:#333,stroke-width:1px;
    classDef main fill:#4F46E5,color:#fff,stroke:#4F46E5;
    class A main;
    
    style B fill:#1E293B,color:#fff,stroke:#1E293B;
    style C fill:#1E293B,color:#fff,stroke:#1E293B;
```

---

## 2. 后端架构（服务/模块划分）

后端采用 **Spring Boot 3.x 单体架构** 进行开发。利用 Spring Security + JWT 统一承担安全与鉴权任务。使用 Redis 进行极速查询与并发防冲突（分布式锁机制）。同时借助 MyBatis-Plus 提高持久层开发效率。

```mermaid
graph TD
    Client(("客户端 App / Web / 小程序"))
    
    subgraph backend ["Spring Boot 核心服务进程 (study-room-backend)"]
        Security["Spring Security + JWT 鉴权网关"]
        
        CtrlLayer["分流控制层 (App API / Admin API)"]
        SvcLayer["业务服务层 (Auth / Room / Order / Sys)"]
        MapperLayer["持久化层 (MyBatis-Plus Mappers)"]
        
        Task["定时调度任务 (自动释放违规资源)"]
        
        Security --> CtrlLayer
        CtrlLayer --> SvcLayer
        SvcLayer --> MapperLayer
        Task --> SvcLayer
    end
    
    Client -- "Bearer Token" --> Security
    
    subgraph db ["基础设施层"]
        Redis[("Redis 6.0+ 极速缓存 / 分布式锁")]
        MySQL[("MySQL 8.x 核心关系型主库")]
    end
    
    MapperLayer --> MySQL
    SvcLayer -.-> Redis

    classDef core fill:#0F172A,color:#fff,stroke:#0F172A;
    class Security,CtrlLayer core;
```

- **认证防线**：所有非公共接口进入业务节点前，由 Spring Security 验证并解析 JWT，拒绝非法探测，且利用 Redis 支撑高可用 Token 黑名单或延期机制。
- **Controller 端口分流**：区分移动端操作与管理后台（如 `/api/admin`）；后台接口自动叠加更高权限的断言判定。
- **并发调度机制**：预约引擎（`OrderSvc`）作为系统的核心高并发出口，利用 Redis 的分布式锁以实现安全抢座环境，避免严重并发情况下的“一号多占”超卖现象。
- **异步与定时任务**：使用 Spring Task或Quartz在固定频率异步跑批校验预约时长，对逾期未入座、临时离席超时的座位强制释放，联动生成用户的违规记录。

---

## 3. 数据库设计（ER 图）

主要围绕业务载体进行建模，利用 MySQL 持久化储存和 MyBatis Plus 简化操作：

```mermaid
erDiagram
    USER ||--o{ RESERVATION : "makes"
    USER ||--o{ VIOLATION : "has"
    USER ||--o{ FAVORITE : "collects"
    USER ||--o{ STUDY_TIME_STATS : "owns"
    
    STUDY_ROOM ||--o{ SEAT : "contains"
    
    SEAT ||--o{ RESERVATION : "is_booked_in"
    SEAT ||--o{ FAVORITE : "is_favorited_in"
    
    RESERVATION ||--o| VIOLATION : "triggers"

    USER {
        bigint id PK
        string username
        string avatar
        string phone
        string email
        int credit_score
        datetime last_credit_time
        int role
        int status
    }

    STUDY_ROOM {
        bigint id PK
        string name
        string location
        int floor
        time opening_time
        time closing_time
        int status
    }

    SEAT {
        bigint id PK
        bigint room_id FK
        string seat_number
        boolean has_power
        boolean is_window
        int status
    }

    RESERVATION {
        bigint id PK
        bigint user_id FK
        bigint seat_id FK
        datetime start_time
        datetime end_time
        int status
    }

    VIOLATION {
        bigint id PK
        bigint user_id FK
        bigint reservation_id FK
        string reason
    }

    FAVORITE {
        bigint id PK
        bigint user_id FK
        bigint seat_id FK
    }

    STUDY_TIME_STATS {
        bigint id PK
        bigint user_id FK
        date stat_date
        int total_minutes
    }

    ANNOUNCEMENT {
        bigint id PK
        string title
        int status
    }
```

---

## 4. 系统交互流程

以下以最典型的业务行为 **“用户选座及并发情况下的抢座、校验防超发签到全链路”** 为例，梳理系统各个层级的调用与验证响应路径：

```mermaid
sequenceDiagram
    autonumber
    actor U as 用户 (App/Web/小程序)
    participant Sec as 鉴权网关 (Security)
    participant Ctrl as 业务服务集群 (Controller/Service)
    participant Redis as Redis集群 (锁与缓存)
    participant DB as MySQL (主库)

    %% 场景一：抢座与防并发
    U ->> Sec: 1. 发起选时预约请求
    Sec ->> Ctrl: 校验令牌通过，移交业务节点
    Ctrl ->> Redis: 尝试获取目标占用锁 (SETNX)
    
    alt 分布锁被抢占
        Redis -->> Ctrl: 返回锁定干预失败
        Ctrl -->> U: 通知座位正忙，请重试或重选
    else 获取到分布锁
        Redis -->> Ctrl: 上锁通行许可
        Ctrl ->> DB: 跨表校验规则 (信用积分、合规等)
        DB -->> Ctrl: 校验通过，写入预约订单记录
        Ctrl ->> Redis: 更新大盘余量统计，清空分布锁
        Ctrl -->> U: 抢座成功，出示入场凭证 (二维码)
    end

    %% 场景二：赴场签到
    U ->> Sec: 2. 用户抵场点击签到 (携带 Geo 位置信息)
    Sec ->> Ctrl: 请求放过
    Ctrl ->> DB: 在时钟容限与距离半径配置中校验
    DB -->> Ctrl: 核验完全合法
    Ctrl ->> DB: 更新主订单状态为"使用中"
    Ctrl -->> U: 签到激活成功，开启正式计时
```
