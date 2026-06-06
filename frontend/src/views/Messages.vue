<template>
  <div class="page messages-page">
    <PageHeader title="消息中心" description="查看订单和审核相关的系统通知。">
      <template #actions>
        <el-button :disabled="!hasUnread" @click="readAll">全部已读</el-button>
        <el-button @click="load">刷新</el-button>
      </template>
    </PageHeader>

    <PageState v-if="loading" title="正在加载消息" />
    <PageState v-else-if="error" type="error" title="消息加载失败" :description="error">
      <el-button @click="load">重试</el-button>
    </PageState>
    <PageState v-else-if="!hasMessages" title="暂无消息" description="订单状态变化和审核结果通知会显示在这里。" />

    <template v-else>
      <div class="messages-toolbar">
        <span class="messages-toolbar__info">共 {{ messages.length }} 条，{{ auth.unreadCount }} 条未读</span>
        <span v-if="selectedIds.length" class="messages-toolbar__action">
          <el-button size="small" @click="batchRead">{{ selectedIds.length }} 条标记已读</el-button>
        </span>
      </div>

      <div v-for="msg in messages" :key="msg.id" class="message-card" :class="{ 'message-card--unread': msg.readStatus === 'UNREAD' }">
        <div class="message-card__select">
          <el-checkbox v-model="selectedIds" :label="msg.id" @change="onSelectionChange" />
        </div>
        <div class="message-card__body">
          <div class="message-card__header">
            <span class="message-card__title">{{ msg.title }}</span>
            <StatusTag kind="read" :value="msg.readStatus" />
          </div>
          <p class="message-card__content">{{ msg.content }}</p>
          <div class="message-card__meta">
            <span>{{ noticeLabel(msg.noticeType) }}</span>
            <span>{{ formatDateTime(msg.createTime) }}</span>
          </div>
        </div>
        <div class="message-card__actions">
          <el-button v-if="msg.readStatus === 'UNREAD'" link type="primary" size="small" @click="markRead(msg.id)">标为已读</el-button>
          <el-button link type="danger" size="small" @click="remove(msg.id)">删除</el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus/es/components/message/index'
import api from '../api'
import PageHeader from '../components/PageHeader.vue'
import PageState from '../components/PageState.vue'
import StatusTag from '../components/StatusTag.vue'
import { formatDateTime } from '../utils/format'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const messages = ref([])
const loading = ref(false)
const error = ref('')
const selectedIds = ref([])

const hasMessages = computed(() => messages.value.length > 0)
const hasUnread = computed(() => messages.value.some((m) => m.readStatus === 'UNREAD'))

const NOTICE_LABELS = {
  ORDER_STATUS: '订单通知',
  AUDIT_RESULT: '审核结果',
  PAYMENT: '支付通知',
  SYSTEM: '系统通知'
}

function noticeLabel(type) {
  return NOTICE_LABELS[type] || type || '通知'
}

function onSelectionChange() {
  // trigger reactivity for checkbox
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    messages.value = await api.get('/messages')
    selectedIds.value = []
  } catch (err) {
    error.value = err.message || '消息加载失败'
  } finally {
    loading.value = false
  }
}

async function markRead(messageId) {
  await api.post(`/messages/${messageId}/read`)
  await load()
  await auth.fetchUnreadCount()
}

async function batchRead() {
  if (!selectedIds.value.length) return
  await api.post('/messages/read', selectedIds.value)
  await load()
  await auth.fetchUnreadCount()
}

async function readAll() {
  await api.post('/messages/read-all')
  await load()
  await auth.fetchUnreadCount()
}

async function remove(messageId) {
  await api.delete(`/messages/${messageId}`)
  await load()
  await auth.fetchUnreadCount()
}

onMounted(() => {
  load()
  auth.fetchUnreadCount()
})
</script>

<style scoped>
.messages-page {
  max-width: 780px;
}
.messages-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 13px;
  color: #6b7280;
}
.message-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 8px;
  background: #fff;
  border: 1px solid #e4e7ec;
  border-radius: 8px;
  transition: box-shadow 0.15s;
}
.message-card:hover {
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.message-card--unread {
  border-left: 3px solid #d97706;
  background: #fffbeb;
}
.message-card__select {
  padding-top: 2px;
}
.message-card__body {
  flex: 1;
  min-width: 0;
}
.message-card__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.message-card__title {
  font-weight: 600;
  font-size: 14px;
}
.message-card__content {
  margin: 0 0 8px;
  font-size: 13px;
  color: #374151;
  line-height: 1.5;
  word-break: break-word;
}
.message-card__meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #9ca3af;
}
.message-card__actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex-shrink: 0;
}
</style>
