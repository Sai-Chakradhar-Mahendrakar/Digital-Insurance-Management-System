import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useUserPolicyStore } from './userPolicy'
import { useAuthStore } from './auth'
import type { Policy, PolicyResponse } from '@/types/policy'
import { useAppStore } from './app'

export const usePolicyStore = defineStore('policy', () => {
  const userPolicyStore = useUserPolicyStore()
  const authStore = useAuthStore()
  const appStore = useAppStore()
  const policies = ref<Policy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const fetchPolicies = async (size: number = 1000) => {
    isLoading.value = true
    error.value = null

    try {
      const url = `${appStore.apiEndpoints.policies}?size=${size}`
      const response = await appStore.httpClient.get(url)

      if (!response.ok) {
        throw new Error(`Failed to fetch policies: ${response.status} ${response.statusText}`)
      }

      const data: PolicyResponse = await response.json()

      if (Array.isArray(data)) {
        policies.value = data
      } else if (data.policies && Array.isArray(data.policies)) {
        policies.value = data.policies
      } else {
        policies.value = []
      }

      // If user is authenticated, fetch their purchased policies to filter
      if (authStore.isAuthenticated) {
        await userPolicyStore.fetchUserPolicies()
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load policies'
      console.error('Policy fetch error:', err)
    } finally {
      isLoading.value = false
    }
  }

  // Computed property to get available policies (not purchased by current user)
  const availablePolicies = computed(() => {
    if (!authStore.isAuthenticated) {
      return policies.value // Show all policies if not authenticated
    }

    return policies.value.filter((policy) => !userPolicyStore.isPolicyPurchased(policy.id))
  })

  // Computed property to get purchased policies info
  const policiesWithPurchaseStatus = computed(() => {
    return policies.value.map((policy) => ({
      ...policy,
      isPurchased: userPolicyStore.isPolicyPurchased(policy.id),
    }))
  })

  const getPolicyById = (id: number): Policy | undefined => {
    return policies.value.find((policy) => policy.id === id)
  }

  return {
    policies,
    availablePolicies,
    policiesWithPurchaseStatus,
    isLoading,
    error,
    fetchPolicies,
    getPolicyById,
  }
})
