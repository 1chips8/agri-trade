<template>
  <el-tag :type="meta.type" effect="light">{{ meta.label }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { AUDIT_STATUS, APPLY_STATUS, ORDER_STATUS, SALE_STATUS, statusMeta } from '../constants/status'

const props = defineProps({
  kind: { type: String, required: true },
  value: { type: String, default: '' }
})

const maps = {
  order: ORDER_STATUS,
  audit: AUDIT_STATUS,
  sale: SALE_STATUS,
  apply: APPLY_STATUS
}

const meta = computed(() => statusMeta(maps[props.kind] || {}, props.value))
</script>
