# 前端第二阶段验收报告

**日期**：2026-06-06  
**分支**：codex/frontend-redesign  
**构建**：Vite 1608 模块，~10.3s，0 错误

---

## Step 1：商家商品编辑

| 验收项 | 状态 |
|--------|------|
| 商品表格显示"编辑"按钮 | ✓ |
| 点击编辑弹出对话框，回填全部字段 | ✓ |
| 修改后调用 `PUT /api/merchant/products/{productId}` | ✓ |
| 成功后刷新列表并提示"需重新审核" | ✓ |
| 构建通过 | ✓ |

## Step 2：分类编辑与启用/禁用

| 验收项 | 状态 |
|--------|------|
| 分类状态显示中文标签（启用/禁用） | ✓ |
| 编辑按钮弹出对话框，含名称/排序/状态 | ✓ |
| 提交调用 `PUT /api/admin/product-categories/{categoryId}` | ✓ |
| 启用/禁用按钮调用 `POST .../status?status=ENABLED\|DISABLED` | ✓ |
| 操作后刷新列表 | ✓ |
| 构建通过 | ✓ |

## Step 3：审核拒绝原因弹窗

| 验收项 | 状态 |
|--------|------|
| 商家拒绝弹出输入框，用户填写原因 | ✓ |
| 调用 `POST .../reject` 并传入 `rejectReason` | ✓ |
| 商品拒绝弹出确认输入框 | ✓ |
| 拒绝后刷新列表 | ✓ |
| 构建通过 | ✓ |

## Step 4：商家中心增强展示

| 验收项 | 状态 |
|--------|------|
| 商品表格新增"销量"列 | ✓ |
| 订单表格新增"电话""地址"列 | ✓ |
| 编辑按钮始终可见 | ✓ |
| 上架/下架/发货按钮约束正确 | ✓ |
| 发货按钮从 `PENDING_SHIPMENT` 修复为 `WAIT_SHIPMENT` | ✓ |
| `ORDER_STATUS` 常量修正（`PENDING_SHIPMENT` → `WAIT_SHIPMENT`） | ✓ |
| 构建通过 | ✓ |

## 后端限制

1. **商品编辑后状态重置**：`ProductService.update()` 将 `auditStatus` 重置为 PENDING、`saleStatus` 重置为 OFF_SALE。前端已显示"需重新审核"提示，这是后端既有行为。

2. **商品拒绝不持久化原因**：`POST /api/admin/products/{productId}/reject` 无拒绝原因参数（`productService.audit(id, "REJECTED")` 只改状态）。前端弹窗仅作为操作确认，占位符已注明限制。

3. **分类管理缺全量查询**：`GET /api/product-categories` 仅返回 ENABLED 分类；管理员无法在页面中查看或重新启用已禁用的分类（需后端新增管理端分类查询接口）。

## 第一阶段功能保持

| 功能 | 状态 |
|------|------|
| 商品详情页 | 未改动 |
| 分类筛选 | 未改动 |
| 订单详情渲染 | 未改动 |
| 消息中心 | 未改动 |
| 管理员订单管理 | 仅新增分类操作列，未影响订单 tab |

## 文件变更清单

```text
 M frontend/src/constants/status.js          # WAIT_SHIPMENT 替代 PENDING_SHIPMENT
 M frontend/src/views/Merchant.vue           # 编辑对话框、销量列、电话/地址、发货修复
 M frontend/src/views/Admin.vue              # 分类编辑、状态切换、拒绝弹窗
```
