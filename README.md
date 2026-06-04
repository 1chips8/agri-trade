# 农产品交易系统

基于 Spring Boot 2.7、MyBatis-Plus、Sa-Token、MySQL 8、Redis、RabbitMQ、Vue 3、Vite、Element Plus、Pinia、Axios、ECharts 和 Docker Compose 的农产品直销交易与订单履约系统 MVP。

## MVP 阶段计划

1. 项目骨架：创建 `backend`、`frontend`、`nginx`、`docker-compose.yml` 和 README。
2. 后端基础：Spring Boot、统一响应、全局异常、MyBatis-Plus、Sa-Token 登录、默认管理员。
3. 核心业务：注册登录、商家入驻审核、分类、商品发布审核、购物车、下单、模拟支付、发货、收货、消息、统计。
4. 前端基础：Vue 3、路由、Pinia 登录态、Axios 封装、Element Plus 页面。
5. 页面闭环：商品浏览、购物车下单、订单支付收货、商家后台、管理员审核、统计看板。
6. 部署验证：Docker Compose 编排 MySQL、Redis、RabbitMQ、后端、前端和 Nginx。

## 本地开发

后端需要 JDK 17 和 Maven：

```bash
cd backend
mvn -DskipTests package
mvn spring-boot:run
```

如果 WSL 未安装 JDK/Maven，可以使用本项目本地工具链路径：

```bash
cd backend
JAVA_HOME=/home/chips/projects/agri-trade/.tools/jdk-17.0.19+10 \
  /home/chips/projects/agri-trade/.tools/apache-maven-3.9.16/bin/mvn \
  -Dmaven.repo.local=/home/chips/projects/agri-trade/.tools/m2-repository \
  -DskipTests package
```

前端：

```bash
cd frontend
npm install
npm run dev
```

## Docker Compose 启动

当前 Dockerfile 使用本地已构建产物，启动 Compose 前先执行：

```bash
cd backend
mvn -DskipTests package
cd ../frontend
npm install
npm run build
cd ..
docker compose up -d --build
```

访问：

- 前端入口：http://localhost
- 后端接口：http://localhost:8080/api
- RabbitMQ 管理台：http://localhost:15672

默认管理员：

```text
username: admin
password: admin123
```

## 数据库

MySQL 首次启动会执行：

```text
backend/src/main/resources/db/schema.sql
```

该脚本创建核心表并初始化基础商品分类。默认管理员由后端启动时自动创建。

## 已实现的 MVP 接口范围

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`
- 商家入驻、管理员审核
- 商品分类、商品发布、商品审核、上下架、公开列表
- 购物车增删改查和选中状态
- 订单创建、取消、查询、支付、发货、确认收货
- 消息查询、未读数、标记已读、删除
- 管理员和商家基础统计

## 当前验证记录

- 前端 `npm run build` 已通过。
- 已下载项目本地 JDK 17 和 Maven 3.9.16 到 `.tools/`，后端 `mvn -DskipTests package` 已通过并生成 jar。
- `docker compose config --quiet` 已通过。
- Docker Hub mirror `docker.mirrors.ustc.edu.cn` 失效，Compose 已改用 Public ECR 官方镜像同步地址。
- MySQL、Redis、RabbitMQ 已通过 Docker Compose 启动；RabbitMQ 管理 API 验证通过。
- 后端已连接真实 MySQL 和 Redis，并在 `18081` 完成分类查询、注册、登录、Sa-Token 鉴权和管理员统计接口联调。
- Vite 开发服务器已在 `5174` 启动，`/api` 代理访问真实后端验证通过。
- 当前开发访问地址：前端 `http://localhost:5174`，后端 `http://localhost:18081/api`，RabbitMQ 管理台 `http://localhost:15672`。
