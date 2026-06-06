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

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index'
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
async function createCategory() { await api.post('/admin/product-categories', categoryForm); ElMessage.success('分类已创建'); await load() }

onMounted(load)
</script>
