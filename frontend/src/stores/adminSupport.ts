// src/stores/adminSupport.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'

export interface AdminSupportTicket {
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

export interface UpdateTicketRequest {
  response: string
  status: 'RESOLVED' | 'CLOSED'
}

export const useAdminSupportStore = defineStore('adminSupport', () => {
  const authStore = useAuthStore()

  const tickets = ref<AdminSupportTicket[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const fetchAllTickets = async () => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch('http://localhost:8080/admin/support/fetchAll', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
      })

      if (!response.ok) {
        throw new Error(`Failed to fetch tickets: ${response.status}`)
      }

      const data: AdminSupportTicket[] = await response.json()
      tickets.value = data || []

      return data
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to load support tickets'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateTicket = async (ticketId: number, updateData: UpdateTicketRequest) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await fetch(`http://localhost:8080/admin/support/${ticketId}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${authStore.token}`,
          Cookie: 'JSESSIONID=0BA80B06A6DB56DC2ED71E45B28BE2A6',
        },
        credentials: 'include',
        body: JSON.stringify(updateData),
      })

      if (!response.ok) {
        throw new Error(`Failed to update ticket: ${response.status}`)
      }

      const updatedTicket: AdminSupportTicket = await response.json()

      // Update ticket in the list
      const index = tickets.value.findIndex((ticket) => ticket.id === updatedTicket.id)
      if (index !== -1) {
        tickets.value[index] = updatedTicket
      }

      return updatedTicket
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to update ticket'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    tickets,
    isLoading,
    error,
    fetchAllTickets,
    updateTicket,
  }
})
