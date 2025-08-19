<template>
    <header
      class="bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200 dark:border-slate-700 sticky top-0 z-50 px-4 lg:px-8 py-4"
    >
      <nav class="max-w-7xl mx-auto flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <span class="font-poppins text-xl font-semibold text-slate-800 dark:text-slate-100">
            Insaurance Management System
          </span>
        </div>
        <ul class="hidden md:flex space-x-8">
          <li>
            <RouterLink
              to="/"
              class="text-slate-600 dark:text-slate-300 hover:text-blue-700 relative after:w-0 hover:after:w-full after:h-0.5 after:bg-blue-700 after:absolute after:bottom-0 after:left-0 after:transition-all after:duration-200"
            >
              Home
            </RouterLink>
          </li>
          <li>
            <RouterLink
              to="/about"
              class="text-slate-600 dark:text-slate-300 hover:text-blue-700 relative after:w-0 hover:after:w-full after:h-0.5 after:bg-blue-700 after:absolute after:bottom-0 after:left-0 after:transition-all after:duration-200"
            >
              About
            </RouterLink>
          </li>
        </ul>
  
        <!-- CTA Button -->
        <RouterLink
          to="/signup"
          class="hidden md:inline-block bg-blue-700 hover:bg-blue-800 text-white font-medium px-6 py-3 rounded-lg shadow-sm hover:shadow-md transition-all duration-200 ease-in-out focus:ring-4 focus:ring-blue-200"
        >
          Get Started
        </RouterLink>
  
        <!-- Mobile Menu Button -->
        <button
          @click="toggleMobileMenu"
          class="md:hidden text-slate-600 dark:text-slate-300 focus:outline-none"
        >
          <MenuIcon class="w-6 h-6" />
        </button>
      </nav>
  
      <!-- Mobile Menu -->
      <transition name="fade">
        <div v-if="mobileMenuOpen" class="md:hidden mt-4 space-y-4 px-4">
          <RouterLink
            to="/"
            class="block text-slate-600 dark:text-slate-300 hover:text-blue-700"
            @click="toggleMobileMenu"
          >
            Home
          </RouterLink>
          <RouterLink
            to="/about"
            class="block text-slate-600 dark:text-slate-300 hover:text-blue-700"
            @click="toggleMobileMenu"
          >
            About
          </RouterLink>
          <RouterLink
            to="/signup"
            class="block bg-blue-700 hover:bg-blue-800 text-white font-medium px-6 py-3 rounded-lg shadow-sm hover:shadow-md transition-all"
            @click="toggleMobileMenu"
          >
            Get Started
          </RouterLink>
        </div>
      </transition>
    </header>
  </template>
  
  <script setup>
  import { ref } from "vue"
  import { MenuIcon } from "lucide-vue-next"
  
  const mobileMenuOpen = ref(false)
  const toggleMobileMenu = () => {
    mobileMenuOpen.value = !mobileMenuOpen.value
  }
  </script>
  
  <style scoped>
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.2s ease;
  }
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
  </style>
  