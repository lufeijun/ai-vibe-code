<template>
  <div class="customer-list-page">
    <div class="page-header">
      <h2 class="page-title">客户列表</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增客户</el-button>
        <el-button :icon="Refresh" @click="fetchCustomerList">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="手机号">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="地区">
            <el-input v-model="searchForm.region" placeholder="请输入地区" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="customerList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="region" label="地区" width="120" />
          <el-table-column prop="hobby" label="爱好" width="150" />
          <el-table-column prop="emergencyContact" label="紧急联系人" width="120" />
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" :icon="View" @click="handleViewDetail(row)">详情</el-button>
              <el-button type="success" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
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
      :title="isEdit ? '编辑客户' : '新增客户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="爱好">
          <el-input v-model="formData.hobby" placeholder="请输入爱好" />
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="formData.region" placeholder="请输入地区" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="推荐人">
          <el-select
            v-model="formData.referrerId"
            filterable
            remote
            reserve-keyword
            placeholder="请搜索推荐人手机号"
            :remote-method="searchReferrer"
            :loading="referrerLoading"
            style="width: 100%"
            clearable
          >
            <el-option
              v-for="item in referrerOptions"
              :key="item.id"
              :label="item.phone + ' ' + (item.name || '')"
              :value="item.id"
            />
          </el-select>
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
import { useRouter } from 'vue-router'
import { Plus, Refresh, Search, Edit, View } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

interface Customer {
  id: number
  phone: string
  name: string
  hobby: string
  region: string
  emergencyContact: string
  referrerId: number | null
  createdAt: string
}

const searchForm = reactive({
  phone: '',
  name: '',
  region: ''
})

const loading = ref(false)
const customerList = ref<Customer[]>([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const referrerLoading = ref(false)
const referrerOptions = ref<Customer[]>([])

const formData = reactive({
  id: 0,
  phone: '',
  name: '',
  hobby: '',
  region: '',
  emergencyContact: '',
  referrerId: null as number | null
})

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const fetchCustomerList = async () => {
  loading.value = true
  try {
    const res: any = await request.post('/customer/list', {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res.code === 200) {
      customerList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取客户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchCustomerList()
}

const handleReset = () => {
  searchForm.phone = ''
  searchForm.name = ''
  searchForm.region = ''
  pagination.pageNum = 1
  fetchCustomerList()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchCustomerList()
}

const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  fetchCustomerList()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: 0,
    phone: '',
    name: '',
    hobby: '',
    region: '',
    emergencyContact: '',
    referrerId: null
  })
  dialogVisible.value = true
}

const handleEdit = (row: Customer) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleViewDetail = (row: Customer) => {
  router.push(`/customer/detail/${row.id}`)
}

const searchReferrer = async (query: string) => {
  if (query.length < 3) return
  referrerLoading.value = true
  try {
    const res: any = await request.get(`/customer/search?phone=${encodeURIComponent(query)}`)
    if (res.code === 200) {
      referrerOptions.value = res.data || []
    }
  } catch (error) {
    console.error('搜索推荐人失败:', error)
  } finally {
    referrerLoading.value = false
  }
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    const url = isEdit.value ? '/customer/update' : '/customer/create'
    const res: any = await request.post(url, formData)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      fetchCustomerList()
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
  fetchCustomerList()
})
</script>

<style scoped>
.customer-list-page {
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
