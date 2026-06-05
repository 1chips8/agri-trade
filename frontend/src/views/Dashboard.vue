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
import { nextTick, onBeforeUnmount, onMounted, ref, shallowRef } from 'vue'
import { BarChart } from 'echarts/charts'
import { GridComponent } from 'echarts/components'
import { init, use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import api from '../api'

use([BarChart, GridComponent, CanvasRenderer])

const overview = ref({})
const chartRef = ref(null)
const chart = shallowRef(null)

async function load() {
  overview.value = await api.get('/admin/statistics/overview').catch(() => ({}))
  await nextTick()
  if (!chart.value) chart.value = init(chartRef.value)
  chart.value.setOption({
    xAxis: { type: 'category', data: ['订单', '商品'] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: [overview.value.orderCount || 0, overview.value.productCount || 0] }]
  })
}

onMounted(load)
onBeforeUnmount(() => chart.value?.dispose())
</script>
