<template>
  <div class="page auth-page">
    <el-card class="auth-card">
      <el-tabs v-model="mode">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" label-width="72px">
            <el-form-item label="用户名"><el-input v-model="loginForm.username" /></el-form-item>
            <el-form-item label="密码"><el-input v-model="loginForm.password" type="password" show-password /></el-form-item>
            <el-button type="primary" @click="login">登录</el-button>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" label-width="72px">
            <el-form-item label="用户名"><el-input v-model="registerForm.username" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="registerForm.phone" /></el-form-item>
            <el-form-item label="昵称"><el-input v-model="registerForm.nickname" /></el-form-item>
            <el-form-item label="密码"><el-input v-model="registerForm.password" type="password" show-password /></el-form-item>
            <el-button type="primary" @click="register">注册</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const mode = ref('login')
const loginForm = reactive({ username: 'admin', password: 'admin123' })
const registerForm = reactive({ username: '', phone: '', nickname: '', password: '' })

async function login() {
  await auth.login(loginForm)
  router.push('/products')
}

async function register() {
  await auth.register(registerForm)
  ElMessage.success('注册成功')
  mode.value = 'login'
}
</script>

<style scoped>
.auth-page {
  display: grid;
  place-items: center;
  min-height: calc(100vh - 120px);
}
.auth-card {
  width: min(420px, 100%);
  border-radius: 8px;
}
</style>
