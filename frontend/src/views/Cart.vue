<template>
  <div class="page">
    <div class="toolbar">
      <h2>购物车</h2>
      <el-button type="danger" plain @click="clear">清空</el-button>
    </div>
    <el-table :data="items">
      <el-table-column prop="productId" label="商品ID" />
      <el-table-column prop="merchantId" label="商家ID" />
      <el-table-column label="数量">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" @change="update(row)" />
        </template>
      </el-table-column>
      <el-table-column label="选中">
        <template #default="{ row }">
          <el-switch v-model="row.selected" :active-value="1" :inactive-value="0" @change="select(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }"><el-button link type="danger" @click="remove(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-divider />
    <el-form :model="orderForm" label-width="96px">
      <el-form-item label="收货人"><el-input v-model="orderForm.receiverName" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="orderForm.receiverPhone" /></el-form-item>
      <el-form-item label="地址"><el-input v-model="orderForm.receiverAddress" /></el-form-item>
      <el-button type="primary" @click="submit">提交订单</el-button>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index'
import api from '../api'

const items = ref([])
const orderForm = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '', remark: '' })

async function load() { items.value = await api.get('/cart/items') }
async function update(row) { await api.put(`/cart/items/${row.id}`, { quantity: row.quantity }) }
async function select(row) { await api.put(`/cart/items/${row.id}/selected`, { selected: row.selected }) }
async function remove(id) { await api.delete(`/cart/items/${id}`); await load() }
async function clear() { await api.delete('/cart/items'); await load() }
async function submit() {
  await api.post('/orders', orderForm)
  ElMessage.success('订单已创建')
  await load()
}

onMounted(load)
</script>
