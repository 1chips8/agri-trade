<template>
  <div class="page-state" :class="`page-state--${type}`">
    <h3>{{ title }}</h3>
    <p v-if="description">{{ description }}</p>
    <div v-if="$slots.default" class="page-state__actions">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: { type: String, default: 'empty' },
  title: { type: String, required: true },
  description: { type: String, default: '' }
})
</script>
