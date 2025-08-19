<template>
  <Teleport to="body">
    <div class="fixed top-4 right-4 z-50 w-full max-w-sm pointer-events-none">
      <TransitionGroup name="toast" tag="div" class="flex flex-col gap-3">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="toastClasses(toast.type)"
          class="relative group shadow-lg rounded-lg pointer-events-auto border-2 overflow-hidden transform transition-all duration-300 hover:scale-[1.02] hover:shadow-xl"
        >
          <div
            v-if="toast.duration > 0"
            class="absolute inset-0 opacity-40 transition-all duration-100 ease-linear overflow-hidden"
            :class="animatedBackgroundClasses(toast.type)"
            :style="{
              clipPath: `inset(0 ${100 - getProgressWidth(toast)}% 0 0)`,
              transition: 'clip-path 100ms linear',
            }"
          ></div>

          <div class="absolute inset-0 opacity-100" :class="backgroundGradient(toast.type)"></div>

          <div
            v-if="toast.duration > 0"
            class="absolute top-0 left-0 h-1 transition-all duration-100 ease-linear"
            :class="progressBarClasses(toast.type)"
            :style="{ width: `${getProgressWidth(toast)}%` }"
          ></div>

          <div class="relative p-4">
            <div class="flex items-start">
              <div class="flex-shrink-0 relative">
                <div
                  class="w-8 h-8 rounded-full flex items-center justify-center"
                  :class="iconBackgroundClasses(toast.type)"
                >
                  <component
                    :is="getIcon(toast.type)"
                    class="h-5 w-5 text-white transition-transform duration-300 group-hover:scale-110"
                  />
                </div>
              </div>

              <div class="ml-3 w-0 flex-1 pt-0.5">
                <p class="text-sm font-semibold text-gray-900 leading-tight">
                  {{ toast.title }}
                </p>
                <p v-if="toast.message" class="mt-1 text-sm text-gray-600 leading-relaxed">
                  {{ toast.message }}
                </p>
              </div>

              <div v-if="toast.dismissible" class="ml-4 flex-shrink-0 flex">
                <button
                  @click="removeToast(toast.id)"
                  class="rounded-full p-1 inline-flex text-gray-400 hover:text-gray-600 transition-all duration-200 focus:outline-none transform hover:scale-110"
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
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { useToast } from '@/composables/useToast'
import type { Toast } from '@/composables/useToast' // Import the Toast type
import { CheckCircle, AlertCircle, AlertTriangle, Info, X, Zap } from 'lucide-vue-next'

const { toasts, removeToast } = useToast()

// --- REFACTORED PROGRESS & ANIMATION LOGIC ---

// 1. Reactive timestamp for smooth animations
const currentTime = ref(Date.now())
let animationFrameId: number

// 2. Map to store metadata for each toast
const toastMeta = ref(new Map())

// 3. Animation loop using requestAnimationFrame
const updateCurrentTime = () => {
  currentTime.value = Date.now()
  animationFrameId = requestAnimationFrame(updateCurrentTime)
}

onMounted(() => {
  // Start the animation loop when the component is mounted
  updateCurrentTime()
})

onUnmounted(() => {
  // Stop the animation loop to prevent memory leaks
  cancelAnimationFrame(animationFrameId)
  toastMeta.value.clear()
})

// 4. Watch for new toasts to track their start time
watch(
  toasts,
  (newToasts) => {
    newToasts.forEach((toast) => {
      if (!toastMeta.value.has(toast.id)) {
        toastMeta.value.set(toast.id, { startTime: Date.now() })
      }
    })
  },
  { deep: true },
)

// --- HELPER FUNCTIONS ---

const getIcon = (type: string) => {
  const icons = {
    success: CheckCircle,
    error: AlertCircle,
    warning: AlertTriangle,
    info: Info,
    urgent: Zap,
  }
  return icons[type as keyof typeof icons] || Info
}

const toastClasses = (type: string) => {
  const classes = {
    success: 'border-emerald-500 bg-emerald-50',
    error: 'border-red-500 bg-red-50',
    warning: 'border-amber-500 bg-amber-50',
    info: 'border-blue-500 bg-blue-50',
    urgent: 'border-purple-500 bg-purple-50 animate-pulse',
  }
  return classes[type as keyof typeof classes] || classes.info
}

const iconBackgroundClasses = (type: string) => {
  const classes = {
    success: 'bg-emerald-500',
    error: 'bg-red-500',
    warning: 'bg-amber-500',
    info: 'bg-blue-500',
    urgent: 'bg-purple-500',
  }
  return classes[type as keyof typeof classes] || classes.info
}

const backgroundGradient = (type: string) => {
  const classes = {
    success: 'bg-emerald-50',
    error: 'bg-red-50',
    warning: 'bg-amber-50',
    info: 'bg-blue-50',
    urgent: 'bg-purple-50',
  }
  return classes[type as keyof typeof classes] || classes.info
}

const animatedBackgroundClasses = (type: string) => {
  const classes = {
    success: 'bg-emerald-200',
    error: 'bg-red-200',
    warning: 'bg-amber-200',
    info: 'bg-blue-200',
    urgent: 'bg-purple-200',
  }
  return classes[type as keyof typeof classes] || classes.info
}

const progressBarClasses = (type: string) => {
  const classes = {
    success: 'bg-emerald-500',
    error: 'bg-red-500',
    warning: 'bg-amber-500',
    info: 'bg-blue-500',
    urgent: 'bg-purple-500',
  }
  return classes[type as keyof typeof classes] || classes.info
}

const getProgressWidth = (toast: Toast) => {
  if (!toast.duration || toast.duration <= 0) return 100

  const meta = toastMeta.value.get(toast.id)
  if (!meta) return 100 // Return 100 if toast is not yet tracked

  // Use the reactive currentTime for calculation
  const elapsed = currentTime.value - meta.startTime
  const progress = Math.min(100, (elapsed / toast.duration) * 100)

  return 100 - progress
}
</script>

<style scoped>
/* Let Vue handle the active classes automatically.
   The .toast-move class is essential for smooth re-ordering. */
.toast-move,
.toast-enter-active,
.toast-leave-active {
  transition: all 0.4s cubic-bezier(0.55, 0, 0.1, 1);
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(-30px);
}

/* Ensure leave animations don't disrupt layout */
.toast-leave-active {
  position: absolute;
}
</style>
