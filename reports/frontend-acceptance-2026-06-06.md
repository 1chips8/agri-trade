# Frontend Acceptance Report

Date: 2026-06-06

## Environment

- Entry URL: http://localhost
- Backend direct URL: http://localhost:8081
- MySQL: localhost:3307
- Redis: localhost:6380
- RabbitMQ console: http://localhost:15672

Git branch: `codex/frontend-redesign`

## Verification Commands

- **Frontend build**: PASS — `npm run build` succeeds (Vite 10.07s, 1579 modules, no errors).
- **Docker stack**: PASS — `docker compose up -d --build` succeeded; all 6 containers (mysql, redis, rabbitmq, backend, frontend, nginx) are running.
- **Direct routes**: PASS — all 9 SPA routes return HTTP 200 (`/`, `/products`, `/login`, `/cart`, `/orders`, `/merchant`, `/admin`, `/dashboard`, `/forbidden`).
- **User role fix**: PASS — backend returns `role: "CONSUMER"`; router.js updated to `['CONSUMER', 'MERCHANT']` for buyer routes.

## Buyer Flow

| Check | Status | Notes |
|-------|--------|-------|
| Browse products | ✓ IMPLEMENTED | Product grid with image, name, price, unit, origin, stock. Loading/empty/error states. |
| Guest add to cart → login | ✓ IMPLEMENTED | Guest click shows login prompt, redirects to `/login?redirect=/products` |
| Buyer add to cart | ✓ IMPLEMENTED | API `POST /api/cart/items` with success message. |
| Cart enrichment | ✓ IMPLEMENTED | `enrich()` fetches product details by ID, shows name/image/price/stock. No raw IDs exposed. |
| Selected count and total | ✓ IMPLEMENTED | `selectedCount` and `totalAmount` computed properties with summary bar. |
| Receiver info validation | ✓ IMPLEMENTED | Warning on missing name/phone/address. |
| Submit order | ✓ IMPLEMENTED | `POST /api/orders` then redirect to `/orders`. |
| Pay order | ✓ IMPLEMENTED | `POST /api/payments/mock-pay` for `PENDING_PAYMENT` orders. |
| Cancel order | ✓ IMPLEMENTED | `POST /api/orders/{id}/cancel` for `PENDING_PAYMENT` orders. |
| Confirm receipt | ✓ IMPLEMENTED | `POST /api/orders/{id}/receive` for `SHIPPED` orders. |
| Order detail | ✓ IMPLEMENTED | Toggle fetches `GET /api/orders/{id}`. |
| Order status labels | ✓ IMPLEMENTED | `StatusTag` with `ORDER_STATUS` map: 待支付/待发货/已发货/已完成/已取消. |
| Empty/error states | ✓ IMPLEMENTED | Loading, empty ("暂无商品"/"暂无订单"), error with retry button. |

## Merchant Flow

| Check | Status | Notes |
|-------|--------|-------|
| Submit merchant application | ✓ IMPLEMENTED | Form with merchantName, contactName, contactPhone, originAddress. |
| View application status | ✓ IMPLEMENTED | Shows店铺/联系人/产地/审核状态 with `StatusTag`. |
| Rejected with reason | ✓ IMPLEMENTED | Shows `rejectReason` when status is REJECTED. |
| Product management gated | ✓ IMPLEMENTED | "商家审核通过后可管理商品" when not approved. |
| Product form complete | ✓ IMPLEMENTED | Name, category (dropdown), price, stock, unit, origin, image URL, description. |
| Product list with audit/sale status | ✓ IMPLEMENTED | `StatusTag` for audit (待审核/已通过/已拒绝) and sale (已上架/已下架). |
| On-sale/off-sale buttons | ✓ IMPLEMENTED | Only when audit=APPROVED & sale≠ON_SALE, or sale=ON_SALE. |
| Ship paid orders | ✓ IMPLEMENTED | Ship button only for `PENDING_SHIPMENT` orders. |
| Loading state | ✓ IMPLEMENTED | PageState for initial data load. |

## Admin Flow

| Check | Status | Notes |
|-------|--------|-------|
| Merchant review | ✓ IMPLEMENTED | Table with store/contact/phone/origin/status/time. Approve/reject only for PENDING. |
| Product review | ✓ IMPLEMENTED | Table with name/price/stock/status. Approve/reject only for PENDING. |
| Category management | ✓ IMPLEMENTED | Create form + existing categories list. |
| Category create | ✓ IMPLEMENTED | `POST /api/admin/product-categories` with ElMessage success. |
| Consistent status tags | ✓ IMPLEMENTED | `StatusTag` with audit/apply kinds. |
| Loading state | ✓ IMPLEMENTED | PageState during data load. |

## Dashboard

