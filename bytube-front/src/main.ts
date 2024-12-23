/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Plugins
import { registerPlugins } from '@/plugins'

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'
import { useAuthStore } from './stores/auth'

const app = createApp(App)

registerPlugins(app)

// Initialize authentication before mounting the app
const authStore = useAuthStore();
authStore.initialize().finally(() => {
  app.mount('#app');
});

