<template>
  <div class="user-list-page">
    <div class="page-header">
      <h2 class="page-title">用户列表</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus">新增用户</el-button>
        <el-button type="success" :icon="Refresh">刷新</el-button>
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
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="启用" value="active" />
              <el-option label="禁用" value="inactive" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search">搜索</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 用户表格 -->
        <el-table :data="userList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 'admin' ? 'danger' : ''" size="small">
                {{ row.role === 'admin' ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
                {{ row.status === 'active' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
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
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
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
import { ref, reactive } from 'vue'
import {
  Plus,
  Refresh,
  Search,
  Edit,
  Delete
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  username: '',
  status: ''
})

// 用户列表数据（模拟数据）
const userList = ref([
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    role: 'admin',
    status: 'active',
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    username: 'user1',
    email: 'user1@example.com',
    role: 'user',
    status: 'active',
    createTime: '2024-01-02 14:30:00'
  },
  {
    id: 3,
    username: 'user2',
    email: 'user2@example.com',
    role: 'user',
    status: 'inactive',
    createTime: '2024-01-03 09:15:00'
  }
])

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 3
})

// 搜索
const handleSearch = () => {
  console.log('搜索参数:', searchForm)
  // TODO: 调用API搜索
}

// 重置搜索
const handleReset = () => {
  searchForm.username = ''
  searchForm.status = ''
  handleSearch()
}

// 删除用户
const handleDelete = (row: any) => {
  console.log('删除用户:', row)
  // TODO: 调用API删除
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  console.log('每页大小改变:', size)
  // TODO: 重新获取数据
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.current = page
  console.log('当前页改变:', page)
  // TODO: 重新获取数据
}
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