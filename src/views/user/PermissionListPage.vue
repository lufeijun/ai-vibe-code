<template>
  <div class="permission-list-page">
    <div class="page-header">
      <h2 class="page-title">权限管理</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增权限</el-button>
        <el-button type="success" :icon="Refresh" @click="fetchPermissionList">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <!-- 权限树表格 -->
        <el-table
          v-loading="loading"
          :data="permissionTree"
          row-key="id"
          border
          stripe
          style="width: 100%"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          default-expand-all
        >
          <el-table-column prop="name" label="权限名称" min-width="350">
            <template #default="{ row }">
              <span :style="{ paddingLeft: (row.level - 1) * 20 + 'px' }">
                {{ row.name }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="code" label="权限编码" width="180" />
          <el-table-column prop="type" label="类型" width="90">
            <template #default="{ row }">
              <el-tag size="small" :type="getTypeTagType(row.type)">
                {{ getTypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="level" label="层级" width="70" align="center" />
          <el-table-column prop="path" label="路径" width="140" />
          <el-table-column prop="icon" label="图标" width="150" />
          <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
          <el-table-column prop="enabled" label="是否开启" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
                {{ row.enabled ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
              <el-button type="warning" size="small" :icon="Sort" @click="handleEditSort(row)">编辑顺序</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 新增/编辑权限对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑权限' : '新增权限'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-form-item label="父级权限" prop="parentId" v-if="!isEdit">
          <el-tree-select
            v-model="formData.parentId"
            :data="parentPermissionOptions"
            :props="{ label: 'name', value: 'id' }"
            placeholder="选择父级权限（不选则为一级）"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code" v-if="!isEdit">
          <el-input v-model="formData.code" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="类型" prop="type" v-if="!isEdit">
          <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="系统" value="system" />
            <el-option label="模块" value="module" />
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
          </el-select>
        </el-form-item>
        <el-form-item label="路径" prop="path" v-if="!isEdit">
          <el-input v-model="formData.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="!isEdit">
          <el-input v-model="formData.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="是否开启" prop="enabled">
          <el-switch v-model="formData.enabled" active-text="是" inactive-text="否" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑顺序对话框 -->
    <el-dialog
      v-model="sortDialogVisible"
      title="编辑顺序"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="sort-dialog-content">
        <div class="sort-hint">
          <el-icon><InfoFilled /></el-icon>
          <span>点击按钮调整顺序，点击"确定"保存</span>
        </div>
        <div class="sortable-list">
          <div
            v-for="(item, index) in sortPermissions"
            :key="item.id"
            class="sortable-item"
          >
            <span class="sort-index">{{ index + 1 }}</span>
            <span class="sort-name">{{ item.name }}</span>
            <span class="sort-actions">
              <el-button
                type="primary"
                size="small"
                @click="moveTop(index)"
                :disabled="index === 0"
              >置顶</el-button>
              <el-button
                type="primary"
                size="small"
                @click="moveUp(index)"
                :disabled="index === 0"
              >上移</el-button>
              <el-button
                type="primary"
                size="small"
                @click="moveDown(index)"
                :disabled="index === sortPermissions.length - 1"
              >下移</el-button>
              <el-button
                type="primary"
                size="small"
                @click="moveBottom(index)"
                :disabled="index === sortPermissions.length - 1"
              >置底</el-button>
            </span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="sortDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSort" :loading="sortLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch, nextTick } from 'vue'
import { Plus, Refresh, Edit, Sort, InfoFilled, User, Lock } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/utils/request'

// 权限接口
interface Permission {
  id: number
  parentId: number | null
  name: string
  code: string
  type: string
  level: number
  path: string
  icon: string
  sortOrder: number
  enabled: boolean
  children?: Permission[]
}

// 加载状态
const loading = ref(false)

// 所有权限列表
const allPermissions = ref<Permission[]>([])

// 权限树
const permissionTree = ref<Permission[]>([])

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 排序对话框相关
const sortDialogVisible = ref(false)
const sortLoading = ref(false)
const currentSortParentId = ref<number | null>(null)
const sortPermissions = ref<Permission[]>([])

// 表单数据
const formData = reactive({
  id: 0,
  parentId: null as number | null,
  name: '',
  code: '',
  type: '',
  path: '',
  icon: '',
  sortOrder: 0,
  enabled: true
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 父级权限选项（只包含 level 1-3）
const parentPermissionOptions = computed(() => {
  const buildOptions = (permissions: Permission[]): any[] => {
    return permissions
      .filter(p => p.level < 4)
      .map(p => ({
        id: p.id,
        name: p.name,
        children: p.children && p.children.length > 0 ? buildOptions(p.children) : undefined
      }))
  }
  return buildOptions(permissionTree.value)
})

// 获取类型标签类型
const getTypeTagType = (type: string) => {
  const map: Record<string, any> = {
    system: 'danger',
    module: 'warning',
    menu: 'primary',
    button: 'info'
  }
  return map[type] || 'info'
}

// 获取类型标签
const getTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    system: '系统',
    module: '模块',
    menu: '菜单',
    button: '按钮'
  }
  return map[type] || type
}

// 构建权限树
const buildPermissionTree = (permissions: Permission[]): Permission[] => {
  const map = new Map<number, Permission>()
  const tree: Permission[] = []

  permissions.forEach(p => {
    map.set(p.id, { ...p, children: [] })
  })

  permissions.forEach(p => {
    const node = map.get(p.id)!
    if (p.parentId && map.has(p.parentId)) {
      map.get(p.parentId)!.children!.push(node)
    } else if (!p.parentId || p.level === 1) {
      tree.push(node)
    }
  })

  // 按 sortOrder 排序
  const sortTree = (nodes: Permission[]) => {
    nodes.sort((a, b) => a.sortOrder - b.sortOrder)
    nodes.forEach(n => {
      if (n.children && n.children.length > 0) {
        sortTree(n.children)
      }
    })
  }
  sortTree(tree)

  return tree
}

// 获取权限列表
const fetchPermissionList = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/permission/list')
    if (res.code === 200) {
      allPermissions.value = res.data || []
      permissionTree.value = buildPermissionTree(allPermissions.value)
    } else {
      ElMessage.error(res.message || '获取权限列表失败')
    }
  } catch (error) {
    console.error('获取权限列表失败:', error)
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

// 新增权限
const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: 0,
    parentId: null,
    name: '',
    code: '',
    type: '',
    path: '',
    icon: '',
    sortOrder: 0,
    enabled: true
  })
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = (row: Permission) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    parentId: row.parentId,
    name: row.name,
    code: row.code,
    type: row.type,
    path: row.path,
    icon: row.icon,
    sortOrder: row.sortOrder,
    enabled: row.enabled
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      let res: any
      if (isEdit.value) {
        // 编辑时只发送允许修改的字段
        const updateData = {
          id: formData.id,
          name: formData.name,
          sortOrder: formData.sortOrder,
          enabled: formData.enabled
        }
        res = await request.put(`/permission/${formData.id}`, updateData)
      } else {
        // 新增时确定层级
        const parentId = formData.parentId
        let level = 1
        if (parentId) {
          const parent = allPermissions.value.find(p => p.id === parentId)
          if (parent) {
            level = parent.level + 1
          }
        }
        const createData = {
          ...formData,
          level
        }
        delete createData.id
        res = await request.post('/permission', createData)
      }

      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        fetchPermissionList()
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

// 编辑顺序
const handleEditSort = (row: Permission) => {
  currentSortParentId.value = row.parentId
  // 获取同一父级下的所有权限
  sortPermissions.value = allPermissions.value
    .filter(p => p.parentId === row.parentId)
    .sort((a, b) => a.sortOrder - b.sortOrder)

  sortDialogVisible.value = true
}

// 上移
const moveUp = (index: number) => {
  if (index <= 0) return
  const temp = sortPermissions.value[index]
  sortPermissions.value[index] = sortPermissions.value[index - 1]
  sortPermissions.value[index - 1] = temp
  // 更新 sortOrder
  updateSortOrders()
}

// 下移
const moveDown = (index: number) => {
  if (index >= sortPermissions.value.length - 1) return
  const temp = sortPermissions.value[index]
  sortPermissions.value[index] = sortPermissions.value[index + 1]
  sortPermissions.value[index + 1] = temp
  // 更新 sortOrder
  updateSortOrders()
}

// 移到顶部
const moveTop = (index: number) => {
  if (index <= 0) return
  const item = sortPermissions.value.splice(index, 1)[0]
  sortPermissions.value.unshift(item)
  // 更新 sortOrder
  updateSortOrders()
}

// 移到底部
const moveBottom = (index: number) => {
  if (index >= sortPermissions.value.length - 1) return
  const item = sortPermissions.value.splice(index, 1)[0]
  sortPermissions.value.push(item)
  // 更新 sortOrder
  updateSortOrders()
}

// 更新 sortOrder 值
const updateSortOrders = () => {
  sortPermissions.value.forEach((item, index) => {
    item.sortOrder = index + 1
  })
}

// 保存排序
const handleSaveSort = async () => {
  sortLoading.value = true
  try {
    const items = sortPermissions.value.map(p => ({
      id: p.id,
      sortOrder: p.sortOrder
    }))

    const res: any = await request.post('/permission/batch-update-sort', { items })

    if (res.code === 200) {
      ElMessage.success('排序更新成功')
      sortDialogVisible.value = false
      fetchPermissionList()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    console.error('更新排序失败:', error)
    ElMessage.error('更新排序失败')
  } finally {
    sortLoading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchPermissionList()
})
</script>

<style scoped>
.permission-list-page {
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

.sort-dialog-content {
  padding: 10px 0;
}

.sort-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  margin-bottom: 16px;
  background: #f4f4f5;
  border-radius: 4px;
  color: #909399;
}

.sortable-list {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.sortable-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
}

.sortable-item:last-child {
  border-bottom: none;
}

.sortable-item:hover {
  background: #f5f7fa;
}

.sort-index {
  width: 30px;
  color: #909399;
  font-size: 14px;
  font-weight: bold;
}

.sort-name {
  flex: 1;
  color: #303133;
}

.sort-actions {
  display: flex;
  gap: 4px;
}
</style>
