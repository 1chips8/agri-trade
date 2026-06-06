<template>
  <el-container class="app-shell">
    <el-header class="topbar">
      <div class="brand">农产品交易系统</div>
      <el-menu mode="horizontal" :ellipsis="false" :default-active="$route.path" router>
        <template v-for="item in menus" :key="item.path">
          <el-menu-item v-if="!item.badge" :index="item.path">{{ item.label }}</el-menu-item>
          <el-menu-item v-else :index="item.path">
            <el-badge :value="auth.unreadCount" :hidden="auth.unreadCount <= 0" class="menu-badge">
              {{ item.label }}
            </el-badge>
          </el-menu-item>
        </template>
      </el-menu>
      <div class="account">
        <span v-if="auth.user">{{ auth.user.nickname || auth.user.username }} · {{ roleLabel }}</span>
        <el-button v-if="auth.isLogin" link @click="logout">退出</el-button>
        <el-button v-else link @click="$router.push('/login')">登录</el-button>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { ROLE_LABEL } from './constants/status'

const router = useRouter()
const auth = useAuthStore()

onMounted(() => {
  if (auth.isLogin) auth.fetchUnreadCount()
})

const menus = computed(() => {
  if (auth.isAdmin) {
    return [
      { path: '/products', label: '商品' },
      { path: '/admin', label: '管理后台' },
      { path: '/dashboard', label: '经营看板' }
    ]
  }
  if (auth.isLogin) {
    return [
      { path: '/products', label: '商品' },
      { path: '/cart', label: '购物车' },
      { path: '/orders', label: '我的订单' },
      { path: '/messages', label: '消息', badge: true },
      { path: '/merchant', label: auth.isMerchant ? '商家中心' : '商家入驻' }
    ]
  }
  return [
    { path: '/products', label: '商品' }
  ]
})

const roleLabel = computed(() => ROLE_LABEL[auth.role] || auth.role)

async function logout() {
  await auth.logout()
  router.push('/login')
}
</script>
