# 项目进度

## 2026-06-05：商品列表类目查询参数校验

### 已完成

- 商品公开列表接口新增可选 `categoryId` 查询参数 guard。
- `categoryId` 不传时仍表示不按类目过滤。
- `categoryId` 传入非正数时在 Controller 入口拒绝。
- 无效类目 ID 不再继续进入商品公开列表查询逻辑。
- 扩展商品请求校验单元测试，覆盖公开列表查询参数的无效 ID 拒绝路径。

### 验证

- `mvn -Dtest=ProductRequestValidationTest test`：3 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，34 个测试通过。

### 遗留问题

- 本轮 Controller 参数入口清扫已覆盖主要 PathVariable 与已知可选 ID 查询参数；后续可转向前端构建验证或新功能开发。

## 2026-06-05：支付订单 ID 参数校验

### 已完成

- 模拟支付请求的 `orderId` 从必填增强为必须为正数。
- 支付记录按订单查询接口新增订单 ID guard。
- `orderId` 为空或非正数时在 Controller 或 Bean Validation 入口拒绝。
- 无效订单 ID 不再继续进入支付记录查询逻辑。
- 扩展支付请求校验单元测试，覆盖请求体和路径参数的无效订单 ID 拒绝路径。

### 验证

- `mvn -Dtest=PaymentRequestValidationTest test`：3 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，33 个测试通过。

### 遗留问题

- Controller 参数清扫还剩商品公开列表可选 `categoryId` 查询参数可考虑补正数校验。

## 2026-06-05：类目 ID 参数校验

### 已完成

- 后台类目修改和状态切换接口新增类目 ID guard。
- `categoryId` 为空或非正数时在 Controller 入口拒绝。
- 无效类目 ID 不再继续进入管理员权限校验或数据库更新逻辑。
- 扩展类目请求校验单元测试，覆盖后台类目操作入口的无效 ID 拒绝路径。

### 验证

- `mvn -Dtest=CategoryRequestValidationTest test`：3 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，31 个测试通过。

### 遗留问题

- 可再做一次 Controller 参数入口清扫，确认是否还有未覆盖的 PathVariable 或 Query 参数防护。

## 2026-06-05：商家申请 ID 参数校验

### 已完成

- 后台商家申请详情、审核通过和驳回接口新增申请 ID guard。
- `applyId` 为空或非正数时在 Controller 入口拒绝。
- 无效商家申请 ID 不再继续进入申请列表查询或审核业务逻辑。
- 扩展商家请求校验单元测试，覆盖多个后台申请操作入口的无效 ID 拒绝路径。

### 验证

- `mvn -Dtest=MerchantRequestValidationTest test`：3 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，30 个测试通过。

### 遗留问题

- 若继续补 PathVariable 防护，可处理类目更新/状态切换等后台单 ID 入口。

## 2026-06-05：购物车项 ID 参数校验

### 已完成

- 购物车修改数量、删除和单项选中接口新增购物车项 ID guard。
- `cartItemId` 为空或非正数时在 Controller 入口拒绝。
- 无效购物车项 ID 不再继续进入归属校验或数据库查询逻辑。
- 扩展购物车请求校验单元测试，覆盖多个购物车项操作入口的无效 ID 拒绝路径。

### 验证

- `mvn -Dtest=CartRequestValidationTest test`：4 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，29 个测试通过。

### 遗留问题

- 若继续补 PathVariable 防护，可处理商家申请审核、类目更新/状态切换等后台单 ID 入口。

## 2026-06-05：订单与消息单条 ID 参数校验

### 已完成

- 订单详情、取消、商家发货和用户收货接口新增订单 ID guard。
- 消息单条已读和删除接口新增消息 ID guard。
- `orderId`、`messageId` 为空或非正数时在 Controller 入口拒绝。
- 无效 ID 不再继续进入订单履约、消息查询或删除逻辑。
- 扩展订单与消息请求校验单元测试，覆盖单条操作入口的无效 ID 拒绝路径。

### 验证

- `mvn -Dtest=OrderRequestValidationTest,MessageRequestValidationTest test`：5 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，28 个测试通过。

### 遗留问题

- 若继续补 PathVariable 防护，可检查购物车删除、商家申请审核、类目更新等其他单 ID 入口。

## 2026-06-05：商品操作 ID 参数校验

### 已完成

- 商品修改、上下架、审核和详情接口新增商品 ID guard。
- `productId` 为空或非正数时在 Controller 入口拒绝。
- 无效商品 ID 不再继续进入商家权限、管理员权限或数据库查询逻辑。
- 扩展商品请求校验单元测试，覆盖多个商品操作入口的无效 ID 拒绝路径。

### 验证

- `mvn -Dtest=ProductRequestValidationTest test`：2 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，26 个测试通过。

### 遗留问题

- 若继续补 PathVariable 防护，可处理订单、消息单条读取/删除等 ID 参数。

