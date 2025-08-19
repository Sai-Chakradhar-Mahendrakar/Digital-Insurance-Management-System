// src/stores/policy.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Policy, PolicyResponse } from '@/types/policy'

export const usePolicyStore = defineStore('policy', () => {
  const policies = ref<Policy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const fetchPolicies = async (size: number = 1000) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/policies?size=${size}`, {
        method: 'GET',
        headers: {
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
      })

      if (!response.ok) {
        throw new Error('Failed to fetch policies')
      }

      const data: PolicyResponse = await response.json()
      policies.value = data.policies
    } catch (err) {
      error.value = 'Failed to load policies'
    } finally {
      isLoading.value = false
    }
  }

  return {
    policies,
    isLoading,
    error,
    fetchPolicies,
  }
})
