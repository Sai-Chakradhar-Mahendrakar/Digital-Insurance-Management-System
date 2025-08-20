// src/types/policy.ts
export interface Policy {
  id: number
  name: string
  type: string
  description: string
  premiumAmount: number
  coverageAmount: number
  features?: string[]
  terms?: string[]
  eligibility?: string[]
  createdAt?: string
  updatedAt?: string
}

export interface PolicyResponse {
  policies: Policy[]
  totalElements: number
  totalPages: number
  size: number
  page: number
}
