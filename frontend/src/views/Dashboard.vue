<template>
  <div class="page">
    <PageHeader title="经营看板" description="查看平台订单、销售额和商品概览。">
      <template #actions><el-button @click="load">刷新</el-button></template>
    </PageHeader>

    <PageState v-if="loading" title="正在加载统计数据" />
    <PageState v-else-if="error" type="error" title="统计数据加载失败" :description="error">
      <el-button @click="load">重试</el-button>
    </PageState>

    <template v-else>
      <el-row :gutter="16">
        <el-col :span="8"><el-card><p class="muted">订单数</p><h2>{{ overview.orderCount || 0 }}</h2></el-card></el-col>
        <el-col :span="8"><el-card><p class="muted">销售额</p><h2>{{ formatMoney(overview.salesAmount) }}</h2></el-card></el-col>
        <el-col :span="8"><el-card><p class="muted">商品数</p><h2>{{ overview.productCount || 0 }}</h2></el-card></el-col>
      </el-row>
      <el-card style="margin-top: 16px">
        <div ref="chartRef" style="height: 360px"></div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, shallowRef } from 'vue'
import { BarChart } from 'echarts/charts'
import { GridComponent } from 'echarts/components'
import { init, use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import { formatMoney } from '../utils/format'

use([BarChart, GridComponent, CanvasRenderer])

const overview = ref({})
const chartRef = ref(null)
const chart = shallowRef(null)
const loading = ref(false)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    overview.value = await api.get('/admin/statistics/overview')
  } catch (err) {
    error.value = err.message || '统计数据加载失败'
    return
  } finally {
    loading.value = false
  }
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