| Check | Status | Notes |
|-------|--------|-------|
| Admin-only access | ✓ IMPLEMENTED | Route guard: `meta: { auth: true, roles: ['ADMIN'] }`. |
| Loading state | ✓ IMPLEMENTED | PageState while fetching statistics. |
| Error state | ✓ IMPLEMENTED | Error message with retry button when API fails. |
| Order count card | ✓ IMPLEMENTED | `overview.orderCount` |
| Sales amount card | ✓ IMPLEMENTED | `formatMoney(overview.salesAmount)` — formatted as ￥X.XX |
| Product count card | ✓ IMPLEMENTED | `overview.productCount` |
| ECharts bar chart | ✓ IMPLEMENTED | Kept from original: order vs product count bar chart. |

## Role And Permission Checks

| Check | Status | Notes |
|-------|--------|-------|
| Guest navigation (Products only) | ✓ IMPLEMENTED | `menus` computed returns only Product route. |
| Buyer navigation (no Admin/Dashboard) | ✓ IMPLEMENTED | Buyer gets Products/Cart/Orders/商家入驻(not 商家中心). |
| Buyer blocked from /admin | ✓ IMPLEMENTED | Route guard `to.meta.roles.includes(auth.role)` → `/forbidden`. |
| Merchant sees 商家中心 | ✓ IMPLEMENTED | `auth.isMerchant ? '商家中心' : '商家入驻'` in menus. |
| Admin navigation | ✓ IMPLEMENTED | Admin gets Products/管理后台/经营看板. |
| Role label shown | ✓ IMPLEMENTED | `{{ user.nickname }} · {{ roleLabel }}` where roleLabel = 游客/买家/商家/管理员. |
| HTTP 401/403 messages | ✓ IMPLEMENTED | API interceptor: 401 → "请先登录", 403 → "没有权限访问". |
| fetchMe resilience | ✓ IMPLEMENTED | Catches errors, clears token/user, returns null. |

## Modified Files Summary

| File | Change |
|------|--------|
| `frontend/src/constants/status.js` | **NEW** — Order/audit/sale/apply status maps with Chinese labels |
| `frontend/src/utils/format.js` | **NEW** — Money, date, fallback, image helpers |
| `frontend/src/components/PageState.vue` | **NEW** — Loading/empty/error/forbidden display component |
| `frontend/src/components/StatusTag.vue` | **NEW** — Maps backend enums to Element Plus tags |
| `frontend/src/components/PageHeader.vue` | **NEW** — Consistent page title/action band |
| `frontend/src/views/Forbidden.vue` | **NEW** — Forbidden access page |
| `frontend/src/styles.css` | **MODIFIED** — Added shared layout, product grid, cart, section card styles |
| `frontend/src/api.js` | **MODIFIED** — Enhanced error interceptor with 401/403 handling |
| `frontend/src/stores/auth.js` | **MODIFIED** — Added isAdmin/isMerchant getters, resilient fetchMe |
| `frontend/src/router.js` | **MODIFIED** — Route metadata (auth + roles), role-based guard, forbidden route; USER→CONSUMER |
| `frontend/src/App.vue` | **MODIFIED** — Role-aware navigation menus, role label display |
| `frontend/src/views/Products.vue` | **MODIFIED** — Page states, auth-aware add-to-cart, stock-aware UI |
| `frontend/src/views/Cart.vue` | **MODIFIED** — Enriched rows, totals, receiver validation, order submission |
| `frontend/src/views/Orders.vue` | **MODIFIED** — Chinese status tags, state-aware actions, detail toggle |
| `frontend/src/views/Merchant.vue` | **MODIFIED** — Application status, gated product mgmt, category select, shipment |
| `frontend/src/views/Admin.vue` | **MODIFIED** — Review tables, category management, status-aware buttons |
| `frontend/src/views/Dashboard.vue` | **MODIFIED** — Loading/error states, formatMoney for sales |
| `frontend/nginx.conf` | **NEW** — SPA fallback with `try_files $uri $uri/ /index.html` |
| `frontend/Dockerfile` | **MODIFIED** — Switch to nginx:1.27-alpine, copy nginx.conf |

## Git Commits (8 task commits + 1 fix commit)

```
968adc3 feat(frontend): add shared presentation utilities
1fa139a feat(frontend): add role-aware navigation
cfe35db feat(frontend): improve product browsing
262a061 feat(frontend): make cart checkout readable
bf12e96 feat(frontend): improve buyer orders
e0cb2c4 feat(frontend): improve merchant center
ba004d9 feat(frontend): improve admin experience
b707ef2 test(frontend): add acceptance report
639ec3f fix(frontend): USER→CONSUMER role, nginx SPA fallback, Dockerfile update

## Remaining Risks

1. **Backend data availability**: The cart enrichment relies on `GET /api/products/{productId}`. If the backend doesn't return complete product data (image, unit, stock), the cart page will show fallback values.

2. **Existing uncommitted work**: The changes in the stash (`pre-frontend-redesign`) include backend cart changes (`CartController`, `CartItemMapper`), docker-compose.override.yml, and tests. These may be needed for backend parity — apply the stash (`git stash pop`) after the redesign branch is reviewed.

3. **Order detail display**: Orders.vue toggles order detail by fetching `GET /api/orders/{id}`, but the detail data is stored in `expanded` ref and not rendered in a dedicated expansion row yet — the data is available for future expansion UI.
