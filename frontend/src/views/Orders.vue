<template>
  <div class="page">
    <div class="toolbar"><h2>我的订单</h2><el-button @click="load">刷新</el-button></div>
    <el-table :data="orders">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="payAmount" label="金额" />
      <el-table-column prop="orderStatus" label="状态" />
      <el-table-column prop="createTime" label="创建时间" min-width="180" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.orderStatus === 'PENDING_PAYMENT'" link type="primary" @click="pay(row.id)">支付</el-button>
          <el-button v-if="row.orderStatus === 'PENDING_PAYMENT'" link type="danger" @click="cancel(row.id)">取消</el-button>
          <el-button v-if="row.orderStatus === 'SHIPPED'" link type="success" @click="receive(row.id)">收货</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'

const orders = ref([])
async function load() { orders.value = await api.get('/orders') }
async function pay(orderId) { await api.post('/payments/mock-pay', { orderId }); await load() }
async function cancel(orderId) { await api.post(`/orders/${orderId}/cancel`, { reason: '用户取消' }); await load() }
async function receive(orderId) { await api.post(`/orders/${orderId}/receive`); await load() }
onMounted(load)
</script>
