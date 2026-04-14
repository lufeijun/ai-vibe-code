<template>
  <div class="dashboard-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <div class="logo">后台管理系统</div>
        <!-- 一级菜单 (level = 1) -->
        <el-menu
          v-if="topMenus.length > 0"
          class="top-menu"
          mode="horizontal"
          :ellipsis="false"
          :default-active="activeTopMenu"
          @select="handleTopMenuSelect"
        >
          <el-menu-item
            v-for="menu in topMenus"
            :key="menu.id"
            :index="menu.path"
          >
            {{ menu.name }}
          </el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <!-- 用户信息 -->
        <div class="user-info">
          <el-avatar :size="32" :src="avatarUrl">{{ userInitial }}</el-avatar>
          <span class="username">{{ authStore.userInfo.username || '管理员' }}</span>
        </div>
        <!-- 退出按钮 -->
        <el-button link class="logout-btn" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </el-header>

    <div class="main-container">
      <!-- 左侧二级菜单 (level = 2 和 level = 3) -->
      <el-aside class="sidebar" width="200px">
        <el-menu
          v-if="sideMenus.length > 0"
          class="side-menu"
          :default-active="activeSideMenu"
          @select="handleSideMenuSelect"
        >
          <template v-for="item in sideMenus" :key="item.id">
            <!-- level = 2: 有子菜单的使用 el-sub-menu -->
            <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.id.toString()">
              <template #title>
                <el-icon v-if="getIcon(item.icon)">
                  <component :is="getIcon(item.icon)" />
                </el-icon>
                <span>{{ item.name }}</span>
              </template>
              <!-- level = 3: 叶子节点使用 el-menu-item -->
              <el-menu-item
                v-for="child in item.children"
                :key="child.id"
                :index="child.path || child.id.toString()"
              >
                <el-icon v-if="getIcon(child.icon)">
                  <component :is="getIcon(child.icon)" />
                </el-icon>
                <span>{{ child.name }}</span>
              </el-menu-item>
            </el-sub-menu>
            <!-- level = 2: 没有子菜单的直接使用 el-menu-item -->
            <el-menu-item v-else :index="item.path || item.id.toString()">
              <el-icon v-if="getIcon(item.icon)">
                <component :is="getIcon(item.icon)" />
              </el-icon>
              <span>{{ item.name }}</span>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useAuthStore, type PermissionTreeDTO } from '@/stores/auth'
import {
  Setting,
  Monitor,
  User as UserIcon,
  Document,
  Lock,
  UserFilled,
  List,
  Management
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 图标映射
const iconMap: Record<string, any> = {
  'user-management': Management,
  'user-center': UserFilled,
  'user-list': List,
  'role-list': Lock,
  'setting': Setting,
  'monitor': Monitor,
  'document': Document,
  'user': UserIcon
}

const getIcon = (iconName: string | undefined) => {
  if (!iconName) return undefined
  return iconMap[iconName]
}

// 在组件挂载时获取权限树
onMounted(async () => {
  if (authStore.isAuthenticated) {
    await authStore.fetchPermissionTree()
  }
})

// 监听认证状态变化
watch(() => authStore.isAuthenticated, async (newVal) => {
  if (newVal) {
    await authStore.fetchPermissionTree()
  }
})

// 顶部一级菜单 (level = 1)
const topMenus = computed(() => {
  return authStore.permissionTree
    .filter(item => item.level === 1 && item.enabled)
    .sort((a, b) => a.sortOrder - b.sortOrder)
})

// 当前激活的一级菜单（根据路由自动计算）
const activeTopMenu = computed(() => {
  const path = route.path
  console.log('当前路由:', path)
  console.log('所有一级菜单:', topMenus.value)

  // 先检查是否有匹配的菜单 path
  for (const menu of topMenus.value) {
    if (menu.path && path.startsWith(menu.path)) {
      console.log('匹配到菜单:', menu.path)
      return menu.path
    }
  }

  // 如果没有匹配的，返回第一个菜单
  if (topMenus.value.length > 0) {
    console.log('使用第一个菜单:', topMenus.value[0].path)
    return topMenus.value[0].path
  }

  console.log('使用默认路径: /dashboard')
  return '/dashboard'
})

// 当前激活的一级菜单数据
const activeTopMenuData = computed(() => {
  return topMenus.value.find(menu =>
    menu.path && menu.path === activeTopMenu.value
  )
})

// 左侧菜单 (level = 2 和 level = 3)
const sideMenus = computed(() => {
  if (!activeTopMenuData.value) return []

  // 获取当前一级菜单的子菜单 (level = 2)
  const level2Menus = (activeTopMenuData.value.children || [])
    .filter(item => item.level === 2 && item.enabled)
    .sort((a, b) => a.sortOrder - b.sortOrder)

  // 处理 level 2 菜单的子菜单 (level = 3)
  return level2Menus.map(menu => ({
    ...menu,
    children: (menu.children || [])
      .filter(item => item.level === 3 && item.enabled)
      .sort((a, b) => a.sortOrder - b.sortOrder)
  }))
})

// 当前激活的二级菜单路径
const activeSideMenu = computed(() => route.path)

// 用户头像相关
const avatarUrl = ref('')
const userInitial = computed(() => {
  const username = authStore.userInfo.username || '管理员'
  return username.charAt(0).toUpperCase()
})

// 处理一级菜单选择
const handleTopMenuSelect = (index: string) => {
  console.log('点击菜单, index:', index)

  // 直接使用 index 作为路径跳转
  if (index.startsWith('/')) {
    console.log('直接跳转:', index)
    router.push(index)
  }
}

// 处理二级菜单选择
const handleSideMenuSelect = (index: string) => {
  if (index.startsWith('/')) {
    router.push(index)
  }
}

// 退出登录
const handleLogout = () => {
  authStore.logout()
  ElMessage.info('已退出登录')
  router.push('/login')
}
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
  min-width: 0;
  flex: 1;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.top-menu {
  background: transparent;
  border-bottom: none;
  flex: 1;
  min-width: 0;
}

/* 强制所有菜单项显示，禁用省略号 */
.top-menu :deep(.el-menu--horizontal) {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  overflow-y: hidden;
  border-bottom: none;
}

/* 隐藏滚动条 */
.top-menu :deep(.el-menu--horizontal::-webkit-scrollbar) {
  display: none;
}

.top-menu :deep(.el-menu-item) {
  color: #ffffff;
  font-size: 16px;
  font-weight: 500;
  white-space: nowrap;
  text-overflow: clip;
  overflow: visible;
  float: none;
  display: inline-block;
  flex-shrink: 0;
}

.top-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
}

.top-menu :deep(.el-menu-item.is-active) {
  color: #ffffff;
  font-weight: 600;
  border-bottom: 2px solid #1890ff;
  background: rgba(255, 255, 255, 0.1);
}

/* 确保菜单项内部文字也是白色 */
.top-menu :deep(.el-menu-item .el-menu-item__content),
.top-menu :deep(.el-menu-item span) {
  color: #ffffff;
}

.top-menu :deep(.el-menu-item.is-active .el-menu-item__content),
.top-menu :deep(.el-menu-item.is-active span) {
  color: #ffffff;
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

.side-menu :deep(.el-menu-item),
.side-menu :deep(.el-sub-menu__title) {
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
