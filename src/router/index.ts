import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginPage.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/layouts/DashboardLayout.vue'),
    meta: {
      title: '后台首页',
      requiresAuth: true
    },
    children: [
      {
        path: '',
        redirect: '/dashboard/overview'
      },
      {
        path: 'overview',
        name: 'DashboardOverview',
        component: () => import('@/views/dashboard/OverviewPage.vue'),
        meta: {
          title: '仪表盘概览',
          requiresAuth: true
        }
      },
      {
        path: 'stats',
        name: 'DashboardStats',
        component: () => import('@/views/dashboard/StatsPage.vue'),
        meta: {
          title: '数据统计',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/layouts/DashboardLayout.vue'),
    meta: {
      title: '用户管理',
      requiresAuth: true
    },
    children: [
      {
        path: '',
        redirect: '/user/list'
      },
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/UserListPage.vue'),
        meta: {
          title: '用户列表',
          requiresAuth: true
        }
      },
      {
        path: 'role',
        name: 'UserRole',
        component: () => import('@/views/user/UserRolePage.vue'),
        meta: {
          title: '角色管理',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/system',
    name: 'System',
    component: () => import('@/layouts/DashboardLayout.vue'),
    meta: {
      title: '系统设置',
      requiresAuth: true
    },
    children: [
      {
        path: '',
        redirect: '/system/settings'
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('@/views/system/SystemSettingsPage.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true
        }
      },
      {
        path: 'logs',
        name: 'SystemLogs',
        component: () => import('@/views/system/SystemLogsPage.vue'),
        meta: {
          title: '操作日志',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 后台管理系统`
  }

  // 检查是否需要认证
  const isAuthenticated = localStorage.getItem('isLoggedIn') === 'true'

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router