import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { clickOutside } from './directives/clickOutside'
import { useAppStore } from './stores/app'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

const appStore = useAppStore()
appStore.initialize()

app.directive('click-outside', clickOutside)

app.mount('#app')
