<template>
  <div class="system-logs-page">
    <div class="page-header">
      <h2 class="page-title">操作日志</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Download" @click="handleExport">导出日志</el-button>
        <el-button type="danger" :icon="Delete" @click="handleClear">清空日志</el-button>
        <el-button type="success" :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <!-- 筛选条件 -->
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="操作类型">
            <el-select v-model="filterForm.type" placeholder="请选择操作类型" clearable>
              <el-option label="登录" value="login" />
              <el-option label="新增" value="create" />
              <el-option label="修改" value="update" />
              <el-option label="删除" value="delete" />
              <el-option label="查询" value="query" />
              <el-option label="导出" value="export" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作人员">
            <el-input
              v-model="filterForm.operator"
              placeholder="请输入操作人员"
              clearable
            />
          </el-form-item>
          <el-form-item label="操作时间">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 日志表格 -->
        <el-table :data="logList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="operator" label="操作人员" width="120" />
          <el-table-column prop="type" label="操作类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getLogTypeTag(row.type)" size="small">
                {{ getLogTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="module" label="操作模块" width="120" />
          <el-table-column prop="action" label="操作内容" min-width="200" />
          <el-table-column prop="ip" label="IP地址" width="140" />
          <el-table-column prop="userAgent" label="用户代理" width="180">
            <template #default="{ row }">
              <el-tooltip :content="row.userAgent" placement="top">
                <span class="user-agent">{{ truncateText(row.userAgent, 20) }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="result" label="操作结果" width="100">
            <template #default="{ row }">
              <el-tag :type="row.result === 'success' ? 'success' : 'danger'" size="small">
                {{ row.result === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="executeTime" label="执行时间" width="100">
            <template #default="{ row }">
              <span>{{ row.executeTime }}ms</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="操作时间" width="180" />
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" :icon="View" @click="handleViewDetail(row)">
                详情
              </el-button>
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

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="日志详情"
      width="700px"
      destroy-on-close
    >
      <div v-if="currentLog" class="log-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
          <el-descriptions-item label="操作人员">{{ currentLog.operator }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag :type="getLogTypeTag(currentLog.type)" size="small">
              {{ getLogTypeText(currentLog.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作模块">{{ currentLog.module }}</el-descriptions-item>
          <el-descriptions-item label="操作内容">{{ currentLog.action }}</el-descriptions-item>
          <el-descriptions-item label="操作参数">
            <pre class="log-data">{{ formatLogData(currentLog.requestData) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="返回结果">
            <pre class="log-data">{{ formatLogData(currentLog.responseData) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
          <el-descriptions-item label="用户代理">{{ currentLog.userAgent }}</el-descriptions-item>
          <el-descriptions-item label="操作结果">
            <el-tag :type="currentLog.result === 'success' ? 'success' : 'danger'" size="small">
              {{ currentLog.result === 'success' ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="执行时间">{{ currentLog.executeTime }}ms</el-descriptions-item>
          <el-descriptions-item label="错误信息">
            <span :class="{ 'error-text': currentLog.errorMessage }">
              {{ currentLog.errorMessage || '无' }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ currentLog.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Delete,
  View
} from '@element-plus/icons-vue'

// 筛选表单
const filterForm = reactive({
  type: '',
  operator: '',
  dateRange: []
})

// 日志列表数据（模拟数据）
const logList = ref([
  {
    id: 1,
    operator: 'admin',
    type: 'login',
    module: '认证模块',
    action: '用户登录',
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    result: 'success',
    executeTime: 120,
    createTime: '2024-01-01 10:00:00',
    requestData: { username: 'admin', remember: true },
    responseData: { code: 200, message: '登录成功', token: 'xxx' },
    errorMessage: ''
  },
  {
    id: 2,
    operator: 'admin',
    type: 'create',
    module: '用户管理',
    action: '新增用户',
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    result: 'success',
    executeTime: 250,
    createTime: '2024-01-01 11:30:00',
    requestData: { username: 'user1', email: 'user1@example.com', role: 'user' },
    responseData: { code: 200, message: '用户创建成功', userId: 2 },
    errorMessage: ''
  },
  {
    id: 3,
    operator: 'admin',
    type: 'delete',
    module: '用户管理',
    action: '删除用户',
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    result: 'failure',
    executeTime: 180,
    createTime: '2024-01-02 14:20:00',
    requestData: { userId: 3 },
    responseData: { code: 500, message: '用户不存在' },
    errorMessage: '用户ID不存在'
  }
])

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 3
})

// 日志详情对话框相关
const detailDialogVisible = ref(false)
const currentLog = ref<any>(null)

// 获取日志类型标签样式
const getLogTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    login: 'primary',
    create: 'success',
    update: 'warning',
    delete: 'danger',
    query: 'info',
    export: 'success',
    other: ''
  }
  return tagMap[type] || ''
}

// 获取日志类型文本
const getLogTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    login: '登录',
    create: '新增',
    update: '修改',
    delete: '删除',
    query: '查询',
    export: '导出',
    other: '其他'
  }
  return textMap[type] || type
}

// 截断文本
const truncateText = (text: string, length: number) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 格式化日志数据
const formatLogData = (data: any) => {
  if (!data) return '无'
  try {
    return JSON.stringify(data, null, 2)
  } catch {
    return String(data)
  }
}

// 搜索日志
const handleSearch = () => {
  console.log('搜索参数:', filterForm)
  // TODO: 调用API搜索日志
  ElMessage.info('正在搜索日志...')
}

// 重置搜索
const handleReset = () => {
  filterForm.type = ''
  filterForm.operator = ''
  filterForm.dateRange = []
  handleSearch()
}

// 查看详情
const handleViewDetail = (row: any) => {
  currentLog.value = row
  detailDialogVisible.value = true
}

// 导出日志
const handleExport = () => {
  ElMessage.info('正在导出日志文件...')
  // TODO: 调用API导出日志
}

// 清空日志
const handleClear = () => {
  ElMessageBox.confirm('确定要清空所有操作日志吗？此操作不可恢复！', '警告', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    ElMessage.success('日志已清空')
    // TODO: 调用API清空日志
  }).catch(() => {
    // 取消操作
  })
}

// 刷新日志
const handleRefresh = () => {
  handleSearch()
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
.system-logs-page {
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

.filter-form {
  margin-bottom: 20px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.user-agent {
  color: #666;
  cursor: pointer;
}

.log-detail {
  max-height: 500px;
  overflow-y: auto;
}

.log-data {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.4;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.error-text {
  color: #f56c6c;
  font-weight: 500;
}
</style>