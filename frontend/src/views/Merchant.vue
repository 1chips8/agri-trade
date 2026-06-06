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
            <el-table-column label="销量"><template #default="{ row }">{{ row.salesCount ?? 0 }}</template></el-table-column>
            <el-table-column label="审核"><template #default="{ row }"><StatusTag kind="audit" :value="row.auditStatus" /></template></el-table-column>
            <el-table-column label="销售"><template #default="{ row }"><StatusTag kind="sale" :value="row.saleStatus" /></template></el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button link @click="editProduct(row)">编辑</el-button>
                <el-button v-if="row.auditStatus === 'APPROVED' && row.saleStatus !== 'ON_SALE'" link @click="onSale(row.id)">上架</el-button>
                <el-button v-if="row.saleStatus === 'ON_SALE'" link @click="offSale(row.id)">下架</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-dialog v-model="editDialogVisible" title="编辑商品" width="540px">
            <el-form :model="editForm" label-width="88px">
              <el-form-item label="名称"><el-input v-model="editForm.productName" /></el-form-item>
              <el-form-item label="分类">
                <el-select v-model="editForm.categoryId" placeholder="选择分类">
                  <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="价格"><el-input-number v-model="editForm.price" :min="0" style="width:100%" /></el-form-item>
              <el-form-item label="库存"><el-input-number v-model="editForm.stock" :min="0" style="width:100%" /></el-form-item>
              <el-form-item label="单位"><el-input v-model="editForm.unit" /></el-form-item>
              <el-form-item label="产地"><el-input v-model="editForm.originPlace" /></el-form-item>
              <el-form-item label="图片"><el-input v-model="editForm.productImage" placeholder="图片 URL" /></el-form-item>
              <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
            </el-form>
            <template #footer>
              <el-button @click="editDialogVisible = false">取消</el-button>
              <el-button type="primary" :loading="editSubmitting" @click="updateProduct">保存</el-button>
            </template>
          </el-dialog>
        </template>
      </el-tab-pane>

      <el-tab-pane label="订单发货" name="orders">
        <el-table :data="orders">
          <el-table-column prop="orderNo" label="订单号" min-width="180" />
          <el-table-column label="金额"><template #default="{ row }">{{ formatMoney(row.payAmount) }}</template></el-table-column>
          <el-table-column label="状态"><template #default="{ row }"><StatusTag kind="order" :value="row.orderStatus" /></template></el-table-column>
          <el-table-column label="收货人"><template #default="{ row }">{{ textOrDash(row.receiverName) }}</template></el-table-column>
          <el-table-column label="电话" width="130"><template #default="{ row }">{{ textOrDash(row.receiverPhone) }}</template></el-table-column>
          <el-table-column label="地址" min-width="180"><template #default="{ row }">{{ textOrDash(row.receiverAddress) }}</template></el-table-column>
          <el-table-column label="创建时间" min-width="170"><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button v-if="row.orderStatus === 'WAIT_SHIPMENT'" link type="primary" @click="ship(row.id)">发货</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
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

const editDialogVisible = ref(false)
const editSubmitting = ref(false)
const editForm = reactive({
  productName: '',
  price: 0,
  stock: 0,
  categoryId: null,
  unit: '',
  originPlace: '',
  productImage: '',
  description: ''
})

function editProduct(product) {
  editForm.productName = product.productName
  editForm.price = product.price
  editForm.stock = product.stock
  editForm.categoryId = product.categoryId
  editForm.unit = product.unit
  editForm.originPlace = product.originPlace || ''
  editForm.productImage = product.productImage || ''
  editForm.description = product.description || ''
  editForm.id = product.id
  editDialogVisible.value = true
}

async function updateProduct() {
  editSubmitting.value = true
  try {
    await api.put(`/merchant/products/${editForm.id}`, editForm)
    ElMessage.success('商品已更新，需重新审核')
    editDialogVisible.value = false
    await load()
  } finally {
    editSubmitting.value = false
  }
}

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
</script>
