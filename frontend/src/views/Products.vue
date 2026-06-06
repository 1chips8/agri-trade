<template>
  <div class="page">
    <PageHeader title="农产品集市" description="浏览已审核上架的农产品，选择数量后加入购物车。">
      <template #actions>
        <el-input v-model="keyword" clearable placeholder="搜索农产品" style="width: 280px" @keyup.enter="load" />
        <el-button type="primary" @click="load">查询</el-button>
      </template>
    </PageHeader>

    <PageState v-if="loading" title="正在加载商品" description="请稍候" />
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

<script setup>
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
</script>
