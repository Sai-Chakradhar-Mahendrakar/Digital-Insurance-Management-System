<!-- src/components/common/InlineError.vue -->
<template>
  <Transition
    name="error"
    enter-active-class="transition ease-out duration-200"
    enter-from-class="opacity-0 transform scale-95"
    enter-to-class="opacity-100 transform scale-100"
    leave-active-class="transition ease-in duration-150"
    leave-from-class="opacity-100 transform scale-100"
    leave-to-class="opacity-0 transform scale-95"
  >
    <div v-if="error" class="mt-2 p-3 bg-red-50 border border-red-200 rounded-md">
      <div class="flex">
        <div class="flex-shrink-0">
          <AlertCircle class="h-5 w-5 text-red-400" />
        </div>
        <div class="ml-3">
          <h3 v-if="title" class="text-sm font-medium text-red-800">{{ title }}</h3>
          <div class="text-sm text-red-700">
            <p>{{ error }}</p>
          </div>
          <div v-if="dismissible" class="mt-2">
            <button
              @click="$emit('dismiss')"
              type="button"
              class="text-sm font-medium text-red-800 hover:text-red-600 transition-colors"
            >
              Dismiss
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { AlertCircle } from 'lucide-vue-next'

interface Props {
  error?: string | null
  title?: string
  dismissible?: boolean
}

withDefaults(defineProps<Props>(), {
  error: null,
  title: '',
  dismissible: false,
})

defineEmits<{
  (e: 'dismiss'): void
}>()
</script>
