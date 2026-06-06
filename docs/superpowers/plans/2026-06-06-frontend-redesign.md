# Frontend Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rework the Vue frontend into a portfolio-ready agricultural trading system with clear buyer, merchant, and admin flows.

**Architecture:** Keep the existing Vue 3, Vue Router, Pinia, Axios, Element Plus, and ECharts stack. Add small shared utilities/components for status labels, formatting, page states, and route permissions, then improve each business flow without a broad framework rewrite.

**Tech Stack:** Vue 3 SFCs, Pinia, Vue Router 4, Axios, Element Plus, ECharts, Vite, Spring Boot APIs under `/api`.

---

## Scope And Constraints

- Primary target is desktop browser acceptance.
- Do not migrate frameworks, add a UI framework, or create a marketing landing page.
- Use existing backend APIs first:
  - `GET /api/products`
  - `GET /api/products/{productId}`
  - `GET /api/product-categories`
  - `GET /api/cart/items`
  - `POST /api/cart/items`
  - `PUT /api/cart/items/{id}`
  - `PUT /api/cart/items/{id}/selected`
  - `DELETE /api/cart/items/{id}`
  - `DELETE /api/cart/items`
  - `POST /api/orders`
  - `GET /api/orders`
  - `GET /api/orders/{orderId}`
  - `POST /api/payments/mock-pay`
  - `POST /api/orders/{orderId}/cancel`
  - `POST /api/orders/{orderId}/receive`
  - `POST /api/merchant/apply`
  - `GET /api/merchant/apply/my`
  - `GET /api/merchant/products`
  - `POST /api/merchant/products`
  - `POST /api/merchant/products/{productId}/on-sale`
  - `POST /api/merchant/products/{productId}/off-sale`
  - `GET /api/merchant/orders`
  - `POST /api/merchant/orders/{orderId}/ship`
  - `GET /api/admin/merchant-applications`
  - `POST /api/admin/merchant-applications/{applyId}/approve`
  - `POST /api/admin/merchant-applications/{applyId}/reject`
  - `GET /api/admin/products/audit`
  - `POST /api/admin/products/{productId}/approve`
  - `POST /api/admin/products/{productId}/reject`
  - `POST /api/admin/product-categories`
  - `GET /api/admin/statistics/overview`
- Only add backend DTO endpoints if frontend enrichment with existing APIs is insufficient.
- Respect current uncommitted work. Before each task, run `git status --short` and stage only files changed by that task.

## Planned File Structure

- Modify `frontend/src/api.js`: centralized HTTP errors and auth/permission signaling.
- Modify `frontend/src/router.js`: route metadata for auth and roles.
- Modify `frontend/src/App.vue`: role-aware navigation and account display.
- Modify `frontend/src/styles.css`: shared layout, toolbar, state, product/cart/order/admin styles.
- Modify `frontend/src/stores/auth.js`: role helpers and resilient `fetchMe`.
- Create `frontend/src/constants/status.js`: status dictionaries for orders, audits, sales, merchant applications, and roles.
- Create `frontend/src/utils/format.js`: money, date, fallback, and image helpers.
- Create `frontend/src/components/PageState.vue`: loading/empty/error/forbidden display.
- Create `frontend/src/components/StatusTag.vue`: maps backend status codes to Element Plus tags.
- Create `frontend/src/components/PageHeader.vue`: consistent page title/action band.
- Modify `frontend/src/views/Products.vue`: buyer product browsing and add-to-cart behavior.
- Modify `frontend/src/views/Cart.vue`: enriched cart rows, totals, receiver form, and order submission.
- Modify `frontend/src/views/Orders.vue`: readable order list with status-aware actions and details.
- Modify `frontend/src/views/Merchant.vue`: merchant application, product management, and shipment tabs.
- Modify `frontend/src/views/Admin.vue`: review tables and category management.
- Modify `frontend/src/views/Dashboard.vue`: admin-only states and clearer metrics.
- Modify `memory-bank/progress.md`: record completed phases and verification results.
- Create or update `reports/frontend-acceptance-2026-06-06.md`: final acceptance report.

---

### Task 1: Establish Shared Frontend Utilities And Components

**Files:**
- Create: `frontend/src/constants/status.js`
- Create: `frontend/src/utils/format.js`
- Create: `frontend/src/components/PageState.vue`
- Create: `frontend/src/components/StatusTag.vue`
- Create: `frontend/src/components/PageHeader.vue`
- Modify: `frontend/src/styles.css`

- [ ] **Step 1: Add status dictionaries**

Create `frontend/src/constants/status.js`:

```js
export const ORDER_STATUS = {
  PENDING_PAYMENT: { label: '待支付', type: 'warning' },
  PENDING_SHIPMENT: { label: '待发货', type: 'primary' },
  SHIPPED: { label: '已发货', type: 'success' },
  COMPLETED: { label: '已完成', type: 'success' },
  CANCELED: { label: '已取消', type: 'info' }
}

export const AUDIT_STATUS = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}

export const SALE_STATUS = {
  ON_SALE: { label: '已上架', type: 'success' },
  OFF_SALE: { label: '已下架', type: 'info' }
}

export const APPLY_STATUS = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}

export const ROLE_LABEL = {
  GUEST: '游客',
  USER: '买家',
  MERCHANT: '商家',
  ADMIN: '管理员'
}

export function statusMeta(map, value) {
  return map[value] || { label: value || '未知', type: 'info' }
}
```

