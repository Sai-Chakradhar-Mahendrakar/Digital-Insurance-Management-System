import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { useAppStore } from './app'

export interface SupportTicket {
  id: number
  userId: number
  policyId: number | null
  claimId: number | null
  subject: string
  description: string
  status: 'OPEN' | 'RESOLVED' | 'CLOSED'
  response: string | null
  createdAt: string
  resolvedAt: string | null
}

export interface CreateTicketRequest {
  policyId?: number
  claimId?: number
  subject: string
  description: string
}

export const useSupportTicketsStore = defineStore('supportTickets', () => {
  const authStore = useAuthStore()
  const appStore = useAppStore()

  const tickets = ref<SupportTicket[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const createSupportTicket = async (ticketData: CreateTicketRequest) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await appStore.httpClient.post(
        appStore.apiEndpoints.createSupportTicket,
        ticketData
      )

      if (!response.ok) {
        throw new Error(`Failed to create ticket: ${response.status}`)
      }

      const newTicket: SupportTicket = await response.json()
      tickets.value.unshift(newTicket)

      return newTicket
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to create support ticket'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchUserTickets = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await appStore.httpClient.get(
        `${appStore.config.apiBaseUrl}/support/user/getTicketsByUser`
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch tickets: ${response.status}`)
      }

      const data: SupportTicket[] = await response.json()
      tickets.value = data || []
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load support tickets'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    tickets,
    isLoading,
    error,
    createSupportTicket,
    fetchUserTickets,
  }
})
