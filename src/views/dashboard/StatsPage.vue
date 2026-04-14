<template>
  <div class="dashboard-stats">
    <h2 class="page-title">数据统计</h2>

    <!-- 时间范围选择 -->
    <div class="time-range">
      <el-card class="time-card" shadow="hover">
        <div class="time-header">
          <span class="time-label">统计时间范围</span>
          <div class="time-controls">
            <el-select v-model="timeRange" size="small" style="width: 120px">
              <el-option label="今天" value="today" />
              <el-option label="近7天" value="7d" />
              <el-option label="近30天" value="30d" />
              <el-option label="近90天" value="90d" />
              <el-option label="自定义" value="custom" />
            </el-select>
            <el-date-picker
              v-if="timeRange === 'custom'"
              v-model="customDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              size="small"
              style="width: 240px; margin-left: 10px"
            />
            <el-button type="primary" size="small" style="margin-left: 10px" @click="handleRefresh">
              刷新
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 详细统计 -->
    <div class="detailed-stats">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="stat-card" shadow="hover">
            <template #header>
              <div class="stat-header">
                <span>用户活跃度</span>
                <el-tag type="success">日活跃用户: 890</el-tag>
              </div>
            </template>
            <div class="stat-chart">
              <div class="chart-placeholder">
                <el-icon><TrendCharts /></el-icon>
                <p>用户活跃度趋势图</p>
              </div>
            </div>
            <div class="stat-summary">
              <div class="summary-item">
                <div class="summary-label">平均日活跃用户</div>
                <div class="summary-value">856</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">较上月变化</div>
                <div class="summary-value positive">+12.5%</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="stat-card" shadow="hover">
            <template #header>
              <div class="stat-header">
                <span>订单统计</span>
                <el-tag type="warning">今日订单: 5,678</el-tag>
              </div>
            </template>
            <div class="stat-chart">
              <div class="chart-placeholder">
                <el-icon><Histogram /></el-icon>
                <p>订单统计柱状图</p>
              </div>
            </div>
            <div class="stat-summary">
              <div class="summary-item">
                <div class="summary-label">平均日订单量</div>
                <div class="summary-value">4,892</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">较上月变化</div>
                <div class="summary-value positive">+8.3%</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :xs="24" :lg="12">
          <el-card class="stat-card" shadow="hover">
            <template #header>
              <div class="stat-header">
                <span>收入统计</span>
                <el-tag type="danger">今日收入: ¥123,456</el-tag>
              </div>
            </template>
            <div class="stat-chart">
              <div class="chart-placeholder">
                <el-icon><DataLine /></el-icon>
                <p>收入统计折线图</p>
              </div>
            </div>
            <div class="stat-summary">
              <div class="summary-item">
                <div class="summary-label">平均日收入</div>
                <div class="summary-value">¥98,765</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">较上月变化</div>
                <div class="summary-value positive">+15.2%</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="stat-card" shadow="hover">
            <template #header>
              <div class="stat-header">
                <span>用户留存率</span>
                <el-tag type="info">7日留存: 68%</el-tag>
              </div>
            </template>
            <div class="stat-chart">
              <div class="chart-placeholder">
                <el-icon><PieChart /></el-icon>
                <p>用户留存率饼图</p>
              </div>
            </div>
            <div class="stat-summary">
              <div class="summary-item">
                <div class="summary-label">30日留存率</div>
                <div class="summary-value">42%</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">较上月变化</div>
                <div class="summary-value negative">-3.2%</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <div class="data-table">
      <el-card class="table-card" shadow="hover">
        <template #header>
          <div class="table-header">
            <span>详细数据</span>
            <el-button link size="small" :icon="Download">导出数据</el-button>
          </div>
        </template>
        <el-table :data="tableData" border stripe style="width: 100%">
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="newUsers" label="新增用户" width="100" align="center" />
          <el-table-column prop="activeUsers" label="活跃用户" width="100" align="center" />
          <el-table-column prop="orders" label="订单数" width="100" align="center" />
          <el-table-column prop="revenue" label="收入(元)" width="120" align="center" />
          <el-table-column prop="conversionRate" label="转化率" width="100" align="center">
            <template #default="{ row }">
              <span :class="getConversionRateClass(row.conversionRate)">
                {{ row.conversionRate }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="avgOrderValue" label="客单价(元)" width="120" align="center" />
        </el-table>
        <div class="table-pagination">
          <el-pagination
            v-model:current-page="tablePage.current"
            v-model:page-size="tablePage.size"
            :page-sizes="[10, 20, 50]"
            :total="tablePage.total"
            layout="total, sizes, prev, pager, next"
            @size-change="handleTableSizeChange"
            @current-change="handleTablePageChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  TrendCharts,
  Histogram,
  DataLine,
  PieChart,
  Download
} from '@element-plus/icons-vue'