- [ ] **Step 2: Add formatting helpers**

Create `frontend/src/utils/format.js`:

```js
export const fallbackProductImage =
  'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80'

export function formatMoney(value) {
  const amount = Number(value || 0)
  return `￥${amount.toFixed(2)}`
}

export function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

export function textOrDash(value) {
  return value === null || value === undefined || value === '' ? '-' : value
}

export function productImage(value) {
  return value || fallbackProductImage
}
```

- [ ] **Step 3: Add shared page state component**

Create `frontend/src/components/PageState.vue`:

```vue
<template>
  <div class="page-state" :class="`page-state--${type}`">
    <h3>{{ title }}</h3>
    <p v-if="description">{{ description }}</p>
    <div v-if="$slots.default" class="page-state__actions">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: { type: String, default: 'empty' },
  title: { type: String, required: true },
  description: { type: String, default: '' }
})
</script>
```

- [ ] **Step 4: Add shared status tag**

Create `frontend/src/components/StatusTag.vue`:

```vue
<template>
  <el-tag :type="meta.type" effect="light">{{ meta.label }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { AUDIT_STATUS, APPLY_STATUS, ORDER_STATUS, SALE_STATUS, statusMeta } from '../constants/status'

const props = defineProps({
  kind: { type: String, required: true },
  value: { type: String, default: '' }
})

const maps = {
  order: ORDER_STATUS,
  audit: AUDIT_STATUS,
  sale: SALE_STATUS,
  apply: APPLY_STATUS
}

const meta = computed(() => statusMeta(maps[props.kind] || {}, props.value))
</script>
```

- [ ] **Step 5: Add shared page header**

Create `frontend/src/components/PageHeader.vue`:

```vue
<template>
  <div class="page-header">
    <div>
      <h2>{{ title }}</h2>
      <p v-if="description">{{ description }}</p>
    </div>
    <div v-if="$slots.actions" class="page-header__actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' }
})
</script>
```

- [ ] **Step 6: Add shared CSS**

Append to `frontend/src/styles.css`:

```css
.page {
  max-width: 1180px;
  margin: 0 auto;
}

.page-header,
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.page-header h2,
.toolbar h2 {
  margin: 0;
  font-size: 22px;
}

.page-header p,
.muted {
  margin: 4px 0 0;
  color: #667085;
}

.page-header__actions,
.page-state__actions {
  display: flex;
  gap: 8px;
}

.page-state {
  display: grid;
  place-items: center;
  min-height: 220px;
  padding: 32px;
  text-align: center;
  color: #475467;
  border: 1px dashed #d0d5dd;
  border-radius: 8px;
  background: #fcfcfd;
}

.page-state h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #344054;
}

.page-state p {
  margin: 0;
}

.price {
  color: #c2410c;
  font-weight: 700;
}

.table-product {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-product img {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 6px;
}

.summary-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 16px;
  padding: 16px;
  border: 1px solid #e4e7ec;
  border-radius: 8px;
  background: #fff;
}
```

- [ ] **Step 7: Verify build**

Run:

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Expected: Vite build succeeds.

- [ ] **Step 8: Commit task**

```powershell
git add frontend/src/constants/status.js frontend/src/utils/format.js frontend/src/components/PageState.vue frontend/src/components/StatusTag.vue frontend/src/components/PageHeader.vue frontend/src/styles.css
git commit -m "feat(frontend): add shared presentation utilities"
```

---

### Task 2: Add Role-Aware Navigation And Route Guards

**Files:**
- Modify: `frontend/src/api.js`
- Modify: `frontend/src/stores/auth.js`
- Modify: `frontend/src/router.js`
- Modify: `frontend/src/App.vue`

- [ ] **Step 1: Improve API error objects**

Update `frontend/src/api.js` response interceptor so callers can distinguish business errors:

```js
api.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && body.code !== 0) {
      const message = body.message || '请求失败'
      const error = new Error(message)
      error.code = body.code
      error.responseBody = body
      ElMessage.error(message)
      return Promise.reject(error)
    }
    return body.data
  },
  (error) => {
    const status = error.response?.status
    const message = status === 401
      ? '请先登录'
      : status === 403
        ? '没有权限访问'
        : error.message || '网络异常'
    const normalized = new Error(message)
    normalized.status = status
    normalized.original = error
    ElMessage.error(message)
    return Promise.reject(normalized)
  }
)
```

- [ ] **Step 2: Extend auth store role helpers**

Update `frontend/src/stores/auth.js` getters:

```js
getters: {
  isLogin: (state) => Boolean(state.token),
  role: (state) => state.user?.role || 'GUEST',
  isAdmin: (state) => state.user?.role === 'ADMIN',
  isMerchant: (state) => state.user?.role === 'MERCHANT'
}
```

Update `fetchMe`:

```js
async fetchMe() {
  if (!this.token) return null
  try {
    this.user = await api.get('/auth/me')
    localStorage.setItem('user', JSON.stringify(this.user))
    return this.user
  } catch (error) {
    this.token = ''
    this.user = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    return null
  }
}
```

- [ ] **Step 3: Add route metadata and forbidden route**

