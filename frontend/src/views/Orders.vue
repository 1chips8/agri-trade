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

    <el-table v-else :data="orders" @expand-change="handleExpand">
      <el-table-column type="expand">
        <template #default="{ row }">
          <div v-if="detailLoading[row.id]" class="expand-loading">加载中…</div>
          <template v-else-if="detailData[row.id]">
            <div class="order-detail-header">
              <span>订单号：{{ detailData[row.id].order.orderNo }}</span>
              <span>创建时间：{{ formatDateTime(detailData[row.id].order.createTime) }}</span>
              <span v-if="detailData[row.id].order.payTime">支付时间：{{ formatDateTime(detailData[row.id].order.payTime) }}</span>
              <span v-if="detailData[row.id].order.shipTime">发货时间：{{ formatDateTime(detailData[row.id].order.shipTime) }}</span>
              <span v-if="detailData[row.id].order.completeTime">完成时间：{{ formatDateTime(detailData[row.id].order.completeTime) }}</span>
              <span v-if="detailData[row.id].order.cancelReason">取消原因：{{ detailData[row.id].order.cancelReason }}</span>
              <span v-if="detailData[row.id].order.remark">备注：{{ detailData[row.id].order.remark }}</span>
            </div>
            <el-table :data="detailData[row.id].items" size="small" style="width: 100%">
              <el-table-column label="商品" min-width="200">
                <template #default="{ row: item }">
                  <div class="table-product">
                    <img :src="productImage(item.productImage)" :alt="item.productName" />
                    <span>{{ item.productName }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="单价" width="120">
                <template #default="{ row: item }">{{ formatMoney(item.productPrice) }}</template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="80" />
              <el-table-column label="小计" width="120">
                <template #default="{ row: item }">{{ formatMoney(item.totalAmount) }}</template>
              </el-table-column>
            </el-table>
          </template>
        </template>
      </el-table-column>
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
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.orderStatus === 'PENDING_PAYMENT'" link type="primary" @click="pay(row.id)">支付</el-button>
          <el-button v-if="row.orderStatus === 'PENDING_PAYMENT'" link type="danger" @click="cancel(row.id)">取消</el-button>
          <el-button v-if="row.orderStatus === 'SHIPPED'" link type="success" @click="receive(row.id)">确认收货</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import StatusTag from '../components/StatusTag.vue'
import { formatDateTime, formatMoney, productImage, textOrDash } from '../utils/format'

const orders = ref([])
const loading = ref(false)
const error = ref('')
const detailData = reactive({})
const detailLoading = reactive({})

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

async function handleExpand(row, expandedRows) {
  if (!expandedRows.includes(row)) return
  if (detailData[row.id]) return
  detailLoading[row.id] = true
  detailData[row.id] = await api.get(`/orders/${row.id}`).catch(() => null)
  detailLoading[row.id] = false
}

onMounted(load)
</script>

<style scoped>
.order-detail-header {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 24px;
  padding: 8px 12px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #6b7280;
  background: #f9fafb;
  border-radius: 6px;
}
.expand-loading {
  padding: 24px;
  text-align: center;
  color: #9ca3af;
}
</style>
