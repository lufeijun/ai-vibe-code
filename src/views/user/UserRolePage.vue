<template>
  <div class="user-role-page">
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
        <el-button type="success" :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <!-- 搜索表单 -->
        <el-form :model="queryParams" class="search-form" inline>
          <el-form-item label="角色名称">
            <el-input v-model="queryParams.name" placeholder="请输入角色名称" clearable />
          </el-form-item>
          <el-form-item label="角色代码">
            <el-input v-model="queryParams.code" placeholder="请输入角色代码" clearable />
          </el-form-item>
          
          <el-form-item label="状态">
            <el-select  style="width: 100px" v-model="queryParams.enabled" placeholder="请选择状态" clearable>
              <el-option label="请选择" v-bind:value="-1" />
              <el-option label="启用" v-bind:value="1" />
              <el-option label="禁用" v-bind:value="2" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        
        <!-- 角色表格 -->
        <el-table :data="roleList" border stripe style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="角色名称" width="150" />
          <el-table-column prop="code" label="角色代码" width="120" />
          <el-table-column prop="description" label="角色描述" min-width="200" />
          <el-table-column prop="enabled" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.enabled ? 'success' : 'danger'">
                {{ row.enabled ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" />
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleFormRules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色代码" prop="code">
          <el-input v-model="roleForm.code" placeholder="请输入角色代码" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-switch v-model="roleForm.enabled" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSaveRole">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 权限配置对话框（示例） -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限配置"
      width="600px"
      destroy-on-close
    >
      <div class="permission-dialog">
        <el-tree
          ref="permissionTreeRef"
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
          <el-button type="primary" :loading="submitLoading" @click="handleSavePermission">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  Plus,
  Refresh,
  Edit,
  Delete,
  Setting,
  Search,
  RefreshRight
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

// 类型定义
interface RoleItem {
  id: number
  name: string
  code: string
  description: string
  enabled: boolean
  createdAt: string
  updatedAt: string
}

interface RoleQueryRequest {
  name?: string
  code?: string
  enabled?: boolean
  pageNum?: number
  pageSize?: number
}

interface RoleAddRequest {
  name: string
  code?: string
  description?: string
  enabled?: boolean
}

interface RoleUpdateRequest extends RoleAddRequest {
  id?: number
}

interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

interface PermissionTreeItem {
  id: number
  name: string
  label?: string
  children?: PermissionTreeItem[]
}

interface RoleAssignPermissionsRequest {
  roleId: number
  permissionIds: number[]
}

// 状态
const loading = ref(false)
const roleList = ref<RoleItem[]>([])
const submitLoading = ref(false)
const roleDialogVisible = ref(false)
const isEdit = ref(false)
const roleFormRef = ref<FormInstance>()
const editingRoleId = ref<number | null>(null)
const permissionTreeRef = ref()
const currentPermissionRole = ref<RoleItem | null>(null)

// 查询参数 - 使用数字类型处理状态
const queryParams = reactive<{
  name?: string
  code?: string
  enabled?: number
  pageNum?: number
  pageSize?: number
}>({
  name: '',
  code: '',
  enabled: -1,
  pageNum: 1,
  pageSize: 10
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 角色表单
const roleForm = reactive<RoleUpdateRequest>({
  id: undefined,
  name: '',
  code: '',
  description: '',
  enabled: true
})

// 表单验证规则
const roleFormRules: FormRules<RoleAddRequest> = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
}

// 获取角色列表
const fetchRoleList = async () => {
  loading.value = true
  try {
    // 构建请求参数，处理 enabled 字段（字符串转布尔值）
    const requestParams: RoleQueryRequest = {
      name: queryParams.name || undefined,
      code: queryParams.code || undefined,
      enabled:
        queryParams.enabled == -1 
          ? undefined
          : (queryParams.enabled === 1 ? true : false),
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    }
    const res = await request.post<{ code: number; message: string; data: PageResult<RoleItem> }>(
      '/role/list',
      requestParams
    )
    if (res.code === 200) {
      roleList.value = res.data.records
      pagination.total = res.data.total
      pagination.current = res.data.current
      pagination.size = res.data.size
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  pagination.current = 1
  fetchRoleList()
}

// 刷新：重置权限树缓存并刷新列表
const handleRefresh = () => {
  permissionTreeLoaded.value = false
  permissionTree.value = []
  fetchRoleList()
}

// 重置
const handleReset = () => {
  queryParams.name = ''
  queryParams.code = ''
  queryParams.enabled = -1
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  pagination.current = 1
  fetchRoleList()
}

const handleChange = (val) => {
  console.log('改变后的值:', val)
  console.log('值的类型:', typeof val)
  console.log('queryParams.enabled:', queryParams.enabled)
}

// 新增角色
const handleAdd = () => {
  isEdit.value = false
  editingRoleId.value = null
  roleForm.id = undefined
  roleForm.name = ''
  roleForm.code = ''
  roleForm.description = ''
  roleForm.enabled = true
  roleDialogVisible.value = true
}

// 编辑角色
const handleEdit = (row: RoleItem) => {
  isEdit.value = true
  editingRoleId.value = row.id
  roleForm.id = row.id
  roleForm.name = row.name
  roleForm.code = row.code
  roleForm.description = row.description
  roleForm.enabled = row.enabled
  roleDialogVisible.value = true
}

// 保存角色
const handleSaveRole = async () => {
  if (!roleFormRef.value) return

  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        let res
        if (isEdit.value && editingRoleId.value) {
          // 编辑模式 - 调用更新接口
          res = await request.post<{ code: number; message: string }>(
            `/role/update/${editingRoleId.value}`,
            roleForm
          )
          if (res.code === 200) {
            ElMessage.success('更新角色成功')
          }
        } else {
          // 新增模式 - 调用新增接口
          res = await request.post<{ code: number; message: string }>(
            '/role/add',
            roleForm
          )
          if (res.code === 200) {
            ElMessage.success('新增角色成功')
          }
        }
        if (res?.code === 200) {
          roleDialogVisible.value = false
          fetchRoleList()
        }
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新角色失败' : '新增角色失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 权限对话框相关
const permissionDialogVisible = ref(false)
const permissionTree = ref<PermissionTreeItem[]>([])
const permissionTreeLoaded = ref(false)
const defaultCheckedKeys = ref<number[]>([])
const treeProps = {
  children: 'children',
  label: 'name'
}

// 获取权限树
const fetchPermissionTree = async () => {
  // 如果已经加载过，直接返回
  if (permissionTreeLoaded.value && permissionTree.value.length > 0) {
    return
  }

  try {
    const res = await request.get<{ code: number; message: string; data: PermissionTreeItem[] }>(
      '/permission/tree'
    )
    if (res.code === 200) {
      // 确保每个节点都有 label 字段（兼容 el-tree）
      const addLabel = (items: PermissionTreeItem[]): PermissionTreeItem[] => {
        return items.map(item => ({
          ...item,
          label: item.name,
          children: item.children ? addLabel(item.children) : undefined
        }))
      }
      permissionTree.value = addLabel(res.data)
      permissionTreeLoaded.value = true
    }
  } catch (error) {
    ElMessage.error('获取权限树失败')
  }
}

// 状态变更
const handleStatusChange = (row: any) => {
  console.log('角色状态变更:', row.id, row.enabled)
  // TODO: 调用API更新状态
}

// 配置权限
const handlePermission = async (row: RoleItem) => {
  console.log('配置权限:', row)
  currentPermissionRole.value = row
  defaultCheckedKeys.value = []
  await fetchPermissionTree()
  permissionDialogVisible.value = true
  // TODO: 根据角色加载已选中的权限数据（调用获取角色权限接口）
}

// 保存权限配置
const handleSavePermission = async () => {
  if (!currentPermissionRole.value || !permissionTreeRef.value) {
    return
  }

  // 获取所有选中的节点 key
  const checkedKeys = permissionTreeRef.value.getCheckedKeys(false) as number[]
  // 获取半选中的节点 key（父节点）
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys() as number[]
  // 合并所有选中的权限 ID
  const allPermissionIds = [...checkedKeys, ...halfCheckedKeys]

  const requestData: RoleAssignPermissionsRequest = {
    roleId: currentPermissionRole.value.id,
    permissionIds: allPermissionIds
  }

  submitLoading.value = true
  try {
    const res = await request.post<{ code: number; message: string }>(
      '/role/assign-permissions',
      requestData
    )
    if (res.code === 200) {
      ElMessage.success('权限分配成功')
      permissionDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('权限分配失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除角色
const handleDelete = (row: any) => {
  console.log('删除角色:', row)
  // TODO: 调用API删除
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  pagination.size = size
  fetchRoleList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page
  pagination.current = page
  fetchRoleList()
}

// 初始化
onMounted(() => {
  fetchRoleList()
})

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