Update `frontend/src/router.js` routes to include:

```js
{ path: '/forbidden', component: () => import('./views/Forbidden.vue') },
{ path: '/cart', component: () => import('./views/Cart.vue'), meta: { auth: true, roles: ['USER', 'MERCHANT'] } },
{ path: '/orders', component: () => import('./views/Orders.vue'), meta: { auth: true, roles: ['USER', 'MERCHANT'] } },
{ path: '/merchant', component: () => import('./views/Merchant.vue'), meta: { auth: true, roles: ['USER', 'MERCHANT'] } },
{ path: '/admin', component: () => import('./views/Admin.vue'), meta: { auth: true, roles: ['ADMIN'] } },
{ path: '/dashboard', component: () => import('./views/Dashboard.vue'), meta: { auth: true, roles: ['ADMIN'] } }
```

Update the navigation guard:

```js
router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (auth.token && !auth.user) await auth.fetchMe()
  if (to.meta.auth && !auth.isLogin) return { path: '/login', query: { redirect: to.fullPath } }
  if (to.meta.roles && !to.meta.roles.includes(auth.role)) return '/forbidden'
})
```

- [ ] **Step 4: Create forbidden view**

Create `frontend/src/views/Forbidden.vue`:

```vue
<template>
  <div class="page">
    <PageState title="无权限访问" description="当前账号不能访问这个页面，请切换账号或返回商品页。">
      <el-button type="primary" @click="$router.push('/products')">返回商品页</el-button>
    </PageState>
  </div>
</template>

<script setup>
import PageState from '../components/PageState.vue'
</script>
```

- [ ] **Step 5: Make `App.vue` navigation role-aware**

Replace the static menu items in `frontend/src/App.vue` with a computed `menus` list:

```js
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ROLE_LABEL } from './constants/status'

const menus = computed(() => {
  if (auth.isAdmin) {
    return [
      { path: '/products', label: '商品' },
      { path: '/admin', label: '管理后台' },
      { path: '/dashboard', label: '经营看板' }
    ]
  }
  if (auth.isLogin) {
    return [
      { path: '/products', label: '商品' },
      { path: '/cart', label: '购物车' },
      { path: '/orders', label: '我的订单' },
      { path: '/merchant', label: auth.isMerchant ? '商家中心' : '商家入驻' }
    ]
  }
  return [
    { path: '/products', label: '商品' }
  ]
})

const roleLabel = computed(() => ROLE_LABEL[auth.role] || auth.role)
```

Use the menu:

```vue
<el-menu mode="horizontal" :ellipsis="false" :default-active="$route.path" router>
  <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">{{ item.label }}</el-menu-item>
</el-menu>
```

Show account role:

```vue
<span v-if="auth.user">{{ auth.user.nickname || auth.user.username }} · {{ roleLabel }}</span>
```

- [ ] **Step 6: Verify permissions manually**

Run frontend build:

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Expected:

- Guest can build and `/products` route remains public.
- `/cart`, `/orders`, `/merchant` redirect to `/login` when no token exists.
- Logged-in non-admin users cannot access `/admin` or `/dashboard`.

- [ ] **Step 7: Commit task**

```powershell
git add frontend/src/api.js frontend/src/stores/auth.js frontend/src/router.js frontend/src/App.vue frontend/src/views/Forbidden.vue
git commit -m "feat(frontend): add role-aware navigation"
```

---

### Task 3: Redesign Product Browsing

**Files:**
- Modify: `frontend/src/views/Products.vue`
- Modify: `frontend/src/styles.css`

- [ ] **Step 1: Add product page state and auth-aware add-to-cart**

Update `Products.vue` script to use:

```js
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import { formatMoney, productImage, textOrDash } from '../utils/format'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const products = ref([])
const keyword = ref('')
const quantities = reactive({})
const loading = ref(false)
const error = ref('')
const hasProducts = computed(() => products.value.length > 0)

async function load() {
  loading.value = true
  error.value = ''
  try {
    products.value = await api.get('/products', { params: { keyword: keyword.value || undefined } })
    products.value.forEach((item) => {
      quantities[item.id] = quantities[item.id] || 1
    })
  } catch (err) {
    error.value = err.message || '商品加载失败'
  } finally {
    loading.value = false
  }
}

async function addCart(item) {
  if (!auth.isLogin) {
    ElMessage.info('请先登录后再加入购物车')
    router.push({ path: '/login', query: { redirect: '/products' } })
    return
  }
  await api.post('/cart/items', { productId: item.id, quantity: quantities[item.id] || 1 })
  ElMessage.success('已加入购物车')
}

onMounted(load)
```

- [ ] **Step 2: Replace template with buyer-focused layout**

Use this structure in `Products.vue`:

