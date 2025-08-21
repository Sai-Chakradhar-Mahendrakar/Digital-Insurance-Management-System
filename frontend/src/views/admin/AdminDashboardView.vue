<!-- src/views/admin/AdminDashboardView.vue -->
<template>
  <div class="min-h-screen bg-slate-50">
    <AdminNavbar />

    <div class="flex">
      <AdminSidebar />

      <!-- Main Content -->
      <main class="flex-1 p-8">
        <div class="max-w-7xl mx-auto">
          <!-- Dashboard Header -->
          <div class="mb-8">
            <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">Admin Dashboard</h1>
            <p class="text-slate-600 font-inter">
              Manage your insurance policies and system overview
            </p>
          </div>

          <!-- Session Expiry Warning -->
          <div
            v-if="tokenInfo.timeToExpiry < 300"
            class="bg-orange-50 border border-orange-200 rounded-lg p-4 mb-8"
          >
            <div class="flex items-center">
              <AlertTriangle class="w-5 h-5 text-orange-600 mr-3" />
              <div>
                <h4 class="font-medium text-orange-900">Session Expiring Soon</h4>
                <p class="text-sm text-orange-700">
                  Your admin session will expire in
                  {{ formatTimeRemaining(tokenInfo.timeToExpiry) }}. Please refresh or re-login to
                  continue.
                </p>
              </div>
            </div>
          </div>

          <!-- Stats Cards -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <StatsCard
              title="Total Policies"
              :value="policies.length.toString()"
              icon="shield"
              color="blue"
            />
            <StatsCard
              title="Active Users"
              :value="isLoadingActiveUsers ? 'Loading...' : String(activeUsers)"
              icon="users"
              color="green"
            />
            <StatsCard
              title="Claims Processed"
              :value="approvedClaims.toString()"
              icon="file-text"
              color="yellow"
            />
            <StatsCard
              title="Revenue (₹)"
              :value="totalPremium.toString()"
              icon="trending-up"
              color="purple"
            />
          </div>

          <!-- Rest of dashboard content... -->
          <!-- Quick Actions -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-8">
            <h3 class="text-lg font-semibold text-slate-900 mb-4">Quick Actions</h3>
            <div class="flex flex-wrap gap-4">
              <AppButton
                variant="primary"
                @click="navigateTo('/admin/policies')"
                class="flex items-center space-x-2"
              >
                <Plus class="w-4 h-4" />
                <span>Add New Policy</span>
              </AppButton>
              <!-- <AppButton variant="secondary" class="flex items-center space-x-2">
                <FileText class="w-4 h-4" />
                <span>Generate Report</span>
              </AppButton> -->
              <!-- <AppButton variant="ghost" class="flex items-center space-x-2">
                <Settings class="w-4 h-4" />
                <span>System Settings</span>
              </AppButton> -->
            </div>
          </div>

          <!-- Recent Activity -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
            <h3 class="text-lg font-semibold text-slate-900 mb-4">Recent Activity</h3>
            <div class="space-y-4">
              <div
                v-for="activity in recentActivities"
                :key="activity.id"
                class="flex items-center space-x-4 p-3 hover:bg-slate-50 rounded-lg"
              >
                <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                  <component :is="getActivityIcon(activity.type)" class="w-5 h-5 text-blue-600" />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-slate-900">{{ activity.title }}</p>
                  <p class="text-xs text-slate-500">{{ activity.time }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AdminNavbar from '@/components/admin/AdminNavbar.vue'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import StatsCard from '@/components/admin/StatsCard.vue'
import AppButton from '@/components/common/AppButton.vue'
import { useAdminPolicyStore } from '@/stores/adminPolicy'
import { useAdminUserStore } from '@/stores/adminUser'
import { useAdminClaimsStore } from '@/stores/adminClaims'

import {
  Plus,
  FileText,
  Settings,
  Shield,
  User,
  Clock,
  ChevronDown,
  ChevronRight,
  AlertTriangle,
} from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()
const adminPolicyStore = useAdminPolicyStore()
const showTokenDetails = ref(false)
const tokenInfo = computed(() => authStore.tokenInfo)
const policies = computed(() => adminPolicyStore.policies)

const adminUserStore = useAdminUserStore()
const adminClaimsStore = useAdminClaimsStore()
const { fetchActiveUsers, activeUsers, isLoadingActiveUsers, errorActiveUsers } = adminUserStore

// const users = computed(() => adminUserStore.users)
const approvedClaims = computed(
  () => adminClaimsStore.claims.filter((c) => c.status === 'APPROVED').length,
)
const totalPremium = computed(() =>
  adminPolicyStore.policies.reduce((sum, p) => sum + p.premiumAmount, 0),
)
// const { fetchActiveUsers } = adminUserStore
const recentActivities = ref([
  { id: 1, type: 'policy', title: 'New health insurance policy created', time: '2 hours ago' },
  { id: 2, type: 'user', title: 'New user registration: John Doe', time: '4 hours ago' },
  { id: 3, type: 'claim', title: 'Claim approved for Policy #1234', time: '6 hours ago' },
  { id: 4, type: 'policy', title: 'Motor insurance policy updated', time: '8 hours ago' },
])

const navigateTo = (path: string) => {
  router.push(path)
}

const getActivityIcon = (type: string) => {
  const icons: Record<string, any> = {
    policy: Shield,
    user: User,
    claim: Clock,
  }
  return icons[type] || Shield
}

onMounted(async () => {
  await adminPolicyStore.fetchAdminPolicies()
})
onMounted(() => {
  fetchActiveUsers()
})

const formatTimestamp = (timestamp?: number) => {
  if (!timestamp) return 'N/A'
  return new Date(timestamp * 1000).toLocaleString('en-IN')
}

const formatExpiry = (timestamp?: number) => {
  if (!timestamp) return 'N/A'
  const date = new Date(timestamp * 1000)
  return date.toLocaleString('en-IN', {
    dateStyle: 'short',
    timeStyle: 'short',
  })
}

const formatTimeRemaining = (seconds: number) => {
  if (seconds <= 0) return 'Expired'

  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const remainingSeconds = seconds % 60

  if (hours > 0) {
    return `${hours}h ${minutes}m`
  } else if (minutes > 0) {
    return `${minutes}m ${remainingSeconds}s`
  } else {
    return `${remainingSeconds}s`
  }
}
</script>
