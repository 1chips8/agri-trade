# 农产品交易系统前端整改开发手册

日期：2026-06-06

适用对象：接手本项目进行前端整改的开发者或开发小组。

本手册用于配合以下两份文档执行开发：

- 设计文档：`docs/superpowers/specs/2026-06-06-frontend-redesign-design.md`
- 实施计划：`docs/superpowers/plans/2026-06-06-frontend-redesign.md`

本次整改目标不是把项目做成真实商业级电商平台，而是把现有 Vue 前端提升到“作品集/简历项目级别”：三类角色清楚、核心交易流程可走通、页面能被用户理解、验收时不依赖看接口或数据库。

## 1. 当前项目状态

### 1.1 技术栈

前端：

- Vue 3
- Vue Router 4
- Pinia
- Axios
- Element Plus
- ECharts
- Vite

后端：

- Spring Boot
- MyBatis Plus
- MySQL
- Redis
- RabbitMQ

本次主要开发目录：

- `frontend/src/App.vue`
- `frontend/src/router.js`
- `frontend/src/api.js`
- `frontend/src/stores/auth.js`
- `frontend/src/views/*.vue`
- `frontend/src/styles.css`

### 1.2 当前未提交改动提醒

接手开发前必须先运行：

```powershell
git status --short
```

截至本手册编写时，工作树中存在前一阶段留下的未提交改动，包括：

- `backend/src/main/java/com/agritrade/cart/CartController.java`
- `backend/src/main/java/com/agritrade/cart/CartItemMapper.java`
- `frontend/Dockerfile`
- `memory-bank/progress.md`
- `backend/src/test/java/com/agritrade/cart/CartControllerReaddTest.java`
- `docker-compose.override.yml`
- `frontend/nginx.conf`

这些改动与前端整改有关联但不应被无意覆盖。开发者接手后应先和项目负责人确认：

- 是否保留这些改动。
- 是否先单独提交这些改动。
- 是否基于当前工作树继续开发。

不要执行 `git reset --hard`、`git checkout -- .` 或删除未跟踪文件，除非项目负责人明确授权。

## 2. 本地运行环境

### 2.1 推荐入口

使用 Docker Compose 启动完整本地环境：

```powershell
docker compose up -d --build
docker compose ps
```

默认服务端口来自 `docker-compose.yml`：

- 页面入口：`http://localhost`
- 后端：`http://localhost:8080`
- MySQL：`localhost:3306`
- Redis：`localhost:6379`
- RabbitMQ 管理台：`http://localhost:15672`

当前本地存在 `docker-compose.override.yml`，会覆盖部分端口：

- 后端：`http://localhost:8081`
- MySQL：`localhost:3307`
- Redis：`localhost:6380`

如果其他开发者没有这个 override 文件，则应使用默认端口。

### 2.2 前端构建

建议用 Docker Node 构建，避免本机 Node 版本差异：

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

构建成功是每个阶段合并或提交前的最低要求。

### 2.3 前端开发模式

如果开发者本机 Node 可用，可在 `frontend` 目录下运行：

```powershell
npm install
npm run dev
```

如果本机 Node 不可用，则优先通过 Docker 构建和 Docker Compose 运行页面。

## 3. 开发组织方式

### 3.1 推荐采用 Subagent-Driven 模式

用户已选择方案 1：按任务分阶段开发，每个阶段完成后审查，再进入下一阶段。

建议分配方式：

- 每个 Task 由一个开发者或一个独立工作会话完成。
- 每个 Task 必须可单独构建、可单独提交。
- 每个 Task 完成后，由负责人做代码审查和页面验收。
- 不允许在 Task 1 顺手做 Task 4 的页面，也不允许跨阶段大改样式。

### 3.2 分支建议

建议新建前端整改分支：

```powershell
git checkout -b codex/frontend-redesign
```

如果当前工作树已有未提交改动，先不要切分支。应先确认这些改动如何处理。

### 3.3 提交粒度

每个 Task 一个提交：

- `feat(frontend): add shared presentation utilities`
- `feat(frontend): add role-aware navigation`
- `feat(frontend): improve product browsing`
- `feat(frontend): make cart checkout readable`
- `feat(frontend): improve buyer orders`
- `feat(frontend): improve merchant center`
- `feat(frontend): improve admin experience`
- `test(frontend): add acceptance report`

提交前必须运行：

```powershell
git diff
git status --short
```