## 2026-06-05：类目状态切换参数校验

### 已完成

- 后台类目启用/禁用接口新增状态值 guard。
- 状态切换只允许 `ENABLED` 或 `DISABLED`。
- 非法状态会在权限校验和数据库更新前被拒绝，避免脏状态写入。
- 扩展类目请求校验单元测试，覆盖非法状态拒绝路径。

### 验证

- `mvn -Dtest=CategoryRequestValidationTest test`：2 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，25 个测试通过。

### 遗留问题

- 若继续打磨后台管理入口，可补充商品审核/上下架等 PathVariable 正数校验。

## 2026-06-05：账号请求参数校验增强

### 已完成

- 注册请求限制用户名长度为 3 到 32 位。
- 注册与登录请求限制密码长度为 6 到 64 位。
- 注册请求限制手机号格式为中国大陆 11 位手机号。
- 注册请求限制昵称最多 32 位。
- 新增账号请求校验单元测试，覆盖注册与登录的字段形状约束。

### 验证

- `mvn -Dtest=AuthRequestValidationTest test`：2 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，24 个测试通过。

### 遗留问题

- 若继续增强账号安全，可补充密码复杂度策略、登录失败限流或验证码流程。

## 2026-06-05：消息批量已读请求校验

### 已完成

- 消息批量已读接口保留原有 JSON 数组入参，新增入口 guard。
- 批量消息 ID 列表不能为空。
- 批量消息 ID 不能包含空值或非正数。
- 新增消息请求校验单元测试，覆盖空列表、空 ID 和非正 ID。

### 验证

- `mvn -Dtest=MessageRequestValidationTest test`：2 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，22 个测试通过。

### 遗留问题

- 当前参数校验已覆盖主要订单链路、后台类目和消息批量入口；若继续打磨，可转向结构化校验错误响应或前端展示。

## 2026-06-05：类目请求参数校验

### 已完成

- 后台类目创建与修改请求启用 Bean Validation。
- 类目名称要求非空，排序值不能为负数。
- 类目状态限制为 `ENABLED` 或 `DISABLED`；创建时状态为空仍沿用默认 `ENABLED`。
- 新增类目请求校验单元测试，覆盖约束声明和 Controller `@Valid` 启用情况。

### 验证

- `mvn -Dtest=CategoryRequestValidationTest test`：1 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，20 个测试通过。

### 遗留问题

- 当前参数校验已覆盖主要订单链路、后台类目和消息批量入口；若继续打磨，可转向结构化校验错误响应或前端展示。

## 2026-06-05：支付与商家请求参数校验

### 已完成

- 模拟支付请求启用 Bean Validation，要求 `orderId` 必填。
- 商家入驻申请启用 Bean Validation，要求店铺名、联系人、联系电话和产地地址非空。
- 管理员驳回商家申请请求启用 Bean Validation，要求驳回原因非空。
- 新增支付与商家请求校验单元测试，覆盖约束声明和 Controller `@Valid` 启用情况。

### 验证

- `mvn -Dtest=PaymentRequestValidationTest,MerchantRequestValidationTest test`：3 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，19 个测试通过。

### 遗留问题

- 当前参数校验已覆盖主要订单链路、后台类目和消息批量入口；若继续打磨，可转向结构化校验错误响应或前端展示。

## 2026-06-05：参数校验错误详情

### 已完成

- 全局参数校验异常处理从固定“参数校验失败”升级为包含字段级错误详情。
- `MethodArgumentNotValidException` 与 `BindException` 统一从 `BindingResult` 提取错误。
- 字段错误按 `field: message` 拼接，保留原有 `Result` 失败响应结构。
- 新增全局异常处理单元测试，覆盖多字段错误详情返回。

### 验证

- `mvn -Dtest=GlobalExceptionHandlerValidationTest test`：1 个测试通过。
- `mvn clean test`：重新编译 53 个生产源码，16 个测试通过。

### 遗留问题

- 当前字段错误详情放在 `message` 字符串里；若前端需要逐字段高亮，可后续扩展为结构化 `data`。

## 2026-06-05：核心请求参数校验

### 已完成

- 购物车新增、修改数量、单项选中和全选请求启用 Bean Validation。
- 购物车请求限制商品 ID 必填、数量必须为正数、选中值只能为 `0` 或 `1`。
- 下单请求要求收货人、手机号和收货地址非空。
- 商品发布与修改请求要求分类、名称、价格、库存和单位满足基础约束。
- 新增请求校验单元测试，覆盖约束声明和 Controller `@Valid` 启用情况。

### 验证

- `mvn clean test`：重新编译 53 个生产源码，15 个测试通过。

### 遗留问题

- 当前参数校验已覆盖主要订单链路、后台类目和消息批量入口；若继续打磨，可转向结构化校验错误响应或前端展示。

## 2026-06-05：订单状态原子流转

