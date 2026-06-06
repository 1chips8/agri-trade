export const ORDER_STATUS = {
  PENDING_PAYMENT: { label: '待支付', type: 'warning' },
  WAIT_SHIPMENT: { label: '待发货', type: 'primary' },
  SHIPPED: { label: '已发货', type: 'success' },
  COMPLETED: { label: '已完成', type: 'success' },
  CANCELED: { label: '已取消', type: 'info' }
}

export const AUDIT_STATUS = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}

export const SALE_STATUS = {
  ON_SALE: { label: '已上架', type: 'success' },
  OFF_SALE: { label: '已下架', type: 'info' }
}

export const APPLY_STATUS = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}

export const READ_STATUS = {
  UNREAD: { label: '未读', type: 'warning' },
  READ: { label: '已读', type: 'info' }
}

export const ROLE_LABEL = {
  GUEST: '游客',
  USER: '买家',
  MERCHANT: '商家',
  ADMIN: '管理员'
}

export function statusMeta(map, value) {
  return map[value] || { label: value || '未知', type: 'info' }
}
