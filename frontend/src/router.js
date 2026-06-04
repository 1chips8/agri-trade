import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from './stores/auth'
import Products from './views/Products.vue'
import Login from './views/Login.vue'
import Cart from './views/Cart.vue'
import Orders from './views/Orders.vue'
import Merchant from './views/Merchant.vue'
import Admin from './views/Admin.vue'
import Dashboard from './views/Dashboard.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/products' },
    { path: '/products', component: Products },
    { path: '/login', component: Login },
    { path: '/cart', component: Cart, meta: { auth: true } },
    { path: '/orders', component: Orders, meta: { auth: true } },
    { path: '/merchant', component: Merchant, meta: { auth: true } },
    { path: '/admin', component: Admin, meta: { auth: true } },
    { path: '/dashboard', component: Dashboard, meta: { auth: true } }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isLogin) return '/login'
})

export default router
