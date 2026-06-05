import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/products' },
    { path: '/products', component: () => import('./views/Products.vue') },
    { path: '/login', component: () => import('./views/Login.vue') },
    { path: '/cart', component: () => import('./views/Cart.vue'), meta: { auth: true } },
    { path: '/orders', component: () => import('./views/Orders.vue'), meta: { auth: true } },
    { path: '/merchant', component: () => import('./views/Merchant.vue'), meta: { auth: true } },
    { path: '/admin', component: () => import('./views/Admin.vue'), meta: { auth: true } },
    { path: '/dashboard', component: () => import('./views/Dashboard.vue'), meta: { auth: true } }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isLogin) return '/login'
})

export default router