```vue
<template>
  <div class="page">
    <PageHeader title="农产品集市" description="浏览已审核上架的农产品，选择数量后加入购物车。">
      <template #actions>
        <el-input v-model="keyword" clearable placeholder="搜索农产品" style="width: 280px" @keyup.enter="load" />
        <el-button type="primary" @click="load">查询</el-button>
      </template>
    </PageHeader>

    <PageState v-if="loading" type="loading" title="正在加载商品" description="请稍候" />
    <PageState v-else-if="error" type="error" title="商品加载失败" :description="error">
      <el-button @click="load">重试</el-button>
    </PageState>
    <PageState v-else-if="!hasProducts" title="暂无商品" description="换个关键词试试，或等待商家上架商品。" />

    <div v-else class="product-grid">
      <el-card v-for="item in products" :key="item.id" class="product-card">
        <img class="product-image" :src="productImage(item.productImage)" :alt="item.productName" />
        <div class="product-card__body">
          <h3>{{ item.productName }}</h3>
          <p class="muted">{{ textOrDash(item.originPlace) }} · 库存 {{ item.stock }} {{ item.unit }}</p>
          <p class="price">{{ formatMoney(item.price) }} / {{ item.unit }}</p>
          <div class="product-card__actions">
            <el-input-number v-model="quantities[item.id]" :min="1" :max="Math.max(item.stock || 0, 1)" :disabled="item.stock <= 0" />
            <el-button type="primary" :disabled="item.stock <= 0" @click="addCart(item)">
              {{ item.stock > 0 ? '加入购物车' : '暂无库存' }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>
```

- [ ] **Step 3: Add product grid CSS**

Append to `frontend/src/styles.css`:

```css
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.product-card {
  border-radius: 8px;
}

.product-card .el-card__body {
  padding: 0;
}

.product-card__body {
  padding: 14px;
}

.product-card h3 {
  margin: 0 0 8px;
  font-size: 17px;
}

.product-image {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
}

.product-card__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}
```

- [ ] **Step 4: Verify product page**

Run:

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Expected: Build succeeds and product page supports loading, empty, error, guest login guidance, and disabled out-of-stock purchase.

- [ ] **Step 5: Commit task**

```powershell
git add frontend/src/views/Products.vue frontend/src/styles.css
git commit -m "feat(frontend): improve product browsing"
```

---

### Task 4: Redesign Cart And Order Submission

**Files:**
- Modify: `frontend/src/views/Cart.vue`
- Modify: `frontend/src/styles.css`

- [ ] **Step 1: Add enriched cart state**

Update `Cart.vue` script:

```js
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import { formatMoney, productImage } from '../utils/format'

const router = useRouter()
const items = ref([])
const loading = ref(false)
const error = ref('')
const submitting = ref(false)
const orderForm = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })

const selectedItems = computed(() => items.value.filter((item) => item.selected === 1))
const selectedCount = computed(() => selectedItems.value.reduce((sum, item) => sum + Number(item.quantity || 0), 0))
const totalAmount = computed(() => selectedItems.value.reduce((sum, item) => sum + Number(item.subtotal || 0), 0))

async function enrich(item) {
  const product = await api.get(`/products/${item.productId}`).catch(() => null)
  const price = Number(product?.price || 0)
  return {
    ...item,
    product,
    unitPrice: price,
    subtotal: price * Number(item.quantity || 0)
  }
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const rows = await api.get('/cart/items')
    items.value = await Promise.all(rows.map(enrich))
  } catch (err) {
    error.value = err.message || '购物车加载失败'
  } finally {
    loading.value = false
  }
}

async function update(row) {
  await api.put(`/cart/items/${row.id}`, { quantity: row.quantity })
  await load()
}

async function select(row) {
  await api.put(`/cart/items/${row.id}/selected`, { selected: row.selected })
  await load()
}

async function remove(id) {
  await api.delete(`/cart/items/${id}`)
  await load()
}

async function clear() {
  await api.delete('/cart/items')
  await load()
}

async function submit() {
  if (!selectedItems.value.length) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  if (!orderForm.receiverName || !orderForm.receiverPhone || !orderForm.receiverAddress) {
    ElMessage.warning('请填写完整收货信息')
    return
  }
  submitting.value = true
  try {
    await api.post('/orders', orderForm)
    ElMessage.success('订单已创建')
    await load()
    router.push('/orders')
  } finally {
    submitting.value = false
  }
}

onMounted(load)
```

- [ ] **Step 2: Replace cart template**

Use:

