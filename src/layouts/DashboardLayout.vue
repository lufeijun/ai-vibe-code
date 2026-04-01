<template>
  <div class="dashboard-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <div class="logo">后台管理系统</div>
        <!-- 一级菜单 -->
        <el-menu
          class="top-menu"
          mode="horizontal"
          :default-active="activeTopMenu"
          @select="handleTopMenuSelect"
        >
          <el-menu-item index="/dashboard">仪表盘</el-menu-item>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/system">系统设置</el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <!-- 用户信息 -->
        <div class="user-info">
          <el-avatar :size="32" :src="avatarUrl">{{ userInitial }}</el-avatar>
          <span class="username">{{ authStore.userInfo.username || '管理员' }}</span>
        </div>
        <!-- 退出按钮 -->
        <el-button type="text" class="logout-btn" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </el-header>

    <div class="main-container">
      <!-- 左侧二级菜单 -->
      <el-aside class="sidebar" width="200px">
        <el-menu
          class="side-menu"
          :default-active="activeSideMenu"
          @select="handleSideMenuSelect"
        >
          <template v-for="item in sideMenus" :key="item.index">
            <el-menu-item :index="item.index">
              <el-icon v-if="item.icon">
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ref, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import {
  Setting,
  Monitor,
  User as UserIcon,
  Document,
  Lock
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 当前激活的一级菜单（根据路由自动计算）
const activeTopMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/dashboard')) return '/dashboard'
  if (path.startsWith('/user')) return '/user'
  if (path.startsWith('/system')) return '/system'
  return '/dashboard' // 默认
})

// 当前激活的二级菜单路径
const activeSideMenu = computed(() => route.path)

// 页面标题（从路由元数据获取）
const pageTitle = computed(() => route.meta?.title || '后台管理系统')

// 二级菜单配置
interface SideMenuItem {
  index: string
  title: string
  icon: any
}

const sideMenuConfig: Record<string, SideMenuItem[]> = {
  '/dashboard': [
    { index: '/dashboard/overview', title: '概览', icon: Monitor },
    { index: '/dashboard/stats', title: '统计', icon: Document }
  ],
  '/user': [
    { index: '/user/list', title: '用户列表', icon: UserIcon },
    { index: '/user/role', title: '角色管理', icon: Lock }
  ],
  '/system': [
    { index: '/system/settings', title: '系统设置', icon: Setting },
    { index: '/system/logs', title: '操作日志', icon: Document }
  ]
}

// 当前激活的二级菜单配置
const sideMenus = computed(() => sideMenuConfig[activeTopMenu.value] || [])

// 用户头像相关
const avatarUrl = ref('')
const userInitial = computed(() => {
  const username = authStore.userInfo.username || '管理员'
  return username.charAt(0).toUpperCase()
})

// 处理一级菜单选择
const handleTopMenuSelect = (index: string) => {
  // 导航到该模块的第一个二级菜单
  if (sideMenuConfig[index] && sideMenuConfig[index].length > 0) {
    router.push(sideMenuConfig[index][0].index)
  } else {
    // 如果没有二级菜单，导航到一级菜单路径
    router.push(index)
  }
}

// 处理二级菜单选择
const handleSideMenuSelect = (index: string) => {
  router.push(index)
}

// 退出登录
const handleLogout = () => {
  authStore.logout()
  ElMessage.info('已退出登录')
  router.push('/login')
}

// 初始化：根据URL参数设置激活菜单（可选）
// 暂时简单设置为dashboard
</script>

<style scoped>
.dashboard-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏样式 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #001529;
  color: white;
  padding: 0 20px;
  height: 60px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.top-menu {
  background: transparent;
  border-bottom: none;
}

.top-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}

.top-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.top-menu :deep(.el-menu-item.is-active) {
  color: white;
  border-bottom: 2px solid #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: default;
}

.username {
  font-size: 14px;
}

.logout-btn {
  color: rgba(255, 255, 255, 0.8);
  padding: 8px 12px;
}

.logout-btn:hover {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧边栏 */
.sidebar {
  background: white;
  border-right: 1px solid #f0f0f0;
  overflow-y: auto;
}

.side-menu {
  border-right: none;
  height: 100%;
}

.side-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
}

.side-menu :deep(.el-menu-item.is-active) {
  background: #e6f7ff;
  color: #1890ff;
}

/* 主内容区域 */
.content {
  padding: 24px;
  overflow-y: auto;
  background: #f5f7fa;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  margin-bottom: 24px;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

.welcome-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.welcome-card p {
  margin-bottom: 12px;
  color: #666;
  line-height: 1.6;
}

.logout-section {
  text-align: center;
}
</style>