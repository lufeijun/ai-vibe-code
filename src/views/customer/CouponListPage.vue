<template>
  <div class="coupon-list-page">
    <div class="page-header">
      <h2 class="page-title">优惠券管理</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增优惠券</el-button>
        <el-button :icon="Refresh" @click="fetchCouponList">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="优惠券名称">
            <el-input v-model="searchForm.name" placeholder="请输入名称" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
              <el-option label="启用" value="启用" />
              <el-option label="禁用" value="禁用" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="couponList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="优惠券名称" width="180" />
          <el-table-column prop="code" label="编码" width="120" />
          <el-table-column label="抵扣金额" width="100">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: bold">¥{{ row.discountAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="minAmount" label="使用门槛" width="100" />
          <el-table-column prop="totalQuantity" label="总量" width="80" />
          <el-table-column prop="usedQuantity" label="已用" width="80" />
          <el-table-column prop="validStart" label="有效期开始" width="120" />
          <el-table-column prop="validEnd" label="有效期结束" width="120" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === '启用' ? 'success' : 'info'">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
      :title="isEdit ? '编辑优惠券' : '新增优惠券'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" label-width="120px">
        <el-form-item label="优惠券名称">
          <el-input v-model="formData.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠券编码">
          <el-input v-model="formData.code" placeholder="请输入唯一编码" />
        </el-form-item>
        <el-form-item label="抵扣金额">
          <el-input-number v-model="formData.discountAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="使用门槛">
          <el-input-number v-model="formData.minAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="发放总量">
          <el-input-number v-model="formData.totalQuantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每人限领">
          <el-input-number v-model="formData.limitPerCustomer" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期开始">
          <el-date-picker v-model="formData.validStart" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期结束">
          <el-date-picker v-model="formData.validEnd" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
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
import { Plus, Refresh, Search, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import request from '@/utils/request'

interface Coupon {
  id: number
  name: string
  code: string
  discountAmount: number
  minAmount: number
  totalQuantity: number
  usedQuantity: number
  limitPerCustomer: number
  validStart: string
  validEnd: string
  status: string
}

const searchForm = reactive({
  name: '',
  status: ''
})

const loading = ref(false)
const couponList = ref<Coupon[]>([])

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
  code: '',
  discountAmount: 0,
  minAmount: 0,
  totalQuantity: 100,
  usedQuantity: 0,
  limitPerCustomer: 1,
  validStart: '',
  validEnd: '',
  status: '启用'
})

const fetchCouponList = async () => {
  loading.value = true
  try {
    const res: any = await request.post('/coupon/list', {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res.code === 200) {
      couponList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取优惠券列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchCouponList()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  pagination.pageNum = 1
  fetchCouponList()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchCouponList()
}

const handleCurrentChange = (page: number) => {
  pagination.pageNum = page
  fetchCouponList()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: 0,
    name: '',
    code: '',
    discountAmount: 0,
    minAmount: 0,
    totalQuantity: 100,
    usedQuantity: 0,
    limitPerCustomer: 1,
    validStart: '',
    validEnd: '',
    status: '启用'
  })
  dialogVisible.value = true
}

const handleEdit = (row: Coupon) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row: Coupon) => {
  try {
    await ElMessageBox.confirm('确定要删除该优惠券吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res: any = await request.post(`/coupon/delete/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchCouponList()
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
    const url = isEdit.value ? '/coupon/update' : '/coupon/create'
    const payload = { ...formData }

    if (!isEdit.value) {
      delete payload.id
    }

    const res: any = await request.post(url, payload)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      fetchCouponList()
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
  fetchCouponList()
})
</script>

<style scoped>
.coupon-list-page {
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
