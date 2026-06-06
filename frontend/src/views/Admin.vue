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
        <p style="font-size:13px;color:#909399;margin-bottom:8px">注意：禁用分类后，因公开查询接口只返回启用分类，禁用项将从当前列表中消失。</p>
        <el-table :data="categories">
          <el-table-column prop="categoryName" label="分类名称" />
          <el-table-column prop="sortOrder" label="排序" />
          <el-table-column label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'" size="small">
                {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button link @click="editCategory(row)">编辑</el-button>
              <el-button v-if="row.status === 'ENABLED'" link type="danger" @click="toggleCategoryStatus(row)">禁用</el-button>
              <el-button v-else link type="success" @click="toggleCategoryStatus(row)">启用</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog v-model="editCategoryDialogVisible" title="编辑分类" width="420px">
          <el-form :model="editCategoryForm" label-width="72px">
            <el-form-item label="名称"><el-input v-model="editCategoryForm.categoryName" /></el-form-item>
            <el-form-item label="排序"><el-input-number v-model="editCategoryForm.sortOrder" :min="0" style="width:100%" /></el-form-item>
            <el-form-item label="状态">
              <el-select v-model="editCategoryForm.status">
                <el-option label="启用" value="ENABLED" />
                <el-option label="禁用" value="DISABLED" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="editCategoryDialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="editCategorySubmitting" @click="updateCategory">保存</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <el-tab-pane label="订单管理" name="orders">
        <div style="margin-bottom: 12px">
          <el-select v-model="orderStatusFilter" clearable placeholder="全部状态" style="width: 160px">
            <el-option v-for="(meta, key) in ORDER_STATUS" :key="key" :label="meta.label" :value="key" />
          </el-select>
        </div>
        <el-table :data="filteredOrders">
          <el-table-column prop="orderNo" label="订单号" min-width="180" />
          <el-table-column label="金额" width="120">
            <template #default="{ row }">{{ formatMoney(row.payAmount) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }"><StatusTag kind="order" :value="row.orderStatus" /></template>
          </el-table-column>
          <el-table-column label="收货人" width="120">
            <template #default="{ row }">{{ textOrDash(row.receiverName) }}</template>
          </el-table-column>
          <el-table-column label="电话" width="140">
            <template #default="{ row }">{{ textOrDash(row.receiverPhone) }}</template>
          </el-table-column>
          <el-table-column label="地址" min-width="200">
            <template #default="{ row }">{{ textOrDash(row.receiverAddress) }}</template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="170">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index'
import { ElMessageBox } from 'element-plus/es/components/message-box/index'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import StatusTag from '../components/StatusTag.vue'
import { ORDER_STATUS } from '../constants/status'
import { formatDateTime, formatMoney, textOrDash } from '../utils/format'

const activeTab = ref('merchants')
const loading = ref(false)
const applications = ref([])
const products = ref([])
const categories = ref([])
const orders = ref([])
const orderStatusFilter = ref('')
const categoryForm = reactive({ categoryName: '', parentId: 0, sortOrder: 0, status: 'ENABLED' })

const editCategoryDialogVisible = ref(false)
const editCategorySubmitting = ref(false)
const editCategoryForm = reactive({ categoryName: '', sortOrder: 0, status: 'ENABLED', id: null })

function editCategory(category) {
  editCategoryForm.id = category.id
  editCategoryForm.categoryName = category.categoryName
  editCategoryForm.sortOrder = category.sortOrder
  editCategoryForm.status = category.status
  editCategoryDialogVisible.value = true
}

async function updateCategory() {
  editCategorySubmitting.value = true
  try {
    await api.put(`/admin/product-categories/${editCategoryForm.id}`, {
      categoryName: editCategoryForm.categoryName,
      sortOrder: editCategoryForm.sortOrder,
      status: editCategoryForm.status
    })
    ElMessage.success('分类已更新')
    editCategoryDialogVisible.value = false
    await load()
  } finally {
    editCategorySubmitting.value = false
  }
}

async function toggleCategoryStatus(category) {
  const nextStatus = category.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
  await api.post(`/admin/product-categories/${category.id}/status?status=${nextStatus}`)
  ElMessage.success(nextStatus === 'ENABLED' ? '分类已启用' : '分类已禁用')
  await load()
}

const filteredOrders = computed(() => {
  if (!orderStatusFilter.value) return orders.value
  return orders.value.filter((o) => o.orderStatus === orderStatusFilter.value)
})

async function load() {
  loading.value = true
  try {
    applications.value = await api.get('/admin/merchant-applications').catch(() => [])
    products.value = await api.get('/admin/products/audit').catch(() => [])
    categories.value = await api.get('/product-categories').catch(() => [])
    orders.value = await api.get('/admin/orders').catch(() => [])
  } finally {
    loading.value = false
  }
}

async function approveApply(id) { await api.post(`/admin/merchant-applications/${id}/approve`); await load() }
async function rejectApply(id) {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝商家申请', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '拒绝原因'
    })
    await api.post(`/admin/merchant-applications/${id}/reject`, { rejectReason: value })
    ElMessage.success('已拒绝')
    await load()
  } catch { /* cancelled */ }
}
async function approveProduct(id) { await api.post(`/admin/products/${id}/approve`); await load() }
async function rejectProduct(id) {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝商品', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '拒绝原因（后端暂不持久化，仅确认操作）'
    })
    await api.post(`/admin/products/${id}/reject`)
    ElMessage.success('已拒绝')
    await load()
  } catch { /* cancelled */ }
}
async function createCategory() { await api.post('/admin/product-categories', categoryForm); ElMessage.success('分类已创建'); await load() }

onMounted(load)
</script>