确认没有无关文件、调试代码、构建产物或凭据。

## 4. 总体整改目标

整改完成后，用户应能从页面上理解系统：

- 游客能看商品，但加入购物车会引导登录。
- 买家能完成商品浏览、购物车、下单、支付、取消、收货。
- 商家能申请入驻、查看申请状态、发布商品、查看审核状态、处理发货。
- 管理员能审核商家、审核商品、管理分类、查看经营看板。
- 不同角色看到不同菜单。
- 无权限访问时有清楚提示。
- 页面有加载、空数据、错误状态。

不要把系统做成营销首页。它应该是一个清晰、可信、业务导向的农产品交易系统。

## 5. 任务拆解与交付要求

### Task 1：共享前端工具和组件

目标：先建立后续页面都要用的基础能力。

需要新增：

- `frontend/src/constants/status.js`
- `frontend/src/utils/format.js`
- `frontend/src/components/PageState.vue`
- `frontend/src/components/StatusTag.vue`
- `frontend/src/components/PageHeader.vue`

需要修改：

- `frontend/src/styles.css`

交付标准：

- 后端枚举状态不再直接暴露给用户。
- 金额、时间、缺省文案有统一格式。
- 页面有统一的 loading、empty、error、forbidden 展示组件。
- 构建通过。

验收命令：

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

### Task 2：角色导航和路由权限

目标：解决“所有人看到一样菜单”和“只判断登录不判断角色”的问题。

需要修改：

- `frontend/src/api.js`
- `frontend/src/stores/auth.js`
- `frontend/src/router.js`
- `frontend/src/App.vue`

需要新增：

- `frontend/src/views/Forbidden.vue`

菜单规则：

- 游客：商品、登录。
- 买家：商品、购物车、我的订单、商家入驻。
- 商家：商品、购物车、我的订单、商家中心。
- 管理员：商品、管理后台、经营看板。

路由规则：

- `/products`：公开。
- `/cart`、`/orders`、`/merchant`：登录用户可访问。
- `/admin`、`/dashboard`：仅管理员可访问。
- 无权限访问进入 `/forbidden` 或安全页面。

交付标准：

- 普通用户看不到管理后台入口。
- 普通用户直接访问 `/admin` 不会看到管理页面。
- 未登录访问购物车、订单、商家页会去登录。
- 构建通过。

### Task 3：商品浏览页

目标：把商品页从简单卡片列表改成买家入口。

需要修改：

- `frontend/src/views/Products.vue`
- `frontend/src/styles.css`

页面要求：

- 显示商品图、名称、价格、单位、产地、库存。
- 搜索为空时有空状态。
- 加载失败有错误状态和重试按钮。
- 库存为 0 时不能购买。
- 游客点击加入购物车时引导登录。
- 登录用户加入购物车后有成功反馈。

交付标准：

- 页面不出现内部 ID。
- 用户能理解每个商品是什么、多少钱、还有多少库存。
- 构建通过。

### Task 4：购物车和下单

目标：购物车必须让用户知道自己在买什么。

需要修改：

- `frontend/src/views/Cart.vue`
- `frontend/src/styles.css`

页面要求：

- 显示商品图、商品名、单价、数量、小计、库存。
- 显示选中状态。
- 底部显示已选数量和总价。
- 收货信息和商品列表分区清楚。
- 未选商品或收货信息不完整时阻止提交并提示。
- 提交成功后跳转订单页。

注意：

当前购物车接口只返回购物车行信息。如果缺少商品名和价格，先在前端用 `GET /api/products/{productId}` 补齐。

交付标准：

- 页面不再只显示 `productId` 和 `merchantId`。
- 用户能看到总金额。
- 能提交订单。
- 构建通过。

### Task 5：买家订单页

目标：订单页要让买家看懂订单状态和下一步操作。

需要修改：

- `frontend/src/views/Orders.vue`

页面要求：

- 状态显示中文标签。
- 待支付订单显示支付、取消。
- 已发货订单显示确认收货。
- 已完成、已取消订单不显示无效操作。
- 显示订单号、金额、收货人、手机号、地址、创建时间。
- 无订单时显示空状态。
- 加载失败时显示错误状态。

交付标准：

- 不直接展示 `PENDING_PAYMENT` 这类枚举给用户。
- 操作按钮符合订单状态。
- 构建通过。

### Task 6：商家中心

目标：商家端从测试表单变成完整业务中心。

需要修改：

