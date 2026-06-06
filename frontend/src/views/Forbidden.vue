<template>
  <div class="page">
    <PageState title="无权限访问" description="当前账号不能访问这个页面，请切换账号或返回商品页。">
      <el-button type="primary" @click="$router.push('/products')">返回商品页</el-button>
    </PageState>
  </div>
</template>

<script setup>
import PageState from '../components/PageState.vue'
</script>
