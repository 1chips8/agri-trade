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

<script setup>
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
</script>
