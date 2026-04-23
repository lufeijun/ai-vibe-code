<template>
  <div class="user-list-page">
    <div class="page-header">
      <h2 class="page-title">用户列表</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
        <el-button type="success" :icon="Refresh" @click="fetchUserList">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <!-- 搜索表单 -->
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="用户名">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input
              v-model="searchForm.email"
              placeholder="请输入邮箱"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input
              v-model="searchForm.phone"
              placeholder="请输入手机号"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="在职状态">
            <el-select
              v-model="searchForm.isEmployed"
              placeholder="请选择"
              clearable
              style="width: 120px"
            >
              <el-option label="在职" :value="true" />
              <el-option label="离职" :value="false" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 用户表格 -->
        <el-table v-loading="loading" :data="userList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column prop="city" label="城市" width="100" />
          <el-table-column prop="isEmployed" label="在职状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.isEmployed ? 'success' : 'danger'" size="small">
                {{ row.isEmployed ? '在职' : '离职' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="hireDate" label="入职日期" width="120">
            <template #default="{ row }">
              {{ row.hireDate || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="resignationDate" label="离职日期" width="120">
            <template #default="{ row }">
              {{ row.resignationDate || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="roles" label="角色" width="150">
            <template #default="{ row }">
              <el-tag v-for="role in row.roles" :key="role.id" :type="role.code === 'admin' ? 'danger' : 'info'" size="small" style="margin-right: 4px;">
                {{ role.name }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="warning" size="small" @click="handleChangePassword(row)">修改密码</el-button>
              <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="用户名" prop="username" v-if="!isEdit">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码（默认123456）" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="formData.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="在职状态" prop="isEmployed">
          <el-switch v-model="formData.isEmployed" active-text="在职" inactive-text="离职" />
        </el-form-item>
        <el-form-item label="入职日期" prop="hireDate">
          <el-date-picker
            v-model="formData.hireDate"
            type="date"
            placeholder="请选择入职日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="离职日期" prop="resignationDate">
          <el-date-picker
            v-model="formData.resignationDate"
            type="date"
            placeholder="请选择离职日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="formData.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in allRoles"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改用户密码"
      width="400px"
      :close-on-click-modal="false"
      @close="resetPasswordForm"
    >
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="passwordForm.username" disabled />
        </el-form-item>
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
        <el-button type="primary" @click="handlePasswordSubmit" :loading="passwordSubmitLoading">
          确定修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Refresh, Search, Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'

// 角色接口
interface Role {
  id: number
  name: string
  code: string
}

// 用户数据接口
interface User {
  id: number
  username: string
  email: string
  phone: string
  city: string
  isEmployed: boolean
  hireDate: string
  resignationDate: string
  roles: Role[]
  createdAt: string
}

// 用户查询参数接口
interface UserQueryParams {
  username?: string
  email?: string
  phone?: string
  isEmployed?: boolean
  pageNum: number
  pageSize: number
}

// 分页信息接口
interface PaginationInfo {
  pageNum: number
  pageSize: number
  total: number
}

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
  phone: '',
  isEmployed: undefined as boolean | undefined
})

// 加载状态
const loading = ref(false)

// 用户列表数据
const userList = ref<User[]>([])

// 所有角色列表
const allRoles = ref<Role[]>([])

// 分页配置
const pagination = reactive<PaginationInfo>({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive({
  id: 0,
  username: '',
  password: '',
  email: '',
  phone: '',
  city: '',
  isEmployed: true,
  hireDate: '',
  resignationDate: '',
  roleIds: [] as number[]
})

// 表单验证规则
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 修改密码对话框相关
const passwordDialogVisible = ref(false)
const passwordSubmitLoading = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  userId: 0,
  username: '',
  newPassword: '',
  confirmPassword: ''
})
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== passwordForm.newPassword) {
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

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取所有角色
const fetchAllRoles = async () => {
  try {
    const res: any = await request.get('/role/list')
    if (res.code === 200) {
      allRoles.value = res.data || []
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params: UserQueryParams = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    if (searchForm.username.trim()) {
      params.username = searchForm.username.trim()
    }
    if (searchForm.email.trim()) {
      params.email = searchForm.email.trim()
    }
    if (searchForm.phone.trim()) {
      params.phone = searchForm.phone.trim()
    }
    if (searchForm.isEmployed !== undefined) {
      params.isEmployed = searchForm.isEmployed
    }

    const res: any = await request.post('/user/list', params)

    if (res.code === 200) {
      userList.value = res.data.records || []
      pagination.total = res.data.total || 0
      pagination.pageNum = res.data.current || 1
      pagination.pageSize = res.data.size || 10
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchUserList()
}

// 重置搜索
const handleReset = () => {
  searchForm.username = ''
  searchForm.email = ''
  searchForm.phone = ''
  searchForm.isEmployed = undefined
  pagination.pageNum = 1
  fetchUserList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchUserList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  fetchUserList()
}

// 新增用户
const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: 0,
    username: '',
    password: '',
    email: '',
    phone: '',
    city: '',
    isEmployed: true,
    hireDate: '',
    resignationDate: '',
    roleIds: []
  })
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row: User) => {
  isEdit.value = true
  try {
    const res: any = await request.get(`/user/${row.id}`)
    if (res.code === 200) {
      const user = res.data
      Object.assign(formData, {
        id: user.id,
        username: user.username,
        password: '',
        email: user.email || '',
        phone: user.phone || '',
        city: user.city || '',
        isEmployed: user.isEmployed !== false,
        hireDate: user.hireDate || '',
        resignationDate: user.resignationDate || '',
        roleIds: (user.roles || []).map((r: Role) => r.id)
      })
      dialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      let res: any
      const payload = { ...formData }

      if (!isEdit.value) {
        delete payload.id
      }

      if (isEdit.value) {
        res = await request.post('/user/update', payload)
      } else {
        res = await request.post('/user/create', payload)
      }

      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        fetchUserList()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 修改密码
const handleChangePassword = (row: User) => {
  passwordForm.userId = row.id
  passwordForm.username = row.username
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.userId = 0
  passwordForm.username = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.resetFields()
}

// 提交修改密码
const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    passwordSubmitLoading.value = true
    try {
      const res: any = await request.post('/user/change-password', {
        userId: passwordForm.userId,
        newPassword: passwordForm.newPassword
      })

      if (res.code === 200) {
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
      } else {
        ElMessage.error(res.message || '密码修改失败')
      }
    } catch (error) {
      console.error('密码修改失败:', error)
      ElMessage.error('密码修改失败')
    } finally {
      passwordSubmitLoading.value = false
    }
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchAllRoles()
  fetchUserList()
})
</script>

<style scoped>
.user-list-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

.page-actions {
  display: flex;
  gap: 10px;
}

.page-content {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.content-card {
  border: none;
  box-shadow: none;
}

.search-form {
  margin-bottom: 20px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
