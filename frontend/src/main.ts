import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { clickOutside } from './directives/clickOutside'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.directive('click-outside', clickOutside)
app.use(createPinia())
app.use(router)
app.mount('#app')
