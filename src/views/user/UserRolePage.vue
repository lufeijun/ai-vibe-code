<template>
  <div class="user-role-page">
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus">新增角色</el-button>
        <el-button type="success" :icon="Refresh">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <!-- 角色表格 -->
        <el-table :data="roleList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="角色名称" width="150" />
          <el-table-column prop="code" label="角色代码" width="120" />
          <el-table-column prop="description" label="角色描述" min-width="200" />
          <el-table-column prop="userCount" label="用户数量" width="100" align="center" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-switch
                v-model="row.status"
                :active-value="true"
                :inactive-value="false"
                @change="handleStatusChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="250" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button
                type="success"
                size="small"
                :icon="Setting"
                @click="handlePermission(row)"
              >权限</el-button>
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

    <!-- 权限配置对话框（示例） -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限配置"
      width="600px"
      destroy-on-close
    >
      <div class="permission-dialog">
        <el-tree
          :data="permissionTree"
          show-checkbox
          node-key="id"
          :default-checked-keys="defaultCheckedKeys"
          :props="treeProps"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePermission">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  Plus,
  Refresh,
  Edit,
  Delete,
  Setting
} from '@element-plus/icons-vue'

// 角色列表数据（模拟数据）
const roleList = ref([
  {
    id: 1,
    name: '超级管理员',
    code: 'admin',
    description: '拥有所有权限，可管理系统所有功能',
    userCount: 1,
    status: true,
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    name: '普通管理员',
    code: 'manager',
    description: '拥有部分管理权限，可管理用户和内容',
    userCount: 5,
    status: true,
    createTime: '2024-01-02 14:30:00'
  },
  {
    id: 3,
    name: '普通用户',
    code: 'user',
    description: '仅拥有基本操作权限',
    userCount: 100,
    status: true,
    createTime: '2024-01-03 09:15:00'
  },
  {
    id: 4,
    name: '访客',
    code: 'guest',
    description: '只读权限，不可进行任何修改操作',
    userCount: 50,
    status: false,
    createTime: '2024-01-04 16:45:00'
  }
])

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 4
})

// 权限对话框相关
const permissionDialogVisible = ref(false)
const permissionTree = ref([
  {
    id: 1,
    label: '用户管理',
    children: [
      { id: 11, label: '用户列表' },
      { id: 12, label: '角色管理' },
      { id: 13, label: '权限分配' }
    ]
  },
  {
    id: 2,
    label: '系统设置',
    children: [
      { id: 21, label: '系统配置' },
      { id: 22, label: '操作日志' },
      { id: 23, label: '数据备份' }
    ]
  },
  {
    id: 3,
    label: '内容管理',
    children: [
      { id: 31, label: '文章管理' },
      { id: 32, label: '分类管理' },
      { id: 33, label: '评论管理' }
    ]
  }
])
const defaultCheckedKeys = ref([11, 12, 21, 31])
const treeProps = {
  children: 'children',
  label: 'label'
}

// 状态变更
const handleStatusChange = (row: any) => {
  console.log('角色状态变更:', row.id, row.status)
  // TODO: 调用API更新状态
}

// 编辑角色
const handleEdit = (row: any) => {
  console.log('编辑角色:', row)
  // TODO: 打开编辑对话框
}

// 配置权限
const handlePermission = (row: any) => {
  console.log('配置权限:', row)
  permissionDialogVisible.value = true
  // TODO: 根据角色加载权限数据
}

// 保存权限配置
const handleSavePermission = () => {
  console.log('保存权限配置')
  permissionDialogVisible.value = false
  // TODO: 调用API保存权限
}

// 删除角色
const handleDelete = (row: any) => {
  console.log('删除角色:', row)
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
.user-role-page {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.permission-dialog {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
}
</style>