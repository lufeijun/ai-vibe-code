<template>
  <div class="customer-detail-page">
    <div class="page-header">
      <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
      <h2 class="page-title">客户详情</h2>
    </div>

    <el-card v-loading="loading" class="detail-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="primary" size="small" :icon="Edit" @click="handleEdit">编辑</el-button>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="手机号">{{ customer.phone }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ customer.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{ customer.region || '-' }}</el-descriptions-item>
        <el-descriptions-item label="爱好">{{ customer.hobby || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ customer.emergencyContact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="推荐人">
          {{ referrerName || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="registrations-card">
      <template #header>
        <div class="card-header">
          <span>报名记录</span>
          <el-button type="primary" size="small" :icon="Plus" @click="handleAddRegistration">报名活动</el-button>
        </div>
      </template>
      <el-table :data="registrationList" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="activityName" label="活动名称" width="180" />
        <el-table-column prop="registrationTime" label="报名时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.registrationTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '已报名' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewRegistration(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="editDialogVisible"
      title="编辑客户"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="editFormRef" :model="editFormData" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="editFormData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="爱好">
          <el-input v-model="editFormData.hobby" placeholder="请输入爱好" />
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="editFormData.region" placeholder="请输入地区" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="editFormData.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editSubmitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="registrationDialogVisible"
      title="报名活动"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="registrationFormRef" :model="registrationFormData" label-width="100px">
        <el-form-item label="选择活动">
          <el-select v-model="registrationFormData.activityId" placeholder="请选择活动" style="width: 100%">
            <el-option
              v-for="activity in availableActivities"
              :key="activity.id"
              :label="activity.name"
              :value="activity.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="registrationFormData.remarks" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registrationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRegistrationSubmit" :loading="registrationSubmitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="registrationDrawerVisible"
      title="报名详情"
      size="600px"
    >
      <div v-if="currentRegistration" class="registration-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="活动名称">{{ currentRegistration.activityName }}</el-descriptions-item>
          <el-descriptions-item label="报名时间">{{ formatDate(currentRegistration.registrationTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentRegistration.status === '已报名' ? 'success' : 'info'">{{ currentRegistration.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ currentRegistration.remarks || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="followup-section">
          <div class="section-header">
            <h4>跟进记录</h4>
            <el-button type="primary" size="small" :icon="Plus" @click="handleAddFollowUp">添加跟进</el-button>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="followup in followupList"
              :key="followup.id"
              :timestamp="formatDate(followup.followUpTime)"
              placement="top"
            >
              <el-card>
                <div class="followup-content">
                  <div class="followup-meta">
                    <el-tag size="small">{{ followup.method }}</el-tag>
                    <el-tag v-if="followup.status" size="small" type="info">{{ followup.status }}</el-tag>
                    <span v-if="followup.nextFollowUp" class="next-followup">下次跟进: {{ formatDate(followup.nextFollowUp) }}</span>
                  </div>
                  <p>{{ followup.content }}</p>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-drawer>

    <el-dialog
      v-model="followupDialogVisible"
      title="添加跟进记录"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="followupFormRef" :model="followupFormData" label-width="100px">
        <el-form-item label="跟进时间">
          <el-date-picker
            v-model="followupFormData.followUpTime"
            type="datetime"
            placeholder="选择时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="跟进方式">
          <el-select v-model="followupFormData.method" placeholder="请选择" style="width: 100%">
            <el-option label="电话" value="电话" />
            <el-option label="微信" value="微信" />
            <el-option label="面谈" value="面谈" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容">
          <el-input v-model="followupFormData.content" type="textarea" :rows="4" placeholder="请输入跟进内容" />
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker
            v-model="followupFormData.nextFollowUp"
            type="datetime"
            placeholder="选择时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="followupFormData.status" placeholder="请选择" style="width: 100%">
            <el-option label="已联系" value="已联系" />
            <el-option label="跟进中" value="跟进中" />
            <el-option label="已报名" value="已报名" />
            <el-option label="已放弃" value="已放弃" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleFollowUpSubmit" :loading="followupSubmitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Edit, Plus } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance } from 'element-plus'
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

interface Customer {
  id: number
  phone: string
  name: string
  hobby: string
  region: string
  emergencyContact: string
  referrerId: number | null
}

interface Registration {
  id: number
  customerId: number
  activityId: number
  activityName?: string
  registrationTime: string
  status: string
  remarks: string
}

interface FollowUp {
  id: number
  registrationId: number
  followUpTime: string
  followerId: number
  content: string
  method: string
  nextFollowUp: string
  status: string
}

interface Activity {
  id: number
  name: string
}

const loading = ref(false)
const customer = ref<Customer>({
  id: 0,
  phone: '',
  name: '',
  hobby: '',
  region: '',
  emergencyContact: '',
  referrerId: null
})
const referrerName = ref('')
const registrationList = ref<Registration[]>([])
const followupList = ref<FollowUp[]>([])
const availableActivities = ref<Activity[]>([])

const editDialogVisible = ref(false)
const editSubmitLoading = ref(false)
const editFormRef = ref<FormInstance>()
const editFormData = reactive({
  id: 0,
  name: '',
  hobby: '',
  region: '',
  emergencyContact: ''
})

const registrationDialogVisible = ref(false)
const registrationSubmitLoading = ref(false)
const registrationFormRef = ref<FormInstance>()
const registrationFormData = reactive({
  customerId: 0,
  activityId: null as number | null,
  remarks: ''
})

const registrationDrawerVisible = ref(false)
const currentRegistration = ref<Registration | null>(null)

const followupDialogVisible = ref(false)
const followupSubmitLoading = ref(false)
const followupFormRef = ref<FormInstance>()
const followupFormData = reactive({
  registrationId: 0,
  followUpTime: '',
  followerId: 0,
  content: '',
  method: '',
  nextFollowUp: '',
  status: ''
})

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const fetchCustomerDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res: any = await request.get(`/customer/${id}`)
    if (res.code === 200) {
      customer.value = res.data
      Object.assign(editFormData, res.data)

      if (res.data.referrerId) {
        const referrerRes: any = await request.get(`/customer/${res.data.referrerId}`)
        if (referrerRes.code === 200) {
          referrerName.value = referrerRes.data.name || referrerRes.data.phone
        }
      }
    }
  } catch (error) {
    console.error('获取客户详情失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchRegistrations = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res: any = await request.get(`/customer/${id}/registrations`)
    if (res.code === 200) {
      registrationList.value = res.data || []

      for (const reg of registrationList.value) {
        const activityRes: any = await request.get(`/activity/${reg.activityId}`)
        if (activityRes.code === 200) {
          reg.activityName = activityRes.data.name
        }
      }
    }
  } catch (error) {
    console.error('获取报名记录失败:', error)
  }
}

const fetchAvailableActivities = async () => {
  try {
    const res: any = await request.post('/activity/list', { pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
      availableActivities.value = (res.data.records || []).filter((a: Activity) => a.status === '报名中')
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
  }
}

const handleBack = () => {
  router.push('/customer/list')
}

const handleEdit = () => {
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  editSubmitLoading.value = true
  try {
    const res: any = await request.post('/customer/update', editFormData)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      editDialogVisible.value = false
      fetchCustomerDetail()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    editSubmitLoading.value = false
  }
}

const handleAddRegistration = async () => {
  await fetchAvailableActivities()
  registrationFormData.customerId = customer.value.id
  registrationFormData.activityId = null
  registrationFormData.remarks = ''
  registrationDialogVisible.value = true
}

const handleRegistrationSubmit = async () => {
  if (!registrationFormData.activityId) {
    ElMessage.warning('请选择活动')
    return
  }
  registrationSubmitLoading.value = true
  try {
    const res: any = await request.post('/registration/create', registrationFormData)
    if (res.code === 200) {
      ElMessage.success('报名成功')
      registrationDialogVisible.value = false
      fetchRegistrations()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    registrationSubmitLoading.value = false
  }
}

const handleViewRegistration = async (row: Registration) => {
  currentRegistration.value = row
  registrationDrawerVisible.value = true

  try {
    const res: any = await request.get(`/registration/${row.id}/followups`)
    if (res.code === 200) {
      followupList.value = res.data || []
    }
  } catch (error) {
    console.error('获取跟进记录失败:', error)
  }
}

const handleAddFollowUp = () => {
  if (!currentRegistration.value) return
  followupFormData.registrationId = currentRegistration.value.id
  followupFormData.followerId = authStore.userInfo.id || 0
  followupFormData.followUpTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  followupFormData.content = ''
  followupFormData.method = ''
  followupFormData.nextFollowUp = ''
  followupFormData.status = ''
  followupDialogVisible.value = true
}

const handleFollowUpSubmit = async () => {
  followupSubmitLoading.value = true
  try {
    const res: any = await request.post('/followup/create', followupFormData)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      followupDialogVisible.value = false

      if (currentRegistration.value) {
        const followupRes: any = await request.get(`/registration/${currentRegistration.value.id}/followups`)
        if (followupRes.code === 200) {
          followupList.value = followupRes.data || []
        }
      }
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    followupSubmitLoading.value = false
  }
}

onMounted(() => {
  fetchCustomerDetail()
  fetchRegistrations()
})
</script>

<style scoped>
.customer-detail-page {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

.detail-card,
.registrations-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.followup-section {
  margin-top: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h4 {
  margin: 0;
}

.followup-content {
  padding: 5px 0;
}

.followup-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.next-followup {
  font-size: 12px;
  color: #666;
}

.followup-content p {
  margin: 0;
  color: #333;
}
</style>
