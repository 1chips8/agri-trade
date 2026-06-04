# 农产品直销交易与订单履约系统

基于前后端分离架构的农产品直销交易平台，覆盖商家入驻、商品发布审核、消费者浏览下单、模拟支付、订单履约、评价通知和后台统计的完整业务闭环。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 |
| ORM | MyBatis-Plus | 3.5.5 |
| 认证鉴权 | Sa-Token（RBAC 四角色模型） | 1.37.0 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7-alpine |
| 消息队列 | RabbitMQ | 3.13-management |
| API 文档 | Knife4j | 4.4.0 |
| 前端框架 | Vue 3 + Vite | 3.5 / 6.0 |
| UI 组件库 | Element Plus | 2.9 |
| 状态管理 | Pinia | 2.3 |
| 图表 | ECharts | 5.6 |
| 反向代理 | Nginx | 1.27-alpine |
| 容器化 | Docker Compose | — |

## 系统架构

```
浏览器 :80 ──▶ Nginx ──┬── /api/** ──▶ Spring Boot :8080 ──┬── MySQL :3306
                       │                                    ├── Redis :6379
                       └── /* ──────▶ 前端 Nginx :80        └── RabbitMQ :5672
                                         (Vue 3 SPA)
```

后端采用模块化单体架构，按业务领域拆分为 12 个模块（auth / user / merchant / category / product / cart / order / payment / inventory / message / review / statistics），每个模块遵循 `controller → service → mapper → entity` 分层。

## 系统角色

| 角色 | 权限范围 |
|------|----------|
| 游客（GUEST） | 注册、登录、浏览商品 |
| 消费者（CONSUMER） | 购物车、下单、支付、收货、评价、消息、申请成为商家 |
| 商家（MERCHANT） | 商品发布与上下架、订单发货、评价回复、经营统计 |
| 管理员（ADMIN） | 商家审核、商品审核、分类管理、平台统计、违规处理 |

## 功能模块

| 模块 | 核心功能 |
|------|----------|
| 认证 | 注册、登录、Sa-Token 会话管理 |
| 商家入驻 | 提交申请 → 管理员审核（通过/拒绝）→ 创建商家档案 → 消息通知 |
| 商品管理 | 商家发布 → 管理员审核 → 上架/下架；前台按关键词搜索、按分类浏览 |
| 购物车 | 加入购物车、数量修改、选中状态管理、按商家分组结算 |
| 订单履约 | 购物车下单（Redis 防重 + 库存锁定）→ 按商家拆单 → 模拟支付 → 发货 → 确认收货 |
| 超时取消 | 订单创建后 RabbitMQ 延迟 30 分钟，未支付自动取消并返还库存 |
| 消息通知 | 支付/审核/发货/取消事件通过 RabbitMQ 异步写入，支持已读/删除管理 |
| 数据统计 | 管理员看板（概览/趋势/热销/分类/商家排行）、商家看板（概览/趋势）、ECharts 图表 |

## 数据库设计

13 张核心表，关键设计要点：

| 表 | 说明 | 亮点 |
|----|------|------|
| `sys_user` | 用户 | BCrypt 密码、CONSUMER/MERCHANT/ADMIN 三角色、逻辑删除 |
| `merchant_apply` | 入驻申请 | 审核状态流转 PENDING→APPROVED/REJECTED |
| `merchant` | 商家档案 | user_id 唯一约束、累计销售额/订单数 |
| `product_category` | 商品分类 | 支持父子层级、ENABLED/DISABLED 状态 |
| `product` | 商品 | 审核状态+销售状态双字段、价格/库存/销量/评分 |
| `cart_item` | 购物车 | (user_id, product_id) 唯一约束、selected 选中标识 |
| `trade_order` | 订单 | 按商家拆单、完整状态流转、收货地址快照 |
| `trade_order_item` | 订单明细 | 商品名/价格/图片快照、防止商品变更影响历史订单 |
| `payment_record` | 支付流水 | 独立流水号、PENDING/SUCCESS/CLOSED 三状态 |
| `inventory_log` | 库存流水 | before/after 双向记录、ORDER/CANCEL 变更类型追踪 |
| `message_notice` | 消息通知 | 已读/未读管理、关联业务 ID 溯源 |
| `product_review` | 商品评价 | 评分+内容+商家回复 |
| `daily_statistics` | 日统计 | 按日期汇总订单/销售额/用户/商品 |

## 项目结构

```
agri-trade
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/agritrade/
│   │   ├── common/                   # 统一返回体、全局异常、实体基类
│   │   ├── config/                   # Sa-Token、MyBatis-Plus、RabbitMQ、管理员初始化
│   │   ├── auth/                     # 认证模块
│   │   ├── user/                     # 用户实体与枚举
│   │   ├── merchant/                 # 商家入驻与审核
│   │   ├── category/                 # 商品分类
│   │   ├── product/                  # 商品发布与审核
│   │   ├── cart/                     # 购物车
│   │   ├── order/                    # 订单与库存流水
│   │   ├── payment/                  # 模拟支付
│   │   ├── message/                  # 消息通知
│   │   └── statistics/              # 数据统计
│   ├── src/main/resources/
│   │   ├── application.yml           # 主配置（环境变量驱动）
│   │   ├── application-local-h2.yml  # 本地 H2 开发配置
│   │   └── db/schema.sql             # 建表脚本 + 初始分类数据
│   └── Dockerfile
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── views/                    # 7 个页面组件
│   │   │   ├── Products.vue          # 商品列表与搜索
│   │   │   ├── Login.vue             # 登录/注册
│   │   │   ├── Cart.vue              # 购物车与下单
│   │   │   ├── Orders.vue            # 订单管理
│   │   │   ├── Merchant.vue          # 商家后台（申请/商品/发货）
│   │   │   ├── Admin.vue             # 管理后台（审核/分类）
│   │   │   └── Dashboard.vue         # 数据看板（ECharts）
│   │   ├── stores/auth.js            # Pinia 登录态
│   │   ├── api.js                    # Axios 封装
│   │   ├── router.js                 # 路由配置
│   │   └── App.vue                   # 导航栏 + 路由出口
│   └── Dockerfile
├── nginx/
│   └── default.conf                  # Nginx 反向代理配置
└── docker-compose.yml                # 6 服务编排
```

## 快速开始

### Docker Compose（推荐）

```bash
# 构建后端 JAR
cd backend && mvn -DskipTests package

# 构建前端 dist
cd ../frontend && npm install && npm run build

# 启动全部服务
cd .. && docker compose up -d --build
```

### 启动后访问

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端入口 | http://localhost | Vue SPA |
| 后端 API | http://localhost:8080/api | REST 接口 |
| API 文档 | http://localhost:8080/doc.html | Knife4j |
| RabbitMQ 管理台 | http://localhost:15672 | 消息队列监控 |

### 本地开发

**后端**（JDK 17 + Maven）：

```bash
cd backend
mvn spring-boot:run
# 或使用 H2 内存数据库（无需 MySQL/Redis/RabbitMQ）：
mvn spring-boot:run -Dspring-boot.run.profiles=local-h2
```

**前端**：

```bash
cd frontend
npm install
npm run dev
```

### 默认管理员

| 用户名 | 密码 |
|--------|------|
| `admin` | `admin123` |

管理员由 `AdminInitializer` 在首次启动时自动创建，密码使用 BCrypt 加密存储。

## 详细设计文档

参见 [农产品交易系统标准开发设计文档](农产品交易系统标准开发设计文档.md)，包含完整的数据库字段定义、API 路径、状态设计、Redis/RabbitMQ 拓扑和事务设计。
