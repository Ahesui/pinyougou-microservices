import { createRouter, createWebHistory } from 'vue-router'
import UserManagement from '../views/UserManagement.vue'

const routes = [
  { path: '/', redirect: '/users' },
  { path: '/users', name: 'UserManagement', component: UserManagement },
  { path: '/products', name: 'ProductManagement', component: () => import('../views/ProductManagement.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router