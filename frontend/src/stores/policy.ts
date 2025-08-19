// src/stores/policy.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Policy, PolicyResponse } from '@/types/policy'

export const usePolicyStore = defineStore('policy', () => {
  const policies = ref<Policy[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const fetchPolicies = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/policies', {
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
      // Use mock data for demo purposes
      policies.value = [
        {
          id: 1,
          name: 'Comprehensive Health Insurance',
          type: 'Health',
          description:
            'Provides full medical coverage including hospitalization and outpatient care.',
          premiumAmount: 5000.0,
          coverageAmount: 1000000.0,
          durationMonths: 12,
          renewalPremiumRate: 4800.0,
          createdAt: '2025-08-18T18:49:43.019767',
        },
      ]
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
