// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { isTokenExpired, isAdminToken } from '@/utils/jwt'

// Import Views
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PoliciesView from '@/views/PoliciesView.vue'
import NotFoundView from '@/views/NotFoundView.vue'

// Admin Views
import AdminLoginView from '@/views/admin/AdminLoginView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import AdminPoliciesView from '@/views/admin/AdminPoliciesView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: {
      title: 'Home',
    },
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      title: 'Login',
      guest: true,
    },
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: {
      title: 'Register',
      guest: true,
    },
  },
  {
    path: '/policies',
    name: 'policies',
    component: PoliciesView,
    meta: {
      title: 'Insurance Policies',
    },
  },

  // Admin Routes
  {
    path: '/admin/login',
    name: 'admin-login',
    component: AdminLoginView,
    meta: {
      title: 'Admin Login',
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
      title: 'Admin Dashboard',
      requiresAdmin: true,
      layout: 'admin',
    },
  },
  {
    path: '/admin/policies',
    name: 'admin-policies',
    component: AdminPoliciesView,
    meta: {
      title: 'Manage Policies',
      requiresAdmin: true,
      layout: 'admin',
    },
  },

  // 404 Not Found - Must be last
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: {
      title: 'Page Not Found',
    },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Navigation Guard (same as before)
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Check if route requires admin access
  if (to.meta.requiresAdmin) {
    const token = authStore.token

    if (!authStore.isAuthenticated || !token) {
      console.warn('Admin access denied: Not authenticated')
      next({
        name: 'admin-login',
        query: { redirect: to.fullPath },
      })
      return
    }

    if (isTokenExpired(token)) {
      console.warn('Admin access denied: Token expired')
      authStore.logout()
      next({
        name: 'admin-login',
        query: { redirect: to.fullPath, expired: 'true' },
      })
      return
    }

    if (!isAdminToken(token)) {
      console.error('Admin access denied: Insufficient permissions')
      next({
        name: 'admin-login',
        query: { error: 'insufficient_permissions' },
      })
      return
    }

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

// Update page title
router.afterEach((to) => {
  const defaultTitle = 'Digital Insurance Management System'
  const routeTitle = to.meta.title as string
  document.title = routeTitle ? `${routeTitle} - ${defaultTitle}` : defaultTitle
})

export default router
