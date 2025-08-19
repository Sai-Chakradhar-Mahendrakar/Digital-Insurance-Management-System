<template>
  <div class="relative" ref="dropdownRef">
    <div @click="toggle">
      <slot name="trigger">
        <button v-html="trigger"></button>
      </slot>
    </div>

    <div
      v-if="isOpen"
      :class="[
        'absolute mt-2 bg-white dark:bg-slate-800 rounded-lg shadow-lg border border-slate-200 dark:border-slate-700 z-50',
        position === 'left' ? 'left-0' : 'right-0',
        width,
      ]"
    >
      <div class="py-2">
        <button
          v-for="item in items"
          :key="item.label"
          @click="handleItemClick(item)"
          class="w-full text-left px-4 py-2 text-sm text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-700"
        >
          {{ item.label }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  trigger: {
    type: String,
    default: ''
  },
  items: {
    type: Array,
    default: () => []
  },
  position: {
    type: String,
    default: 'right',
    validator: (value) => ['left', 'right'].includes(value),
  },
  width: {
    type: String,
    default: 'w-48',
  },
})

const emit = defineEmits(['toggle', 'item-click'])

const isOpen = ref(false)
const dropdownRef = ref(null)

const toggle = () => {
  isOpen.value = !isOpen.value
  emit('toggle', isOpen.value)
}

const close = () => {
  isOpen.value = false
  emit('toggle', false)
}

const handleItemClick = (item) => {
  if (item.onClick) {
    item.onClick()
  }
  emit('item-click', item)
  close()
}

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    close()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

defineExpose({ close })
</script>