import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import QueryForm from '../views/QueryForm.vue'
import { useAuthStore } from '../store/auth'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { 
    path: '/query', 
    component: QueryForm,
    beforeEnter: (to, from, next) => {
      const auth = useAuthStore()
      if(auth.isLoggedIn) next()
      else next('/login')
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
