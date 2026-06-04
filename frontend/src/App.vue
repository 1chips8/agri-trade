<template>
  <el-container class="app-shell">
    <el-header class="topbar">
      <div class="brand">农产品交易系统</div>
      <el-menu mode="horizontal" :ellipsis="false" :default-active="$route.path" router>
        <el-menu-item index="/products">商品</el-menu-item>
        <el-menu-item index="/cart">购物车</el-menu-item>
        <el-menu-item index="/orders">订单</el-menu-item>
        <el-menu-item index="/merchant">商家</el-menu-item>
        <el-menu-item index="/admin">管理</el-menu-item>
        <el-menu-item index="/dashboard">看板</el-menu-item>
      </el-menu>
      <div class="account">
        <span v-if="auth.user">{{ auth.user.nickname || auth.user.username }}</span>
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
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const auth = useAuthStore()

async function logout() {
  await auth.logout()
  router.push('/login')
}
</script>
