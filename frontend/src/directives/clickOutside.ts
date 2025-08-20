// src/directives/clickOutside.ts
import type { DirectiveBinding } from 'vue'

interface ClickOutsideElement extends HTMLElement {
  _clickOutside?: (event: Event) => void
}

export const clickOutside = {
  mounted(el: ClickOutsideElement, binding: DirectiveBinding) {
    el._clickOutside = (event: Event) => {
      if (!(el === event.target || el.contains(event.target as Node))) {
        binding.value()
      }
    }
    document.addEventListener('click', el._clickOutside)
  },
  unmounted(el: ClickOutsideElement) {
    if (el._clickOutside) {
      document.removeEventListener('click', el._clickOutside)
      delete el._clickOutside
    }
  },
}
