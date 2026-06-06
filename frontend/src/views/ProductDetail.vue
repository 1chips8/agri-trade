<template>
  <div class="page">
    <PageState v-if="loading" title="正在加载商品详情" />
    <PageState v-else-if="error" type="error" title="商品加载失败" :description="error">
      <el-button @click="load">重试</el-button>
    </PageState>

    <template v-else-if="product">
      <el-button link style="margin-bottom: 12px" @click="$router.push('/products')">← 返回商品列表</el-button>
      <div class="product-detail">
        <div class="product-detail__image">
          <img :src="productImage(product.productImage)" :alt="product.productName" />
        </div>
        <div class="product-detail__info">
          <h1>{{ product.productName }}</h1>
          <p class="price">{{ formatMoney(product.price) }} / {{ product.unit }}</p>
          <div class="product-detail__meta">
            <p><span class="label">分类</span>{{ categoryName }}</p>
            <p><span class="label">产地</span>{{ textOrDash(product.originPlace) }}</p>
            <p><span class="label">库存</span>{{ product.stock }} {{ product.unit }}</p>
            <p v-if="product.shelfLife"><span class="label">保质期</span>{{ product.shelfLife }}</p>
          </div>
          <p v-if="product.description" class="product-detail__desc">{{ product.description }}</p>
          <div class="product-detail__actions">
            <el-input-number v-model="quantity" :min="1" :max="Math.max(product.stock || 0, 1)" :disabled="product.stock <= 0" />
            <el-button type="primary" size="large" :disabled="product.stock <= 0" @click="addCart">
              {{ product.stock > 0 ? '加入购物车' : '暂无库存' }}
            </el-button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus/es/components/message/index'
import api from '../api'
import PageState from '../components/PageState.vue'
import { formatMoney, productImage, textOrDash } from '../utils/format'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const product = ref(null)
const categories = ref([])
const quantity = ref(1)
const loading = ref(false)
const error = ref('')

const categoryName = computed(() => {
  if (!product.value) return '-'
  const cat = categories.value.find((c) => c.id === product.value.categoryId)
  return cat ? cat.categoryName : `分类 #${product.value.categoryId}`
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [productData, categoriesData] = await Promise.all([
      api.get(`/products/${route.params.id}`),
      api.get('/product-categories').catch(() => [])
    ])
    product.value = productData
    categories.value = categoriesData
    quantity.value = 1
  } catch (err) {
    error.value = err.message || '商品加载失败'
  } finally {
    loading.value = false
  }
}

async function addCart() {
  if (!auth.isLogin) {
    ElMessage.info('请先登录后再加入购物车')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  await api.post('/cart/items', { productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

onMounted(load)
</script>

<style scoped>
.product-detail {
  display: flex;
  gap: 32px;
  background: #fff;
  border: 1px solid #e4e7ec;
  border-radius: 8px;
  padding: 24px;
}
.product-detail__image {
  flex: 0 0 360px;
}
.product-detail__image img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: 8px;
  background: #e5e7eb;
}
.product-detail__info {
  flex: 1;
  min-width: 0;
}
.product-detail__info h1 {
  margin: 0 0 12px;
  font-size: 24px;
}
.product-detail__meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 24px;
  margin: 16px 0;
  padding: 16px;
  background: #f9fafb;
  border-radius: 6px;
}
.product-detail__meta p {
  margin: 0;
  font-size: 14px;
}
.product-detail__meta .label {
  color: #6b7280;
  display: inline-block;
  width: 48px;
}
.product-detail__desc {
  margin: 16px 0;
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
}
.product-detail__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}
</style>
