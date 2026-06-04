<template>
  <div class="page">
    <el-tabs>
      <el-tab-pane label="商家审核">
        <el-table :data="applications">
          <el-table-column prop="merchantName" label="店铺" />
          <el-table-column prop="applyStatus" label="状态" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button link type="success" @click="approveApply(row.id)">通过</el-button>
              <el-button link type="danger" @click="rejectApply(row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="商品审核">
        <el-table :data="products">
          <el-table-column prop="productName" label="商品" />
          <el-table-column prop="price" label="价格" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button link type="success" @click="approveProduct(row.id)">通过</el-button>
              <el-button link type="danger" @click="rejectProduct(row.id)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="分类">
        <el-form :model="categoryForm" inline>
          <el-form-item label="名称"><el-input v-model="categoryForm.categoryName" /></el-form-item>
          <el-button type="primary" @click="createCategory">新增</el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../api'

const applications = ref([])
const products = ref([])
const categoryForm = reactive({ categoryName: '', parentId: 0, sortOrder: 0, status: 'ENABLED' })

async function load() {
  applications.value = await api.get('/admin/merchant-applications').catch(() => [])
  products.value = await api.get('/admin/products/audit').catch(() => [])
}
async function approveApply(id) { await api.post(`/admin/merchant-applications/${id}/approve`); await load() }
async function rejectApply(id) { await api.post(`/admin/merchant-applications/${id}/reject`, { rejectReason: '资料不完整' }); await load() }
async function approveProduct(id) { await api.post(`/admin/products/${id}/approve`); await load() }
async function rejectProduct(id) { await api.post(`/admin/products/${id}/reject`); await load() }
async function createCategory() { await api.post('/admin/product-categories', categoryForm) }

onMounted(load)
</script>
