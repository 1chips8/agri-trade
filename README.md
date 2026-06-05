# 农产品直销交易与订单履约系统

基于 Spring Boot 与 Vue 3 的前后端分离农产品交易平台。当前版本已实现用户认证、商家入驻审核、商品审核与上下架、购物车、模拟支付、订单履约、站内消息和统计看板。

> 当前状态：可运行的课程/演示项目。评价、RabbitMQ 异步通知和商品缓存等能力尚未实现，数据库与配置中保留了部分扩展基础。

## 功能与实现状态

| 模块 | 当前能力 | 状态 |
|------|----------|------|
| 用户认证 | 注册、登录、当前用户、退出登录 | 已实现 |
| 商家入驻 | 用户申请、管理员审核、创建商家档案 | 已实现 |
| 商品管理 | 商家发布/修改/上下架、管理员审核、公开查询 | 已实现 |
| 购物车 | 添加、修改数量、选中、删除、清空 | 已实现 |
| 订单履约 | 按商家拆单、扣减/返还库存、取消、发货、收货、超时自动取消 | 已实现 |
| 支付 | 模拟支付、支付记录查询 | 已实现 |
| 消息通知 | 支付、审核、发货等事件同步写入站内消息 | 已实现 |
| 数据统计 | 平台与商家概览、趋势和排行 | 已实现 |
| Redis | 5 秒用户级下单防重复提交锁 | 已实现 |
| RabbitMQ | 交换机、队列和绑定声明 | 已配置，业务未接入 |
| 商品评价 | `product_review` 表结构 | 未实现 |
| 超时取消 | 30 分钟未支付订单定时取消，并返还库存、关闭支付记录 | 已实现 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | JDK 17、Spring Boot 2.7.18、MyBatis-Plus 3.5.5、Sa-Token 1.37.0 |
| 数据与中间件 | MySQL 8.0、Redis 7、RabbitMQ 3.13 |
| API 文档 | Knife4j 4.4.0 |
| 前端 | Vue 3.5、Vite 6、Element Plus 2.9、Pinia 2.3、ECharts 5.6 |
| 部署 | Docker Compose、Nginx |

## 项目结构

```text
agri-trade/
├── backend/                       # Spring Boot 后端
│   ├── src/main/java/com/agritrade/
│   │   ├── auth/                  # 注册、登录和会话
│   │   ├── merchant/              # 商家入驻与审核
│   │   ├── category/              # 商品分类
│   │   ├── product/               # 商品发布、审核与查询
│   │   ├── cart/                  # 购物车
│   │   ├── order/                 # 订单与库存流水
│   │   ├── payment/               # 模拟支付
│   │   ├── message/               # 站内消息
│   │   └── statistics/            # 数据统计
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-local-h2.yml
│       └── db/schema.sql
├── frontend/                      # Vue 3 前端
├── nginx/default.conf             # 统一入口反向代理
├── docker-compose.yml
└── 农产品交易系统标准开发设计文档.md
```

## 快速开始

### Docker Compose

环境要求：JDK 17、Maven、Node.js/npm 和 Docker Compose。

当前 Dockerfile 会复制本地已经生成的 `backend/target/*.jar` 与 `frontend/dist`，因此首次启动前需要先构建应用：

```bash
cd backend
mvn clean package -DskipTests

cd ../frontend
npm ci
npm run build

cd ..
docker compose up -d --build
```

检查服务状态：

```bash
docker compose ps
docker compose logs -f backend
```

### 本地开发

1. 启动依赖服务：

```bash
docker compose up -d mysql redis rabbitmq
```

2. 启动后端：

```bash
cd backend
mvn spring-boot:run
```

3. 新终端启动前端：

```bash
cd frontend
npm ci
npm run dev
```

前端开发服务器运行在 `http://localhost:5173`，并将 `/api` 代理到 `http://localhost:8080`。

### H2 开发模式

`local-h2` profile 只把 MySQL 替换为 H2 内存数据库；下单防重仍依赖 Redis。RabbitMQ 当前尚未接入业务流程，但配置仍会被加载。

```bash
docker compose up -d redis rabbitmq
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local-h2
```

## 服务地址