### 已完成

- 支付使用数据库条件更新完成 `PENDING_PAYMENT → WAIT_SHIPMENT`。
- 取消使用数据库条件更新完成 `PENDING_PAYMENT → CANCELLED`，成功抢占状态后才返还库存。
- 发货使用商家归属与旧状态条件完成 `WAIT_SHIPMENT → SHIPPED`。
- 收货使用用户归属与旧状态条件完成 `SHIPPED → COMPLETED`。
- 支付、取消、发货和收货的重复或竞争请求受影响行数为 0，不继续执行后续业务。
- 新增 H2 Mapper 集成测试，覆盖四类状态流转只能成功一次。

### 验证

- `mvn clean test`：重新编译 53 个生产源码，10 个测试通过。

### 遗留问题

- 支付记录状态仍通过普通更新写入，但订单状态条件更新保证只有成功抢占订单状态的事务会继续更新支付记录。
- 尚未实现超时自动取消；后续消费者应复用相同的待支付状态条件更新模式。

## 2026-06-05：订单库存原子更新

### 已完成

- `ProductMapper` 新增数据库条件原子扣减与原子返还库存操作。
- 下单流程使用 `stock >= quantity`、商品已上架和正数量条件进行原子扣减。
- 取消订单使用数据库原子累加返还库存。
- 新增 H2 Mapper 集成测试，覆盖正常扣减、库存不足、下架商品、非正数量和库存返还。

### 验证

- `mvn clean test`：重新编译 53 个生产源码，6 个测试通过。

### 遗留问题

- 购物车接口本身尚未校验数量必须为正数；库存 Mapper 会拒绝非正数量，订单事务不会因此修改库存。
- Redis 商品级库存锁未实现；当前数据库原子更新已负责防止库存读后写覆盖。
- 前端生产构建当前受缺失的 Rollup Windows 可选依赖阻塞。

## 2026-06-05：前端生产构建验证

### 已完成

- 复现前端构建环境问题：当前 Windows 环境没有可用 `npm`，只能使用 bundled Node 直接执行 Vite。
- 确认 `frontend/node_modules` 中缺失 `@rollup/rollup-win32-x64-msvc`，且仅存在 Linux 版 Rollup 可选包。
- 按 `package-lock.json` 声明的精确版本，在本地 ignored `node_modules` 中恢复 `@rollup/rollup-win32-x64-msvc@4.61.0`。
- 继续定位并确认 `esbuild` 平台包不匹配：本地仅存在 `@esbuild/linux-x64`，当前平台需要 `@esbuild/win32-x64`。
- 按 `package-lock.json` 声明的精确版本，在本地 ignored `node_modules` 中恢复 `@esbuild/win32-x64@0.25.12`。

### 验证

- `node .\node_modules\vite\bin\vite.js build`：生产构建通过，2225 个模块完成转换，产物写入 `frontend/dist/`。

### 遗留问题

- 当前构建仍有 Rollup 注释移除警告和入口 JS chunk 超过 500 kB 的体积警告；不影响本次构建通过，可后续通过路由懒加载或 `manualChunks` 优化。
- 本机没有可用 `npm`/`corepack`，后续若重装依赖，建议在 Windows 环境执行 `npm ci` 或补齐包管理器，避免再次混入 Linux 平台 `node_modules`。

## 2026-06-05：前端构建体积优化

### 已完成

- 前端路由视图改为懒加载，避免所有页面和看板依赖进入首屏入口包。
- 看板页 ECharts 改为按需注册柱状图、网格组件和 Canvas 渲染器，避免全量引入 ECharts。
- Element Plus 从全量插件注册改为只注册当前模板实际使用的组件。
- `ElMessage` 改为组件级入口导入，避免从 Element Plus 根入口拉入整库。
- Vite 构建配置新增 `manualChunks`，将 Vue/Pinia、Element Plus、ECharts 和其他第三方依赖分离成稳定 vendor chunk。

### 验证

- `node .\node_modules\vite\bin\vite.js build`：生产构建通过，1573 个模块完成转换，已消除单个 JS chunk 超过 500 kB 的警告。
- 优化后主要 JS chunk：入口约 6.30 kB，Vue vendor 约 114.10 kB，Element Plus 约 222.82 kB，ECharts 约 261.51 kB，通用 vendor 约 293.77 kB。
- Vite preview 基础验收：`/`、`/products`、`/login`、`/dashboard` 均返回 200，首页引用的主要 JS/CSS 资产均返回 200。

### 遗留问题

- Element Plus CSS 仍使用全量样式，压缩后约 357.33 kB、gzip 后约 47.79 kB；当前不触发 Vite 大 chunk 警告，后续若追求更小首屏 CSS，可切换为组件级样式导入。
- 由于本机没有 Playwright/Puppeteer 依赖，本次只做了生产构建和 HTTP 级预览验收，未做浏览器截图级交互验收。