```vue
<template>
  <div class="page">
    <PageHeader title="购物车" description="确认商品、数量和收货信息后提交订单。">
      <template #actions>
        <el-button type="danger" plain :disabled="!items.length" @click="clear">清空</el-button>
      </template>
    </PageHeader>

    <PageState v-if="loading" title="正在加载购物车" />
    <PageState v-else-if="error" type="error" title="购物车加载失败" :description="error">
      <el-button @click="load">重试</el-button>
    </PageState>
    <PageState v-else-if="!items.length" title="购物车为空" description="先去商品页挑选农产品。">
      <el-button type="primary" @click="$router.push('/products')">去选购</el-button>
    </PageState>

    <template v-else>
      <el-table :data="items">
        <el-table-column label="商品" min-width="260">
          <template #default="{ row }">
            <div class="table-product">
              <img :src="productImage(row.product?.productImage)" :alt="row.product?.productName || '商品'" />
              <div>
                <strong>{{ row.product?.productName || `商品 #${row.productId}` }}</strong>
                <p class="muted">库存 {{ row.product?.stock ?? '-' }} {{ row.product?.unit || '' }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">{{ formatMoney(row.unitPrice) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="170">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" :max="row.product?.stock || 999" @change="update(row)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }"><span class="price">{{ formatMoney(row.subtotal) }}</span></template>
        </el-table-column>
        <el-table-column label="选中" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.selected" :active-value="1" :inactive-value="0" @change="select(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="summary-bar">
        <div>已选 {{ selectedCount }} 件商品</div>
        <div>合计 <span class="price">{{ formatMoney(totalAmount) }}</span></div>
      </div>

      <el-card class="checkout-card">
        <template #header>收货信息</template>
        <el-form :model="orderForm" label-width="96px">
          <el-form-item label="收货人"><el-input v-model="orderForm.receiverName" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="orderForm.receiverPhone" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="orderForm.receiverAddress" /></el-form-item>
          <el-form-item label="备注"><el-input v-model="orderForm.remark" /></el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">提交订单</el-button>
        </el-form>
      </el-card>
    </template>
  </div>
</template>
```

- [ ] **Step 3: Add checkout CSS**

Append:

```css
.checkout-card {
  margin-top: 16px;
  border-radius: 8px;
}
```

- [ ] **Step 4: Verify cart flow**

Run build:

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Manual check:

- Add an approved product to cart.
- Cart shows product name, image, unit price, quantity, subtotal, selected count, and total.
- Submitting without receiver info shows warning.
- Submitting valid receiver info creates an order and routes to `/orders`.

- [ ] **Step 5: Commit task**

```powershell
git add frontend/src/views/Cart.vue frontend/src/styles.css
git commit -m "feat(frontend): make cart checkout readable"
```

---

### Task 5: Redesign Buyer Orders

**Files:**
- Modify: `frontend/src/views/Orders.vue`

- [ ] **Step 1: Add readable order state**

Update script:

```js
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import StatusTag from '../components/StatusTag.vue'
import { formatDateTime, formatMoney, textOrDash } from '../utils/format'

const orders = ref([])
const loading = ref(false)
const error = ref('')
const expanded = ref({})

const hasOrders = computed(() => orders.value.length > 0)

async function load() {
  loading.value = true
  error.value = ''
  try {
    orders.value = await api.get('/orders')
  } catch (err) {
    error.value = err.message || '订单加载失败'
  } finally {
    loading.value = false
  }
}

async function pay(orderId) {
  await api.post('/payments/mock-pay', { orderId })
  await load()
}

async function cancel(orderId) {
  await api.post(`/orders/${orderId}/cancel`, { reason: '用户取消' })
  await load()
}

async function receive(orderId) {
  await api.post(`/orders/${orderId}/receive`)
  await load()
}

async function toggleDetail(row) {
  if (expanded.value[row.id]) {
    expanded.value[row.id] = null
    return
  }
  expanded.value[row.id] = await api.get(`/orders/${row.id}`).catch(() => null)
}
```

- [ ] **Step 2: Replace orders template**

Use:

```vue
<template>
  <div class="page">
    <PageHeader title="我的订单" description="查看订单状态，并完成支付、取消或确认收货。">
      <template #actions><el-button @click="load">刷新</el-button></template>
    </PageHeader>

    <PageState v-if="loading" title="正在加载订单" />
    <PageState v-else-if="error" type="error" title="订单加载失败" :description="error">
      <el-button @click="load">重试</el-button>
    </PageState>
    <PageState v-else-if="!hasOrders" title="暂无订单" description="提交购物车后，订单会显示在这里。">
      <el-button type="primary" @click="$router.push('/products')">去选购</el-button>
    </PageState>

    <el-table v-else :data="orders">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column label="金额" width="120">
        <template #default="{ row }">{{ formatMoney(row.payAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }"><StatusTag kind="order" :value="row.orderStatus" /></template>
      </el-table-column>
      <el-table-column label="收货信息" min-width="240">
        <template #default="{ row }">
          <div>{{ textOrDash(row.receiverName) }} {{ textOrDash(row.receiverPhone) }}</div>
          <p class="muted">{{ textOrDash(row.receiverAddress) }}</p>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button v-if="row.orderStatus === 'PENDING_PAYMENT'" link type="primary" @click="pay(row.id)">支付</el-button>
          <el-button v-if="row.orderStatus === 'PENDING_PAYMENT'" link type="danger" @click="cancel(row.id)">取消</el-button>
          <el-button v-if="row.orderStatus === 'SHIPPED'" link type="success" @click="receive(row.id)">确认收货</el-button>
          <el-button link @click="toggleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
```

- [ ] **Step 3: Verify orders**

Run:

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Expected:

- Status labels are Chinese tags.
- Only valid actions are visible for each order status.
- Empty state and error state exist.

- [ ] **Step 4: Commit task**

```powershell
git add frontend/src/views/Orders.vue
git commit -m "feat(frontend): improve buyer orders"
```

---

### Task 6: Redesign Merchant Center

**Files:**
- Modify: `frontend/src/views/Merchant.vue`
- Modify: `frontend/src/styles.css`

- [ ] **Step 1: Add merchant state and category loading**

Update script:

```js
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import StatusTag from '../components/StatusTag.vue'
import { formatDateTime, formatMoney, textOrDash } from '../utils/format'

const activeTab = ref('apply')
const applyInfo = ref(null)
const categories = ref([])
const products = ref([])
const orders = ref([])
const loading = ref(false)
const applyForm = reactive({ merchantName: '', contactName: '', contactPhone: '', originAddress: '' })
const productForm = reactive({
  productName: '',
  price: 0,
  stock: 0,
  categoryId: null,
  unit: '斤',
  originPlace: '',
  productImage: '',
  description: ''
})

const approved = computed(() => applyInfo.value?.applyStatus === 'APPROVED')

async function load() {
  loading.value = true
  try {
    categories.value = await api.get('/product-categories').catch(() => [])
    applyInfo.value = await api.get('/merchant/apply/my').catch(() => null)
    products.value = await api.get('/merchant/products').catch(() => [])
    orders.value = await api.get('/merchant/orders').catch(() => [])
    if (!productForm.categoryId && categories.value.length) productForm.categoryId = categories.value[0].id
  } finally {
    loading.value = false
  }
}

async function apply() {
  await api.post('/merchant/apply', applyForm)
  ElMessage.success('已提交入驻申请')
  await load()
}

async function createProduct() {
  await api.post('/merchant/products', productForm)
  ElMessage.success('商品已提交审核')
  await load()
}

async function onSale(id) {
  await api.post(`/merchant/products/${id}/on-sale`)
  await load()
}

async function offSale(id) {
  await api.post(`/merchant/products/${id}/off-sale`)
  await load()
}

async function ship(id) {
  await api.post(`/merchant/orders/${id}/ship`)
  ElMessage.success('已发货')
  await load()
}

onMounted(load)
```

- [ ] **Step 2: Replace merchant template**

Use tabs with clear sections:

```vue
<template>
  <div class="page">
    <PageHeader title="商家中心" description="申请入驻、管理商品，并处理待发货订单。" />
    <PageState v-if="loading" title="正在加载商家信息" />

    <el-tabs v-else v-model="activeTab">
      <el-tab-pane label="入驻状态" name="apply">
        <el-card v-if="applyInfo">
          <template #header>申请状态</template>
          <p>店铺：{{ applyInfo.merchantName }}</p>
          <p>联系人：{{ applyInfo.contactName }} {{ applyInfo.contactPhone }}</p>
          <p>产地：{{ applyInfo.originAddress }}</p>
          <p>状态：<StatusTag kind="apply" :value="applyInfo.applyStatus" /></p>
          <p v-if="applyInfo.rejectReason" class="muted">拒绝原因：{{ applyInfo.rejectReason }}</p>
        </el-card>
        <el-card v-else>
          <template #header>提交入驻申请</template>
          <el-form :model="applyForm" label-width="96px">
            <el-form-item label="店铺名称"><el-input v-model="applyForm.merchantName" /></el-form-item>
            <el-form-item label="联系人"><el-input v-model="applyForm.contactName" /></el-form-item>
            <el-form-item label="联系电话"><el-input v-model="applyForm.contactPhone" /></el-form-item>
            <el-form-item label="产地地址"><el-input v-model="applyForm.originAddress" /></el-form-item>
            <el-button type="primary" @click="apply">提交申请</el-button>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="商品管理" name="products">
        <PageState v-if="!approved" title="商家审核通过后可管理商品" description="请先完成入驻申请并等待管理员审核。" />
        <template v-else>
          <el-card class="section-card">
            <template #header>发布商品</template>
            <el-form :model="productForm" label-width="88px">
              <el-form-item label="名称"><el-input v-model="productForm.productName" /></el-form-item>
              <el-form-item label="分类">
                <el-select v-model="productForm.categoryId" placeholder="选择分类">
                  <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="价格"><el-input-number v-model="productForm.price" :min="0" /></el-form-item>
              <el-form-item label="库存"><el-input-number v-model="productForm.stock" :min="0" /></el-form-item>
              <el-form-item label="单位"><el-input v-model="productForm.unit" /></el-form-item>
              <el-form-item label="产地"><el-input v-model="productForm.originPlace" /></el-form-item>
              <el-form-item label="图片"><el-input v-model="productForm.productImage" placeholder="图片 URL" /></el-form-item>
              <el-form-item label="描述"><el-input v-model="productForm.description" type="textarea" /></el-form-item>
              <el-button type="primary" @click="createProduct">提交审核</el-button>
            </el-form>
          </el-card>

          <el-table :data="products">
            <el-table-column prop="productName" label="商品" />
            <el-table-column label="价格"><template #default="{ row }">{{ formatMoney(row.price) }}</template></el-table-column>
            <el-table-column prop="stock" label="库存" />
            <el-table-column label="审核"><template #default="{ row }"><StatusTag kind="audit" :value="row.auditStatus" /></template></el-table-column>
            <el-table-column label="销售"><template #default="{ row }"><StatusTag kind="sale" :value="row.saleStatus" /></template></el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button v-if="row.auditStatus === 'APPROVED' && row.saleStatus !== 'ON_SALE'" link @click="onSale(row.id)">上架</el-button>
                <el-button v-if="row.saleStatus === 'ON_SALE'" link @click="offSale(row.id)">下架</el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-tab-pane>

      <el-tab-pane label="订单发货" name="orders">
        <el-table :data="orders">
          <el-table-column prop="orderNo" label="订单号" />
          <el-table-column label="金额"><template #default="{ row }">{{ formatMoney(row.payAmount) }}</template></el-table-column>
          <el-table-column label="收货人"><template #default="{ row }">{{ textOrDash(row.receiverName) }}</template></el-table-column>
          <el-table-column label="状态"><template #default="{ row }"><StatusTag kind="order" :value="row.orderStatus" /></template></el-table-column>
          <el-table-column label="创建时间"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button v-if="row.orderStatus === 'PENDING_SHIPMENT'" link type="primary" @click="ship(row.id)">发货</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
```

- [ ] **Step 3: Add section card CSS**

Append:

```css
.section-card {
  margin-bottom: 16px;
  border-radius: 8px;
}
```

- [ ] **Step 4: Verify merchant center**

Run build:

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Expected:

- Unapproved user sees application status/form.
- Product management is gated until application is approved.
- Product form has category, price, stock, unit, origin, image, and description.
- Shipment button only appears for `PENDING_SHIPMENT`.

- [ ] **Step 5: Commit task**

```powershell
git add frontend/src/views/Merchant.vue frontend/src/styles.css
git commit -m "feat(frontend): improve merchant center"
```

---

### Task 7: Redesign Admin And Dashboard

**Files:**
- Modify: `frontend/src/views/Admin.vue`
- Modify: `frontend/src/views/Dashboard.vue`

- [ ] **Step 1: Update admin script**

Use:

```js
import { onMounted, reactive, ref } from 'vue'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import StatusTag from '../components/StatusTag.vue'
import { formatDateTime, formatMoney, textOrDash } from '../utils/format'

const activeTab = ref('merchants')
const loading = ref(false)
const applications = ref([])
const products = ref([])
const categories = ref([])
const categoryForm = reactive({ categoryName: '', parentId: 0, sortOrder: 0, status: 'ENABLED' })

async function load() {
  loading.value = true
  try {
    applications.value = await api.get('/admin/merchant-applications').catch(() => [])
    products.value = await api.get('/admin/products/audit').catch(() => [])
    categories.value = await api.get('/product-categories').catch(() => [])
  } finally {
    loading.value = false
  }
}

async function approveApply(id) { await api.post(`/admin/merchant-applications/${id}/approve`); await load() }
async function rejectApply(id) { await api.post(`/admin/merchant-applications/${id}/reject`, { rejectReason: '资料不完整' }); await load() }
async function approveProduct(id) { await api.post(`/admin/products/${id}/approve`); await load() }
async function rejectProduct(id) { await api.post(`/admin/products/${id}/reject`); await load() }
async function createCategory() { await api.post('/admin/product-categories', categoryForm); await load() }

onMounted(load)
```

- [ ] **Step 2: Replace admin template**

Use:

```vue
<template>
  <div class="page">
    <PageHeader title="管理后台" description="审核商家、审核商品，并维护商品分类。">
      <template #actions><el-button @click="load">刷新</el-button></template>
    </PageHeader>

    <PageState v-if="loading" title="正在加载管理数据" />
    <el-tabs v-else v-model="activeTab">
      <el-tab-pane label="商家审核" name="merchants">
        <el-table :data="applications">
          <el-table-column prop="merchantName" label="店铺" />
          <el-table-column prop="contactName" label="联系人" />
          <el-table-column prop="contactPhone" label="联系电话" />
          <el-table-column label="产地"><template #default="{ row }">{{ textOrDash(row.originAddress) }}</template></el-table-column>
          <el-table-column label="状态"><template #default="{ row }"><StatusTag kind="apply" :value="row.applyStatus" /></template></el-table-column>
          <el-table-column label="申请时间"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button v-if="row.applyStatus === 'PENDING'" link type="success" @click="approveApply(row.id)">通过</el-button>
              <el-button v-if="row.applyStatus === 'PENDING'" link type="danger" @click="rejectApply(row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="商品审核" name="products">
        <el-table :data="products">
          <el-table-column prop="productName" label="商品" />
          <el-table-column label="价格"><template #default="{ row }">{{ formatMoney(row.price) }}</template></el-table-column>
          <el-table-column prop="stock" label="库存" />
          <el-table-column label="状态"><template #default="{ row }"><StatusTag kind="audit" :value="row.auditStatus" /></template></el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button v-if="row.auditStatus === 'PENDING'" link type="success" @click="approveProduct(row.id)">通过</el-button>
              <el-button v-if="row.auditStatus === 'PENDING'" link type="danger" @click="rejectProduct(row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="分类管理" name="categories">
        <el-card class="section-card">
          <el-form :model="categoryForm" inline>
            <el-form-item label="名称"><el-input v-model="categoryForm.categoryName" /></el-form-item>
            <el-form-item label="排序"><el-input-number v-model="categoryForm.sortOrder" :min="0" /></el-form-item>
            <el-button type="primary" @click="createCategory">新增</el-button>
          </el-form>
        </el-card>
        <el-table :data="categories">
          <el-table-column prop="categoryName" label="分类名称" />
          <el-table-column prop="sortOrder" label="排序" />
          <el-table-column prop="status" label="状态" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
```

- [ ] **Step 3: Improve dashboard states**

Update `Dashboard.vue` to import `PageHeader`, `PageState`, and `formatMoney`. Use loading/error state and render metrics as:

```vue
<PageHeader title="经营看板" description="查看平台订单、销售额和商品概览。">
  <template #actions><el-button @click="load">刷新</el-button></template>
</PageHeader>
<PageState v-if="loading" title="正在加载统计数据" />
<PageState v-else-if="error" type="error" title="统计数据加载失败" :description="error">
  <el-button @click="load">重试</el-button>
</PageState>
```

Keep the existing ECharts bar chart, but set `loading.value` and `error.value` around the `/admin/statistics/overview` call. Use `formatMoney(overview.salesAmount)` for sales amount.

- [ ] **Step 4: Verify admin and dashboard**

Run:

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Expected:

- Admin tables show Chinese status tags and state-aware buttons.
- Category management shows existing categories after creation.
- Dashboard is build-safe and has loading/error handling.

- [ ] **Step 5: Commit task**

```powershell
git add frontend/src/views/Admin.vue frontend/src/views/Dashboard.vue
git commit -m "feat(frontend): improve admin experience"
```

---

### Task 8: Final Verification And Acceptance Report

**Files:**
- Create: `reports/frontend-acceptance-2026-06-06.md`
- Modify: `memory-bank/progress.md`

- [ ] **Step 1: Run frontend build**

```powershell
docker run --rm -v "H:\COMputerSCIence\项目\agri-trade\frontend:/workspace" -w /workspace public.ecr.aws/docker/library/node:22-alpine npm run build
```

Expected: Build succeeds.

- [ ] **Step 2: Rebuild and start local stack**

```powershell
docker compose up -d --build backend frontend nginx
docker compose ps
```

Expected: backend, frontend, nginx, mysql, redis, and rabbitmq are running; MySQL is healthy.

- [ ] **Step 3: Verify direct routes**

Run:

```powershell
$routes = @('/products','/login','/cart','/orders','/merchant','/admin','/dashboard','/forbidden')
foreach ($route in $routes) {
  $response = Invoke-WebRequest -UseBasicParsing "http://localhost$route"
  "$route $($response.StatusCode)"
}
```

Expected: every route returns `200`.

- [ ] **Step 4: Verify buyer API-assisted flow**

Use existing seeded or registered buyer credentials. Through the UI when possible, otherwise use API calls to confirm:

- Login succeeds.
- Product list returns approved products.
- Product can be added to cart.
- Cart displays enriched product data.
- Order can be submitted.
- Order can be paid.
- Order can be canceled if still pending, or received after merchant shipment.

- [ ] **Step 5: Verify role boundaries**

Manual browser checks:

- Guest top nav shows only Products and Login.
- Buyer top nav does not show Admin or Dashboard.
- Buyer direct visit to `/admin` shows forbidden or redirects away.
- Admin top nav shows Admin and Dashboard.

- [ ] **Step 6: Verify merchant/admin flow**

Manual/API-assisted checks:

- Buyer submits merchant application.
- Admin approves merchant application.
- Merchant creates product.
- Admin approves product.
- Merchant can put approved product on sale.
- Buyer can see the product.
- Buyer order moves to paid state.
- Merchant sees paid order and ships it.
- Buyer confirms receipt.

- [ ] **Step 7: Write acceptance report**

Create `reports/frontend-acceptance-2026-06-06.md`:

```markdown
# Frontend Acceptance Report

Date: 2026-06-06

## Environment

- Entry URL: http://localhost
- Backend direct URL: http://localhost:8081
- MySQL: localhost:3307
- Redis: localhost:6380
- RabbitMQ console: http://localhost:15672

## Verification Commands

- Frontend build: PASS or FAIL with command output summary.
- Docker stack: PASS or FAIL with service status summary.
- Direct routes: PASS or FAIL with status codes.

## Buyer Flow

- Browse products:
- Add to cart:
- Update cart quantity:
- Submit order:
- Pay order:
- Confirm receipt:

## Merchant Flow

- Submit merchant application:
- View merchant status:
- Create product:
- Put approved product on sale:
- Ship paid order:

## Admin Flow

- Review merchant:
- Review product:
- Create category:
- View dashboard:

## Role And Permission Checks

- Guest navigation:
- Buyer navigation:
- Buyer blocked from admin:
- Admin navigation:

## Remaining Risks

- List any backend data gaps, manual-only checks, or browser automation gaps.
```

- [ ] **Step 8: Update progress**

Prepend `memory-bank/progress.md` with:

```markdown
## 2026-06-06 Frontend redesign

- Implemented portfolio-ready frontend redesign foundation and role-aware navigation.
- Improved buyer product, cart, and order flow.
- Improved merchant center and admin management pages.
- Verified frontend build and local acceptance flow.
- Acceptance report: `reports/frontend-acceptance-2026-06-06.md`.
```

- [ ] **Step 9: Commit verification artifacts**

```powershell
git add memory-bank/progress.md
git add -f reports/frontend-acceptance-2026-06-06.md
git commit -m "test(frontend): add acceptance report"
```

## Self-Review

- Spec coverage: global navigation, role guards, buyer flow, merchant flow, admin flow, dashboard, visual direction, and acceptance report are each covered by at least one task.
- Placeholder scan: no unfinished markers or undefined implementation steps remain.
- Type consistency: status maps use the same backend enum strings observed in current frontend and controllers; role checks use `auth.user.role`.
- Scope check: the plan is one coherent frontend redesign and can be executed in phase commits without unrelated framework migration.
