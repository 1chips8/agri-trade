import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/products' },
    { path: '/products', component: () => import('./views/Products.vue') },
    { path: '/products/:id', component: () => import('./views/ProductDetail.vue') },
    { path: '/login', component: () => import('./views/Login.vue') },
    { path: '/forbidden', component: () => import('./views/Forbidden.vue') },
    { path: '/cart', component: () => import('./views/Cart.vue'), meta: { auth: true, roles: ['CONSUMER', 'MERCHANT'] } },
    { path: '/orders', component: () => import('./views/Orders.vue'), meta: { auth: true, roles: ['CONSUMER', 'MERCHANT'] } },
    { path: '/messages', component: () => import('./views/Messages.vue'), meta: { auth: true, roles: ['CONSUMER', 'MERCHANT'] } },
    { path: '/merchant', component: () => import('./views/Merchant.vue'), meta: { auth: true, roles: ['CONSUMER', 'MERCHANT'] } },
    { path: '/admin', component: () => import('./views/Admin.vue'), meta: { auth: true, roles: ['ADMIN'] } },
    { path: '/dashboard', component: () => import('./views/Dashboard.vue'), meta: { auth: true, roles: ['ADMIN'] } }
  ]
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (auth.token && !auth.user) {
    await auth.fetchMe()
    auth.fetchUnreadCount()
  }
  if (to.meta.auth && !auth.isLogin) return { path: '/login', query: { redirect: to.fullPath } }
  if (to.meta.roles && !to.meta.roles.includes(auth.role)) return '/forbidden'
})

export default router
