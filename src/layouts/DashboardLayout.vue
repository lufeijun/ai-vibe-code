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
        <div class="user-info" @click="handleUserInfoClick">
          <el-avatar :size="32" :src="avatarUrl">{{ userInitial }}</el-avatar>
          <span class="username">{{ authStore.userInfo.username || '管理员' }}</span>
        </div>
        <!-- 退出按钮 -->
        <el-button link class="logout-btn" @click="handleLogout">
          退出登录
        </el-button>
      </div>

      <!-- 用户信息弹窗 -->
      <el-dialog
        v-model="userInfoDialogVisible"
        title="用户信息"
        width="500px"
        @close="resetUserInfoForm"
      >
        <el-form
          ref="userInfoFormRef"
          :model="userInfoForm"
          :rules="userInfoRules"
          label-width="100px"
        >
          <el-form-item label="用户名">
            <el-input v-model="userInfoForm.username" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="userInfoForm.email" disabled />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userInfoForm.phone" />
          </el-form-item>
          <el-form-item label="城市">
            <el-input v-model="userInfoForm.city" />
          </el-form-item>
          <el-form-item label="角色">
            <el-tag
              v-for="role in userInfoForm.roles"
              :key="role.id"
              type="primary"
              style="margin-right: 8px; margin-bottom: 4px;"
            >
              {{ role.name }}
            </el-tag>
            <span v-if="!userInfoForm.roles || userInfoForm.roles.length === 0" class="text-gray">
              暂无角色
            </span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="handleOpenChangePassword">修改密码</el-button>
          <el-button @click="userInfoDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveUserInfo" :loading="savingUserInfo">
            保存
          </el-button>
        </template>
      </el-dialog>

      <!-- 修改密码弹窗 -->
      <el-dialog
        v-model="passwordDialogVisible"
        title="修改密码"
        width="400px"
        @close="resetPasswordForm"
      >
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
        >
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              show-password
              placeholder="请输入新密码"
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              show-password
              placeholder="请再次输入新密码"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePassword" :loading="savingPassword">
            确认修改
          </el-button>
        </template>
      </el-dialog>
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
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ref, computed, onMounted, watch } from 'vue'
import { useAuthStore, type PermissionTreeDTO, type RoleDTO } from '@/stores/auth'
import {
  Setting,
  Monitor,
  User as UserIcon,
  Document,
  Lock,
  UserFilled,
  List,
  Basketball,
  DataBoard,
  Management
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 用户信息弹窗相关
const userInfoDialogVisible = ref(false)
const userInfoFormRef = ref<FormInstance>()
const savingUserInfo = ref(false)
const userInfoForm = ref({
  username: '',
  email: '',
  phone: '',
  city: '',
  roles: [] as RoleDTO[]
})
const userInfoRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 修改密码弹窗相关
const passwordDialogVisible = ref(false)
const passwordFormRef = ref<FormInstance>()
const savingPassword = ref(false)
const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
})
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}
const passwordRules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 图标映射
const iconMap: Record<string, any> = {
  'user-management': Management,
  'user-center': UserFilled,
  'user-list': List,
  'role-list': Lock,
  'setting': Setting,
  'monitor': Monitor,
  'document': Document,
  'Basketball': Basketball,
  'DataBoard': DataBoard,
  'user': UserIcon
}

const getIcon = (iconName: string | undefined) => {
  if (!iconName) return undefined
  return iconMap[iconName]
}

// 在组件挂载时获取权限树和用户信息
onMounted(async () => {
  if (authStore.isAuthenticated) {
    await authStore.fetchPermissionTree()
    // 同时获取完整用户信息
    if (!authStore.fullUserInfo) {
      await authStore.fetchFullUserInfo()
    }
  }
})

// 用户信息相关方法
const handleUserInfoClick = async () => {
  // 优先使用已有的完整用户信息
  if (authStore.fullUserInfo) {
    userInfoForm.value = {
      username: authStore.fullUserInfo.username,
      email: authStore.fullUserInfo.email,
      phone: authStore.fullUserInfo.phone,
      city: authStore.fullUserInfo.city || '',
      roles: authStore.fullUserInfo.roles || []
    }
  } else {
    // 否则使用基本信息
    userInfoForm.value = {
      username: authStore.userInfo.username,
      email: authStore.userInfo.email,
      phone: authStore.userInfo.phone,
      city: '',
      roles: []
    }
  }

  // 打开弹窗
  userInfoDialogVisible.value = true

  // 异步获取最新的完整用户信息
  try {
    await authStore.fetchFullUserInfo()
    // 更新表单
    if (authStore.fullUserInfo) {
      userInfoForm.value = {
        username: authStore.fullUserInfo.username,
        email: authStore.fullUserInfo.email,
        phone: authStore.fullUserInfo.phone,
        city: authStore.fullUserInfo.city || '',
        roles: authStore.fullUserInfo.roles || []
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const resetUserInfoForm = () => {
  userInfoFormRef.value?.resetFields()
}

const handleSaveUserInfo = async () => {
  if (!userInfoFormRef.value) return

  await userInfoFormRef.value.validate(async (valid) => {
    if (valid) {
      savingUserInfo.value = true
      try {
        const success = await authStore.updateUserInfo({
          username: userInfoForm.value.username,
          phone: userInfoForm.value.phone,
          city: userInfoForm.value.city
        })

        if (success) {
          ElMessage.success('用户信息更新成功')
          userInfoDialogVisible.value = false
        } else {
          ElMessage.error('用户信息更新失败')
        }
      } catch (error) {
        ElMessage.error('用户信息更新失败')
      } finally {
        savingUserInfo.value = false
      }
    }
  })
}

const handleOpenChangePassword = () => {
  passwordDialogVisible.value = true
}

const resetPasswordForm = () => {
  passwordForm.value = {
    newPassword: '',
    confirmPassword: ''
  }
  passwordFormRef.value?.resetFields()
}

const handleSavePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      savingPassword.value = true
      try {
        const result = await authStore.changePassword(
          authStore.userInfo.id,
          passwordForm.value.newPassword
        )

        if (result.success) {
          ElMessage.success(result.message)
          passwordDialogVisible.value = false
        } else {
          ElMessage.error(result.message)
        }
      } catch (error) {
        ElMessage.error('密码修改失败')
      } finally {
        savingPassword.value = false
      }
    }
  })
}

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
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.text-gray {
  color: #999;
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
