<template>
  <div class="activity-list-page">
    <div class="page-header">
      <h2 class="page-title">活动管理</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增活动</el-button>
        <el-button :icon="Refresh" @click="fetchActivityList">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="活动名称">
            <el-input v-model="searchForm.name" placeholder="请输入活动名称" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
              <el-option label="未开始" value="未开始" />
              <el-option label="报名中" value="报名中" />
              <el-option label="已结束" value="已结束" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="activityList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="活动名称" width="180" />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column prop="startDate" label="活动开始" width="120" />
          <el-table-column prop="endDate" label="活动结束" width="120" />
          <el-table-column prop="registrationStart" label="报名开始" width="120" />
          <el-table-column prop="registrationEnd" label="报名结束" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑活动' : '新增活动'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" label-width="120px">
        <el-form-item label="活动名称">
          <el-input v-model="formData.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="formData.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option label="夏令营" value="夏令营" />
            <el-option label="冬令营" value="冬令营" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动开始日期">
          <el-date-picker v-model="formData.startDate" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动结束日期">
          <el-date-picker v-model="formData.endDate" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报名开始日期">
          <el-date-picker v-model="formData.registrationStart" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报名结束日期">
          <el-date-picker v-model="formData.registrationEnd" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="formData.maxParticipants" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="未开始" value="未开始" />
            <el-option label="报名中" value="报名中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入活动描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Refresh, Search, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import request from '@/utils/request'

interface Activity {
  id: number
  name: string
  type: string
  startDate: string
  endDate: string
  registrationStart: string
  registrationEnd: string
  location: string
  maxParticipants: number
  status: string
  description: string
}

const searchForm = reactive({
  name: '',
  status: ''
})

const loading = ref(false)
const activityList = ref<Activity[]>([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  id: 0,
  name: '',
  type: '',
  startDate: '',
  endDate: '',
  registrationStart: '',
  registrationEnd: '',
  location: '',
  maxParticipants: 100,
  status: '未开始',
  description: ''
})

const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    '未开始': 'info',
    '报名中': 'success',
    '已结束': 'warning'
  }
  return typeMap[status] || 'info'
}

const fetchActivityList = async () => {
  loading.value = true
  try {
    const res: any = await request.post('/activity/list', {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res.code === 200) {
      activityList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchActivityList()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  pagination.pageNum = 1
  fetchActivityList()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchActivityList()
}

const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  fetchActivityList()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: 0,
    name: '',
    type: '',
    startDate: '',
    endDate: '',
    registrationStart: '',
    registrationEnd: '',
    location: '',
    maxParticipants: 100,
    status: '未开始',
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: Activity) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row: Activity) => {
  try {
    await ElMessageBox.confirm('确定要删除该活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res: any = await request.post(`/activity/delete/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchActivityList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    const url = isEdit.value ? '/activity/update' : '/activity/create'
    const res: any = await request.post(url, formData)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      fetchActivityList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchActivityList()
})
</script>

<style scoped>
.activity-list-page {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
