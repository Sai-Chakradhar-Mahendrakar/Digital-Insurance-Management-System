<template>
    <div class="title-container">
      <header
        class="bg-white dark:bg-slate-800 border-b border-slate-200 dark:border-slate-700 px-4 lg:px-8 py-4 flex items-center justify-between"
      >
        <div class="flex items-center space-x-4">
          <router-link
            to="/"
            class="px-4 py-2 bg-blue-700 hover:bg-blue-800 text-white font-medium rounded-lg transition inline-block text-decoration-none"
          >
            Insurance Management System
          </router-link>
          <SearchBar 
            placeholder="Search..." 
            @search="handleSearch"
            class="w-64"
          />
          <button
            class="px-4 py-2 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-200 font-medium rounded-lg transition"
          >
            Policy
          </button>
        </div>
  
        <div class="flex items-center space-x-4">
          <DropdownWrapper
            :trigger="`<button class='px-4 py-2 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-200 font-medium rounded-lg transition'>Notifications</button>`"
            :items="notificationItems"
            width="w-80"
          />
  
          <DropdownWrapper
            :trigger="`<button class='w-10 h-10 flex items-center justify-center rounded-full bg-blue-700 text-white font-bold hover:bg-blue-800 transition'>U</button>`"
            :items="userMenuItems"
          />
        </div>
      </header>
    </div>
  </template>
  
  <script setup>
  import SearchBar from '@/components/SearchBar.vue'
  import DropdownWrapper from '@/components/dropdown/DropdownWrapper.vue'
  
  const notificationItems = [
    { label: 'New message from Admin', onClick: () => console.log('Admin message') },
    { label: 'Your ticket was updated', onClick: () => console.log('Ticket updated') },
    { label: 'System maintenance tomorrow', onClick: () => console.log('Maintenance') }
  ]
  
  const userMenuItems = [
    { label: 'Edit Profile', onClick: () => console.log('Edit Profile') },
    { label: 'View Support Tickets', onClick: () => console.log('Support Tickets') },
    { label: 'Logout', onClick: () => console.log('Logout') }
  ]
  
  const handleSearch = (query) => {
    console.log('Search query:', query)
  }
  </script>