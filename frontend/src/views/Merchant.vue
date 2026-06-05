<template>
  <div class="page">
    <el-tabs>
      <el-tab-pane label="入驻申请">
        <el-form :model="applyForm" label-width="96px">
          <el-form-item label="店铺名称"><el-input v-model="applyForm.merchantName" /></el-form-item>
          <el-form-item label="联系人"><el-input v-model="applyForm.contactName" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="applyForm.contactPhone" /></el-form-item>
          <el-form-item label="产地地址"><el-input v-model="applyForm.originAddress" /></el-form-item>
          <el-button type="primary" @click="apply">提交申请</el-button>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="商品管理">
        <el-form :model="productForm" inline>
          <el-form-item label="名称"><el-input v-model="productForm.productName" /></el-form-item>
          <el-form-item label="价格"><el-input-number v-model="productForm.price" :min="0" /></el-form-item>
          <el-form-item label="库存"><el-input-number v-model="productForm.stock" :min="0" /></el-form-item>
          <el-button type="primary" @click="createProduct">发布</el-button>
        </el-form>
        <el-table :data="products">
          <el-table-column prop="productName" label="商品" />
          <el-table-column prop="auditStatus" label="审核" />
          <el-table-column prop="saleStatus" label="销售" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button link @click="onSale(row.id)">上架</el-button>
              <el-button link @click="offSale(row.id)">下架</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="订单发货">
        <el-table :data="orders">
          <el-table-column prop="orderNo" label="订单号" />
          <el-table-column prop="orderStatus" label="状态" />
          <el-table-column label="操作"><template #default="{ row }"><el-button link @click="ship(row.id)">发货</el-button></template></el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index'
import api from '../api'

const products = ref([])
const orders = ref([])
const applyForm = reactive({ merchantName: '', contactName: '', contactPhone: '', originAddress: '' })
const productForm = reactive({ productName: '', price: 0, stock: 0, categoryId: 1, unit: '斤', originPlace: '', description: '' })

async function apply() { await api.post('/merchant/apply', applyForm); ElMessage.success('已提交') }
async function loadProducts() { products.value = await api.get('/merchant/products').catch(() => []) }
async function loadOrders() { orders.value = await api.get('/merchant/orders').catch(() => []) }
async function createProduct() { await api.post('/merchant/products', productForm); await loadProducts() }
async function onSale(id) { await api.post(`/merchant/products/${id}/on-sale`); await loadProducts() }
async function offSale(id) { await api.post(`/merchant/products/${id}/off-sale`); await loadProducts() }
async function ship(id) { await api.post(`/merchant/orders/${id}/ship`); await loadOrders() }
onMounted(() => { loadProducts(); loadOrders() })
</script>
