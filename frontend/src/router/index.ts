// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { isTokenExpired, isAdminToken } from '@/utils/jwt'

// Import Views
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PoliciesView from '@/views/PoliciesView.vue'

// Admin Views
import AdminLoginView from '@/views/admin/AdminLoginView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import AdminPoliciesView from '@/views/admin/AdminPoliciesView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
  },
  {
    path: '/policies',
    name: 'policies',
    component: PoliciesView,
  },
  // Admin Routes
  {
    path: '/admin/login',
    name: 'admin-login',
    component: AdminLoginView,
    meta: {
      layout: 'admin',
      guest: true,
    },
  },
  {
    path: '/admin',
    name: 'admin',
    redirect: '/admin/dashboard',
  },
  {
    path: '/admin/dashboard',
    name: 'admin-dashboard',
    component: AdminDashboardView,
    meta: {
      requiresAdmin: true,
      layout: 'admin',
    },
  },
  {
    path: '/admin/policies',
    name: 'admin-policies',
    component: AdminPoliciesView,
    meta: {
      requiresAdmin: true,
      layout: 'admin',
    },
  },
  // Catch all 404
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Navigation Guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Check if route requires admin access
  if (to.meta.requiresAdmin) {
    const token = authStore.token

    // Check if user is authenticated
    if (!authStore.isAuthenticated || !token) {
      console.warn('Admin access denied: Not authenticated')
      next({
        name: 'admin-login',
        query: { redirect: to.fullPath },
      })
      return
    }

    // Check if token is expired
    if (isTokenExpired(token)) {
      console.warn('Admin access denied: Token expired')
      authStore.logout()
      next({
        name: 'admin-login',
        query: { redirect: to.fullPath, expired: 'true' },
      })
      return
    }

    // Check if token has admin role
    if (!isAdminToken(token)) {
      console.error('Admin access denied: Insufficient permissions')
      next({
        name: 'admin-login',
        query: { error: 'insufficient_permissions' },
      })
      return
    }

    // Verify admin role from store
    if (!authStore.isAdmin) {
      console.error('Admin access denied: User role is not ADMIN')
      next({
        name: 'admin-login',
        query: { error: 'invalid_role' },
      })
      return
    }

    next()
  }
  // Guest routes - redirect if already authenticated
  else if (to.meta.guest && authStore.isAuthenticated) {
    if (authStore.isAdmin) {
      next('/admin/dashboard')
    } else {
      next('/')
    }
  }
  // Public routes
  else {
    next()
  }
})

// After navigation
router.afterEach((to, from) => {
  // Update page title based on route
  const defaultTitle = 'Digital Insurance Management System'
  const routeTitle = to.meta.title as string
  document.title = routeTitle ? `${routeTitle} - ${defaultTitle}` : defaultTitle
})

export default router