// 时间范围
const timeRange = ref('7d')
const customDateRange = ref([])

// 表格数据
const tableData = ref([
  { date: '2024-01-01', newUsers: 123, activeUsers: 890, orders: 5678, revenue: '123,456', conversionRate: 3.2, avgOrderValue: 21.7 },
  { date: '2023-12-31', newUsers: 118, activeUsers: 856, orders: 5321, revenue: '115,789', conversionRate: 2.9, avgOrderValue: 21.8 },
  { date: '2023-12-30', newUsers: 112, activeUsers: 832, orders: 4987, revenue: '108,432', conversionRate: 3.1, avgOrderValue: 21.7 },
  { date: '2023-12-29', newUsers: 105, activeUsers: 798, orders: 4765, revenue: '102,345', conversionRate: 2.8, avgOrderValue: 21.5 },
  { date: '2023-12-28', newUsers: 98, activeUsers: 765, orders: 4321, revenue: '98,765', conversionRate: 3.0, avgOrderValue: 22.1 },
  { date: '2023-12-27', newUsers: 92, activeUsers: 721, orders: 4123, revenue: '95,432', conversionRate: 2.7, avgOrderValue: 21.9 },
  { date: '2023-12-26', newUsers: 87, activeUsers: 698, orders: 3987, revenue: '92,345', conversionRate: 3.3, avgOrderValue: 22.3 }
])

// 表格分页
const tablePage = reactive({
  current: 1,
  size: 10,
  total: 7
})

// 刷新数据
const handleRefresh = () => {
  console.log('刷新数据，时间范围:', timeRange.value, customDateRange.value)
  // TODO: 根据时间范围重新加载数据
}

// 获取转化率样式类
const getConversionRateClass = (rate: number) => {
  if (rate >= 3.0) return 'rate-high'
  if (rate >= 2.5) return 'rate-medium'
  return 'rate-low'
}

// 表格分页大小改变
const handleTableSizeChange = (size: number) => {
  tablePage.size = size
  console.log('表格每页大小改变:', size)
  // TODO: 重新获取表格数据
}

// 表格当前页改变
const handleTablePageChange = (page: number) => {
  tablePage.current = page
  console.log('表格当前页改变:', page)
  // TODO: 重新获取表格数据
}
</script>

<style scoped>
.dashboard-stats {
  padding: 20px;
}

.page-title {
  margin: 0 0 24px 0;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

/* 时间范围选择 */
.time-range {
  margin-bottom: 24px;
}

.time-card {
  border: none;
}

.time-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-label {
  font-weight: 500;
  color: #333;
}

.time-controls {
  display: flex;
  align-items: center;
}

/* 详细统计 */
.stat-card {
  border: none;
  margin-bottom: 20px;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-chart {
  height: 200px;
  margin-bottom: 16px;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  background: #f9f9f9;
  border-radius: 4px;
}

.chart-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.chart-placeholder p {
  margin: 0;
  font-size: 14px;
}

.stat-summary {
  display: flex;
  justify-content: space-around;
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.summary-item {
  text-align: center;
}

.summary-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.summary-value {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.summary-value.positive {
  color: #67c23a;
}

.summary-value.negative {
  color: #f56c6c;
}

/* 数据表格 */
.data-table {
  margin-top: 24px;
}

.table-card {
  border: none;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 转化率样式 */
.rate-high {
  color: #67c23a;
  font-weight: 500;
}

.rate-medium {
  color: #e6a23c;
  font-weight: 500;
}

.rate-low {
  color: #f56c6c;
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .time-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .time-controls {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .stat-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .stat-summary {
    flex-direction: column;
    gap: 12px;
  }
}
</style>