- `frontend/src/views/Merchant.vue`
- `frontend/src/styles.css`

页面结构：

- 入驻状态
- 商品管理
- 订单发货

入驻状态要求：

- 未申请时显示申请表。
- 已申请时显示店铺名、联系人、电话、产地、审核状态。
- 被拒绝时显示拒绝原因。

商品管理要求：

- 未通过商家审核时，不能发布商品。
- 发布商品字段包括名称、分类、价格、库存、单位、产地、图片、描述。
- 商品列表显示价格、库存、审核状态、销售状态。
- 只有审核通过的商品可以上架。
- 已上架商品可以下架。

订单发货要求：

- 显示订单号、金额、收货人、订单状态、创建时间。
- 只有待发货订单显示发货按钮。

交付标准：

- 商家入驻、发布商品、发货三个流程能从页面上理解。
- 构建通过。

### Task 7：管理后台和经营看板

目标：管理员页面从简单审核按钮升级为可验收后台。

需要修改：

- `frontend/src/views/Admin.vue`
- `frontend/src/views/Dashboard.vue`

管理后台结构：

- 商家审核
- 商品审核
- 分类管理

商家审核要求：

- 显示店铺、联系人、电话、产地、状态、申请时间。
- 只有待审核申请显示通过、拒绝按钮。

商品审核要求：

- 显示商品名、价格、库存、审核状态。
- 只有待审核商品显示通过、拒绝按钮。

分类管理要求：

- 可以新增分类。
- 可以看到已有分类列表。

看板要求：

- 仅管理员可访问。
- 显示订单数、销售额、商品数。
- 数据加载中、加载失败、无数据时有明确状态。

交付标准：

- 管理员能完成审核商家、审核商品、管理分类。
- 看板不会给普通用户显示空数据。
- 构建通过。

### Task 8：最终验收和报告

目标：证明前端整改不是“看起来改了”，而是真的能跑完整业务闭环。

需要新增：

- `reports/frontend-acceptance-2026-06-06.md`

需要修改：

- `memory-bank/progress.md`

验收内容：

- 构建通过。
- Docker 本地环境可启动。
- 直接访问核心路由不 404。
- 游客、买家、商家、管理员权限边界正确。
- 买家流程可走通。
- 商家流程可走通。
- 管理员流程可走通。
- 记录未完成风险。

最终验收命令：

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
docker compose up -d --build backend frontend nginx
docker compose ps
```

路由检查：

```powershell
$routes = @('/products','/login','/cart','/orders','/merchant','/admin','/dashboard','/forbidden')
foreach ($route in $routes) {
  $response = Invoke-WebRequest -UseBasicParsing "http://localhost$route"
  "$route $($response.StatusCode)"
}
```

## 6. 接口使用说明

### 6.1 商品与分类

- 商品列表：`GET /api/products`
- 商品详情：`GET /api/products/{productId}`
- 分类列表：`GET /api/product-categories`

商品页和购物车页优先使用这些接口补齐商品信息。

### 6.2 购物车

- 查询购物车：`GET /api/cart/items`
- 加入购物车：`POST /api/cart/items`
- 修改数量：`PUT /api/cart/items/{id}`
- 修改选中：`PUT /api/cart/items/{id}/selected`
- 删除单项：`DELETE /api/cart/items/{id}`
- 清空购物车：`DELETE /api/cart/items`

购物车接口当前可能不返回商品名称、价格、图片。前端可以用 `productId` 再查商品详情。

### 6.3 订单与支付

- 创建订单：`POST /api/orders`
- 我的订单：`GET /api/orders`
- 订单详情：`GET /api/orders/{orderId}`
- 模拟支付：`POST /api/payments/mock-pay`
- 取消订单：`POST /api/orders/{orderId}/cancel`
- 确认收货：`POST /api/orders/{orderId}/receive`

订单状态要通过 `StatusTag` 显示中文，不要直接显示后端枚举。

### 6.4 商家

- 提交入驻：`POST /api/merchant/apply`
- 我的入驻状态：`GET /api/merchant/apply/my`
- 商家商品：`GET /api/merchant/products`
- 发布商品：`POST /api/merchant/products`
- 商品上架：`POST /api/merchant/products/{productId}/on-sale`
- 商品下架：`POST /api/merchant/products/{productId}/off-sale`
- 商家订单：`GET /api/merchant/orders`
- 发货：`POST /api/merchant/orders/{orderId}/ship`

### 6.5 管理员

- 商家申请列表：`GET /api/admin/merchant-applications`
- 审核通过商家：`POST /api/admin/merchant-applications/{applyId}/approve`
- 拒绝商家：`POST /api/admin/merchant-applications/{applyId}/reject`
- 待审核商品：`GET /api/admin/products/audit`
- 审核通过商品：`POST /api/admin/products/{productId}/approve`
- 拒绝商品：`POST /api/admin/products/{productId}/reject`
- 新增分类：`POST /api/admin/product-categories`
- 统计概览：`GET /api/admin/statistics/overview`

## 7. 代码规范

### 7.1 Vue 页面规范

页面建议结构：

```vue
<template>
  <div class="page">
    <PageHeader title="页面标题" description="页面说明" />
    <PageState v-if="loading" title="正在加载" />
    <PageState v-else-if="error" type="error" title="加载失败" :description="error" />
    <template v-else>
      <!-- 页面主体 -->
    </template>
  </div>