| 服务 | 地址 | 说明 |
|------|------|------|
| Docker 统一入口 | http://localhost | Vue SPA，经 Nginx 访问 |
| 前端开发服务器 | http://localhost:5173 | 本地开发模式 |
| 后端 API | http://localhost:8080/api | REST 接口 |
| Knife4j | http://localhost:8080/doc.html | API 调试文档 |
| OpenAPI JSON | http://localhost:8080/v3/api-docs | 接口定义 |
| RabbitMQ 管理台 | http://localhost:15672 | 用户名/密码：`agri` / `agri123456` |

## 默认账号

首次启动后端时，`AdminInitializer` 会创建管理员：

| 用户名 | 密码 |
|--------|------|
| `admin` | `admin123` |

该账号仅适合本地演示，部署到共享或公网环境前应修改初始化逻辑和所有默认密码。

## 演示流程

推荐按下面顺序演示，能覆盖平台、商家、用户三类角色和完整交易闭环：

1. 使用 `admin` / `admin123` 登录管理端，确认分类、商品审核和统计入口可访问。
2. 注册一个普通用户，提交商家入驻申请。
3. 切回管理员账号，在商家审核页通过申请。
4. 使用商家账号发布商品，再由管理员审核通过并上架。
5. 使用普通用户浏览商品、加入购物车、提交订单。
6. 在订单页执行模拟支付，展示订单从待支付进入待发货。
7. 使用商家入口发货，再切回用户确认收货，展示履约闭环。
8. 查看站内消息和统计看板，说明支付、审核、发货等事件会同步写入消息并进入统计展示。

超时取消可以单独演示：将 `agri.order.timeout.minutes` 临时调小，例如 `1`，并将 `agri.order.timeout.scan-delay-ms` 调成 `5000`；创建订单后不支付，等待定时任务把订单取消，同时库存返还、支付记录关闭。演示结束后再恢复默认配置。

## 交付亮点

- 订单状态使用数据库条件更新，支付、取消、发货、收货只能在正确旧状态下成功，避免重复请求导致状态回退。
- 下单扣库存和取消返库存使用数据库原子更新，防止库存读后写覆盖。
- 30 分钟未支付订单由定时任务自动取消，复用同一套库存返还与支付关闭逻辑。
- 请求参数已覆盖核心入口校验，非法 ID、数量、状态值和基础表单字段会在进入业务前被拒绝。
- 前端生产包已做路由懒加载和 vendor 拆包，入口 JS 保持在很小体积，适合演示部署。

## 配置

后端配置支持以下环境变量：

| 环境变量 | 默认值 |
|----------|--------|
| `MYSQL_HOST` / `MYSQL_PORT` / `MYSQL_DATABASE` | `localhost` / `3306` / `agri_trade` |
| `MYSQL_USER` / `MYSQL_PASSWORD` | `agri` / `agri123456` |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | `localhost` / `5672` |
| `RABBITMQ_USER` / `RABBITMQ_PASSWORD` | `agri` / `agri123456` |

前端开发代理可通过 `VITE_API_PROXY_TARGET` 修改，默认值为 `http://localhost:8080`。

订单超时取消可通过 `agri.order.timeout.enabled`、`agri.order.timeout.minutes` 和 `agri.order.timeout.scan-delay-ms` 调整，默认启用、30 分钟超时、60 秒扫描一次。

## 验证与测试

```bash
cd backend
mvn test

cd ../frontend
npm run build
```

仓库当前包含请求参数校验、订单状态流转、库存原子更新和订单超时取消等 H2/单元测试；前端没有专门测试脚本，使用生产构建验证编译与打包。

## 已知限制

- 角色权限主要在 Service 层校验，Sa-Token 拦截器只统一检查登录状态。
- Sa-Token 登录会话当前未配置 Redis DAO，不能按“Redis 持久化会话”理解。
- 下单扣库存和取消返库存使用数据库原子更新；Redis 商品级库存锁尚未实现。
- RabbitMQ 尚未用于异步通知；订单超时取消当前由 Spring 定时任务扫描实现。
- `product_review`、`daily_statistics` 表已建，但评价模块与日统计任务尚未实现。
- Docker Compose 中包含演示用明文密码，不适合直接用于生产环境。

## 详细设计

参见 [农产品交易系统标准开发设计文档](农产品交易系统标准开发设计文档.md)。其中包含数据库字段、API、状态和目标架构；阅读时请以文档开头的实现状态说明为准。
