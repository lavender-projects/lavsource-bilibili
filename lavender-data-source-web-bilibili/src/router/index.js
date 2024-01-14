import { createRouter, createWebHistory } from 'vue-router'
import SettingsView from '@/views/SettingsView.vue'
import settingsRoutes from '@/router/routes/settings'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/settings'
    },
    {
      path: '/settings',
      component: SettingsView
    },
    ...settingsRoutes
  ]
})

router.beforeEach((to, from, next) => {
  if(to.path === '/') {
    next('/settings')
    return
  }
  next()
})

export default router
