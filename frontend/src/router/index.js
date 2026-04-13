import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/Home.vue'
import LoginView from '../views/Login.vue'
import RegisterView from '../views/Register.vue'
import ProfileView from '../views/Profile.vue'
import ReservationsView from '../views/Reservations.vue'
import GuideView from '../views/Guide.vue'
import AboutView from '../views/About.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import AdminRoomsView from '../views/admin/Rooms.vue'
import AdminBookingsView from '../views/admin/Bookings.vue'
import AdminUsersView from '../views/admin/Users.vue'
import AdminAnnouncementsView from '../views/admin/Announcements.vue'
import AdminSettingsView from '../views/admin/Settings.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: () => {
        const token = localStorage.getItem('token')
        return token ? '/home' : '/login'
      }
    },
    {
      path: '/home',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true }
    },
    {
      path: '/reservations',
      name: 'reservations',
      component: ReservationsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/guide',
      name: 'guide',
      component: GuideView
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          redirect: '/admin/rooms'
        },
        {
          path: 'rooms',
          name: 'admin-rooms',
          component: AdminRoomsView
        },
        {
          path: 'bookings',
          name: 'admin-bookings',
          component: AdminBookingsView
        },
        {
          path: 'users',
          name: 'admin-users',
          component: AdminUsersView
        },
        {
          path: 'announcements',
          name: 'admin-announcements',
          component: AdminAnnouncementsView
        },
        {
          path: 'settings',
          name: 'admin-settings',
          component: AdminSettingsView
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')

  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && userRole !== 'admin') {
    return next('/home')
  }

  // 已登录用户访问登录页，跳转到首页
  if (to.path === '/login' && token) {
    return next('/home')
  }

  next()
})

export default router
