<!-- src/components/admin/AdminSidebar.vue -->
<template>
  <aside class="w-64 bg-white border-r border-slate-200 min-h-screen">
    <nav class="p-4">
      <ul class="space-y-2">
        <li>
          <router-link
            to="/admin/dashboard"
            class="flex items-center space-x-3 px-4 py-3 text-slate-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/admin/dashboard' }"
          >
            <LayoutDashboard class="w-5 h-5" />
            <span class="font-medium">Dashboard</span>
          </router-link>
        </li>
        <li>
          <router-link
            to="/admin/policies"
            class="flex items-center space-x-3 px-4 py-3 text-slate-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/admin/policies' }"
          >
            <Shield class="w-5 h-5" />
            <span class="font-medium">Policies</span>
          </router-link>
        </li>
        <li>
          <router-link
            to="/admin/users"
            class="flex items-center space-x-3 px-4 py-3 text-slate-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors"
          >
            <Users class="w-5 h-5" />
            <span class="font-medium">Users</span>
          </router-link>
        </li>
        <li>
          <router-link
            to="/admin/claims"
            class="flex items-center space-x-3 px-4 py-3 text-slate-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors"
          >
            <FileText class="w-5 h-5" />
            <span class="font-medium">Claims</span>
          </router-link>
        </li>
        <li>
          <router-link
            to="/admin/reports"
            class="flex items-center space-x-3 px-4 py-3 text-slate-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors"
          >
            <BarChart3 class="w-5 h-5" />
            <span class="font-medium">Reports</span>
          </router-link>
        </li>
        <li>
          <router-link
            to="/admin/settings"
            class="flex items-center space-x-3 px-4 py-3 text-slate-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors"
          >
            <Settings class="w-5 h-5" />
            <span class="font-medium">Settings</span>
          </router-link>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script setup lang="ts">
import { LayoutDashboard, Shield, Users, FileText, BarChart3, Settings } from 'lucide-vue-next'
</script>
