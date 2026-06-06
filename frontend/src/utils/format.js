export const fallbackProductImage =
  'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80'

export function formatMoney(value) {
  const amount = Number(value || 0)
  return `￥${amount.toFixed(2)}`
}

export function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

export function textOrDash(value) {
  return value === null || value === undefined || value === '' ? '-' : value
}

export function productImage(value) {
  return value || fallbackProductImage
}
