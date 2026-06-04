<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" clearable placeholder="搜索农产品" style="max-width: 320px" @keyup.enter="load" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <div class="grid">
      <el-card v-for="item in products" :key="item.id">
        <img class="product-image" :src="item.productImage || fallback" :alt="item.productName" />
        <h3>{{ item.productName }}</h3>
        <p class="muted">{{ item.originPlace }} · 库存 {{ item.stock }} {{ item.unit }}</p>
        <p class="price">￥{{ item.price }}</p>
        <el-input-number v-model="quantities[item.id]" :min="1" :max="item.stock" />
        <el-button type="primary" style="margin-left: 8px" @click="addCart(item)">加入购物车</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const products = ref([])
const keyword = ref('')
const quantities = reactive({})
const fallback = 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80'

async function load() {
  products.value = await api.get('/products', { params: { keyword: keyword.value || undefined } })
  products.value.forEach((item) => {
    quantities[item.id] = quantities[item.id] || 1
  })
}

async function addCart(item) {
  await api.post('/cart/items', { productId: item.id, quantity: quantities[item.id] || 1 })
  ElMessage.success('已加入购物车')
}

onMounted(load)
</script>
