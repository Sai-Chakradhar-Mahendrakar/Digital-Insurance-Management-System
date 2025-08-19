<!-- src/components/common/ToastContainer.vue -->
<template>
  <Teleport to="body">
    <div class="fixed top-4 right-4 z-50 space-y-2">
      <TransitionGroup name="toast" tag="div" class="space-y-2">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="toastClasses(toast.type)"
          class="max-w-sm w-full bg-white shadow-lg rounded-lg pointer-events-auto ring-1 ring-black ring-opacity-5 overflow-hidden"
        >
          <div class="p-4">
            <div class="flex items-start">
              <div class="flex-shrink-0">
                <component
                  :is="getIcon(toast.type)"
                  :class="iconClasses(toast.type)"
                  class="h-6 w-6"
                />
              </div>
              <div class="ml-3 w-0 flex-1 pt-0.5">
                <p class="text-sm font-medium text-gray-900">{{ toast.title }}</p>
                <p v-if="toast.message" class="mt-1 text-sm text-gray-500">{{ toast.message }}</p>
              </div>
              <div v-if="toast.dismissible" class="ml-4 flex-shrink-0 flex">
                <button
                  @click="removeToast(toast.id)"
                  class="bg-white rounded-md inline-flex text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                  <span class="sr-only">Close</span>
                  <X class="h-5 w-5" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { useToast } from '@/composables/useToast'
import { CheckCircle, AlertCircle, AlertTriangle, Info, X } from 'lucide-vue-next'

const { toasts, removeToast } = useToast()

const getIcon = (type: string) => {
  const icons = {
    success: CheckCircle,
    error: AlertCircle,
    warning: AlertTriangle,
    info: Info,
  }
  return icons[type as keyof typeof icons] || Info
}

const toastClasses = (type: string) => {
  const classes = {
    success: 'border-l-4 border-green-400',
    error: 'border-l-4 border-red-400',
    warning: 'border-l-4 border-yellow-400',
    info: 'border-l-4 border-blue-400',
  }
  return classes[type as keyof typeof classes] || classes.info
}

const iconClasses = (type: string) => {
  const classes = {
    success: 'text-green-400',
    error: 'text-red-400',
    warning: 'text-yellow-400',
    info: 'text-blue-400',
  }
  return classes[type as keyof typeof classes] || classes.info
}
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.toast-move {
  transition: transform 0.3s ease;
}
</style>
