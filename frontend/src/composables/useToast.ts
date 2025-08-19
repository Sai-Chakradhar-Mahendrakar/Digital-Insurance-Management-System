// src/composables/useToast.ts
import { ref, reactive } from 'vue'

// Define the toast type more strictly
export type ToastType = 'success' | 'error' | 'warning' | 'info'

export interface Toast {
  id: string
  type: ToastType  // Changed from string to ToastType
  title: string
  message: string
  duration: number
  dismissible: boolean
}

const toasts = ref<Toast[]>([])

export const useToast = () => {
  const addToast = (toast: Omit<Toast, 'id'>) => {
    const id = Date.now().toString()
    const newToast: Toast = {
      id,
      ...toast
    }
    
    toasts.value.push(newToast)
    
    if (toast.duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, toast.duration)
    }
    
    return id
  }

  const removeToast = (id: string) => {
    const index = toasts.value.findIndex(toast => toast.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  const success = (title: string, message: string = '', duration: number = 4000) => {
    return addToast({ type: 'success', title, message, duration, dismissible: true })
  }

  const error = (title: string, message: string = '', duration: number = 6000) => {
    return addToast({ type: 'error', title, message, duration, dismissible: true })
  }

  const warning = (title: string, message: string = '', duration: number = 5000) => {
    return addToast({ type: 'warning', title, message, duration, dismissible: true })
  }

  const info = (title: string, message: string = '', duration: number = 4000) => {
    return addToast({ type: 'info', title, message, duration, dismissible: true })
  }

  const clear = () => {
    toasts.value = []
  }

  return {
    toasts,
    addToast,
    removeToast,
    success,
    error,
    warning,
    info,
    clear
  }
}
