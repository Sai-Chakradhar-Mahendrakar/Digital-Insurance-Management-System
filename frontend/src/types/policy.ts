// src/types/policy.ts
export interface Policy {
  id: number
  name: string
  type: string
  description: string
  premiumAmount: number
  coverageAmount: number
  durationMonths: number
  renewalPremiumRate: number
  createdAt: string
}

export interface PolicyResponse {
  policies: Policy[]
  totalElements: number
  totalPages: number
  size: number
  page: number
}
