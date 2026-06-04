<template>
  <div class="page">
    <div class="toolbar"><h2>经营看板</h2><el-button @click="load">刷新</el-button></div>
    <el-row :gutter="16">
      <el-col :span="8"><el-card><p class="muted">订单数</p><h2>{{ overview.orderCount || 0 }}</h2></el-card></el-col>
      <el-col :span="8"><el-card><p class="muted">销售额</p><h2>{{ overview.salesAmount || 0 }}</h2></el-card></el-col>
      <el-col :span="8"><el-card><p class="muted">商品数</p><h2>{{ overview.productCount || 0 }}</h2></el-card></el-col>
    </el-row>
    <el-card style="margin-top: 16px">
      <div ref="chartRef" style="height: 360px"></div>
    </el-card>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import api from '../api'

const overview = ref({})
const chartRef = ref(null)

async function load() {
  overview.value = await api.get('/admin/statistics/overview').catch(() => ({}))
  await nextTick()
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    xAxis: { type: 'category', data: ['订单', '商品'] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: [overview.value.orderCount || 0, overview.value.productCount || 0] }]
  })
}

onMounted(load)
</script>