</template>
```

每个业务页都应具备：

- loading 状态。
- empty 状态。
- error 状态。
- 状态标签中文化。
- 操作按钮按状态显示。

### 7.2 样式规范

- 优先复用 `styles.css` 中的通用类。
- 卡片圆角不超过 8px。
- 不做大面积单一绿色主题。
- 商品页可以更视觉化，后台页应紧凑、清晰。
- 不要写复杂动画。
- 不要使用营销落地页式大 Hero。

### 7.3 API 错误处理

- `api.js` 统一弹出基础错误。
- 页面需要用 `try/catch` 设置自己的 `error` 状态。
- 不要只依赖 toast。页面必须能告诉用户当前失败了什么。

### 7.4 状态枚举

禁止直接在页面显示这些枚举：

- `PENDING_PAYMENT`
- `PENDING_SHIPMENT`
- `SHIPPED`
- `COMPLETED`
- `CANCELED`
- `PENDING`
- `APPROVED`
- `REJECTED`
- `ON_SALE`
- `OFF_SALE`

必须通过 `frontend/src/constants/status.js` 和 `StatusTag.vue` 显示中文标签。

## 8. 每阶段验收清单

每个 Task 完成后，开发者必须提交以下信息：

- 修改了哪些文件。
- 为什么这样改。
- 如何验证。
- 构建是否通过。
- 页面是否手动看过。
- 是否有接口或数据限制。
- 是否有未完成风险。

推荐阶段报告格式：

```markdown
## Task N 完成报告

### 修改内容

- 

### 验证结果

- `npm run build`: PASS/FAIL
- 页面手动检查：

### 风险

- 
```

## 9. 最终验收标准

最终交付前，负责人应按以下顺序验收：

1. 游客打开 `http://localhost/products`。
2. 游客点击加入购物车，被引导登录。
3. 买家登录后加入购物车。
4. 买家在购物车看到商品名、价格、小计、总价。
5. 买家填写收货信息并提交订单。
6. 买家支付订单。
7. 管理员不能被普通用户看到。
8. 普通用户直接访问 `/admin` 被阻止。
9. 用户提交商家申请。
10. 管理员审核商家。
11. 商家发布商品。
12. 管理员审核商品。
13. 商家上架商品。
14. 买家购买该商品。
15. 商家发货。
16. 买家确认收货。
17. 管理员查看经营看板。

任意一步失败，都必须记录在验收报告中，不能只口头说明。

## 10. 风险与注意事项

- 当前前端没有自动化浏览器测试，最终验收主要依赖构建、接口验证和人工浏览器检查。
- 购物车和订单明细展示依赖后端数据完整性。如果现有接口不足，优先前端组合查询，再评估是否增加后端 DTO。
- 管理员、商家、买家测试账号需要结合本地数据库种子数据或手动注册生成。
- 不要为了视觉效果引入新依赖。
- 不要扩大到移动端深度适配。
- 不要把文档、计划和报告遗忘在本地，应按阶段提交。

## 11. 交付物

最终应包含：

- 前端代码改动。
- 每个阶段的 Git 提交。
- `reports/frontend-acceptance-2026-06-06.md`
- 更新后的 `memory-bank/progress.md`
- 可运行的本地 Docker 环境。

交付给项目负责人时，应说明：

- 哪些功能已经通过验收。
- 哪些功能仍有风险。
- 哪些问题需要后端继续补接口。
- 如何在本地重新运行和复验。
