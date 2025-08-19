<!-- src/views/PoliciesView.vue -->
<template>
  <div class="min-h-screen bg-slate-50">
    <!-- Header -->
    <header class="bg-white border-b border-slate-200 sticky top-0 z-40">
      <div class="max-w-7xl mx-auto px-4 lg:px-8 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <router-link to="/" class="flex items-center space-x-2">
              <div class="w-8 h-8 bg-blue-700 rounded-lg flex items-center justify-center">
                <span class="text-white font-bold text-sm">DI</span>
              </div>
              <h1 class="text-xl font-semibold text-slate-900 font-poppins">Digital Insurance</h1>
            </router-link>
          </div>
          <AppButton variant="ghost" @click="logout">
            <LogOut class="w-4 h-4 mr-2" />
            Logout
          </AppButton>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="text-3xl font-semibold text-slate-900 font-poppins mb-2">Insurance Policies</h1>
        <p class="text-slate-600 font-inter">Manage and view your insurance policies</p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center py-12">
        <div
          class="w-8 h-8 border-2 border-blue-700 border-t-transparent rounded-full animate-spin"
        ></div>
      </div>

      <!-- Empty State -->
      <div v-else-if="policies.length === 0" class="text-center py-12">
        <Shield class="w-16 h-16 text-slate-400 mx-auto mb-4" />
        <h3 class="text-xl font-semibold text-slate-900 mb-2">No Policies Found</h3>
        <p class="text-slate-600">You don't have any insurance policies yet.</p>
      </div>

      <!-- Policies Grid -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
        <PolicyCard
          v-for="policy in policies"
          :key="policy.id"
          :policy="policy"
          @view-details="viewPolicyDetails"
        />
      </div>

      <!-- Policy Details Modal -->
      <PolicyModal v-if="selectedPolicy" :policy="selectedPolicy" @close="selectedPolicy = null" />
    </main>
  </div>
</template>

// In src/views/PoliciesView.vue - update the script section
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue' // Add computed here
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePolicyStore } from '@/stores/policy'
import AppButton from '@/components/common/AppButton.vue'
import PolicyCard from '@/components/policies/PolicyCard.vue'
import PolicyModal from '@/components/policies/PolicyModal.vue'
import { Shield, LogOut } from 'lucide-vue-next'
import type { Policy } from '@/types/policy'

const router = useRouter()
const authStore = useAuthStore()
const policyStore = usePolicyStore()

const isLoading = ref(true)
const selectedPolicy = ref<Policy | null>(null)

const policies = computed(() => policyStore.policies) // This line needs computed imported

const viewPolicyDetails = (policy: Policy) => {
  selectedPolicy.value = policy
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(async () => {
  try {
    await policyStore.fetchPolicies()
  } finally {
    isLoading.value = false
  }
})
</script>
