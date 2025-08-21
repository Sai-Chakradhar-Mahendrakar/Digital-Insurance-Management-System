<template>
  <button :class="buttonClasses" :disabled="disabled" @click="$emit('click', $event)" :type="type">
    <slot />
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'ghost'
  size?: 'small' | 'medium' | 'large'
  disabled?: boolean
  type?: 'button' | 'submit' | 'reset'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'medium',
  disabled: false,
  type: 'button',
})

const emit = defineEmits<{
  (e: 'click', event: MouseEvent): void
}>()

const buttonClasses = computed(() => {
  const baseClasses =
    'font-medium transition-all duration-200 ease-in-out focus:outline-none focus:ring-4 disabled:opacity-50 disabled:cursor-not-allowed'

  const variantClasses = {
    primary:
      'bg-blue-700 hover:bg-blue-800 text-white shadow-sm hover:shadow-md focus:ring-blue-200',
    secondary: 'border-2 border-blue-700 text-blue-700 hover:bg-blue-700 hover:text-white',
    ghost: 'text-slate-600 hover:text-blue-700 hover:bg-blue-50',
  }

  const sizeClasses = {
    small: 'px-4 py-2 text-sm rounded-lg',
    medium: 'px-6 py-3 text-base rounded-lg',
    large: 'px-8 py-4 text-lg rounded-xl',
  }

  return [baseClasses, variantClasses[props.variant], sizeClasses[props.size]].join(' ')
})
</script>
