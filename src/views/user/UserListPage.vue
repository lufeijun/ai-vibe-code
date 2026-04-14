<template>
  <div class="user-list-page">
    <div class="page-header">
      <h2 class="page-title">用户列表</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus">新增用户</el-button>
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
          <el-table-column prop="roles" label="角色" width="120">
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
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" :icon="Edit">编辑</el-button>
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                @click="handleDelete(row)"
              >删除</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import {
  Plus,
  Refresh,
  Search,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 用户查询参数接口
interface UserQueryParams {
  username?: string
  email?: string
  phone?: string
  pageNum: number
  pageSize: number
}

// 用户数据接口
interface User {
  id: number
  username: string
  email: string
  phone: string
  role: string
  status: string
  createTime: string
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
  phone: ''
})

// 加载状态
const loading = ref(false)

// 用户列表数据
const userList = ref<User[]>([])

// 分页配置
const pagination = reactive<PaginationInfo>({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

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

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params: UserQueryParams = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    // 只添加有值的参数
    if (searchForm.username.trim()) {
      params.username = searchForm.username.trim()
    }
    if (searchForm.email.trim()) {
      params.email = searchForm.email.trim()
    }
    if (searchForm.phone.trim()) {
      params.phone = searchForm.phone.trim()
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

// 删除用户
const handleDelete = (row: User) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用删除API
    ElMessage.success('删除成功')
    fetchUserList()
  }).catch(() => {
    // 取消删除
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchUserList()
})

// 监听分页变化（可选，用于调试）
watch(() => pagination.pageNum, (newVal) => {
  console.log('当前页:', newVal)
})

watch(() => pagination.pageSize, (newVal) => {
  console.log('每页条数:', newVal)
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
