<!-- src/views/admin/AdminPoliciesView.vue -->
<template>
  <div class="min-h-screen bg-slate-50">
    <AdminNavbar />

    <div class="flex">
      <AdminSidebar />

      <!-- Main Content -->
      <main class="flex-1 p-8">
        <div class="max-w-7xl mx-auto">
          <!-- Header -->
          <div class="flex justify-between items-center mb-8">
            <div>
              <h1 class="text-3xl font-bold text-slate-900 font-poppins mb-2">Manage Policies</h1>
              <p class="text-slate-600 font-inter">Create, edit, and manage insurance policies</p>
            </div>
            <AppButton
              variant="primary"
              @click="showAddPolicyModal = true"
              class="flex items-center space-x-2"
            >
              <Plus class="w-4 h-4" />
              <span>Add New Policy</span>
            </AppButton>
          </div>

          <!-- Policies Table -->
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div class="p-6 border-b border-slate-200">
              <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-slate-900">All Policies</h3>
                <div class="flex items-center space-x-4">
                  <div class="relative">
                    <Search
                      class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-slate-400"
                    />
                    <input
                      v-model="searchQuery"
                      type="text"
                      placeholder="Search policies..."
                      class="pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-slate-50">
                  <tr>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Policy
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Type
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Premium (₹)
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Coverage (₹)
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Duration
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
                    >
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-slate-200">
                  <tr v-for="policy in filteredPolicies" :key="policy.id" class="hover:bg-slate-50">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div class="text-sm font-medium text-slate-900">{{ policy.name }}</div>
                        <div class="text-sm text-slate-500 truncate max-w-xs">
                          {{ policy.description }}
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                      >
                        {{ policy.type }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ formatINR(policy.premiumAmount) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ formatINR(policy.coverageAmount) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900">
                      {{ policy.durationMonths }} months
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div class="flex items-center space-x-3">
                        <button
                          @click="editPolicy(policy)"
                          class="text-blue-600 hover:text-blue-900 transition-colors"
                        >
                          <Edit class="w-4 h-4" />
                        </button>
                        <button
                          @click="deletePolicy(policy)"
                          class="text-red-600 hover:text-red-900 transition-colors"
                        >
                          <Trash2 class="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Add/Edit Policy Modal -->
    <AddPolicyModal
      v-if="showAddPolicyModal"
      :policy="selectedPolicy"
      @close="closeModal"
      @save="handleSavePolicy"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAdminPolicyStore } from '@/stores/adminPolicy'
import AdminNavbar from '@/components/admin/AdminNavbar.vue'
import AdminSidebar from '@/components/admin/AdminSidebar.vue'
import AddPolicyModal from '@/components/admin/AddPolicyModal.vue'
import AppButton from '@/components/common/AppButton.vue'
import { Plus, Search, Edit, Trash2 } from 'lucide-vue-next'
import type { Policy } from '@/types/policy'

const adminPolicyStore = useAdminPolicyStore()

const showAddPolicyModal = ref(false)
const selectedPolicy = ref<Policy | null>(null)
const searchQuery = ref('')

const policies = computed(() => adminPolicyStore.policies)

const filteredPolicies = computed(() => {
  if (!searchQuery.value.trim()) return policies.value

  const query = searchQuery.value.toLowerCase()
  return policies.value.filter(
    (policy) =>
      policy.name.toLowerCase().includes(query) ||
      policy.type.toLowerCase().includes(query) ||
      policy.description.toLowerCase().includes(query),
  )
})

const formatINR = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)
}

const editPolicy = (policy: Policy) => {
  selectedPolicy.value = policy
  showAddPolicyModal.value = true
}

const deletePolicy = async (policy: Policy) => {
  if (confirm(`Are you sure you want to delete "${policy.name}"?`)) {
    try {
      await adminPolicyStore.deletePolicy(policy.id)
    } catch (error) {
      alert('Failed to delete policy')
    }
  }
}

const handleSavePolicy = async (policyData: any) => {
  try {
    if (selectedPolicy.value) {
      await adminPolicyStore.updatePolicy(selectedPolicy.value.id, policyData)
    } else {
      await adminPolicyStore.createPolicy(policyData)
    }
    closeModal()
  } catch (error) {
    alert('Failed to save policy')
  }
}

const closeModal = () => {
  showAddPolicyModal.value = false
  selectedPolicy.value = null
}

onMounted(() => {
  adminPolicyStore.fetchAdminPolicies()
})
</script>
