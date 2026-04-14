<template>
  <div class="dashboard-overview">
    <h2 class="page-title">仪表盘概览</h2>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon user-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">1,234</div>
                <div class="stat-label">用户总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon active-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">890</div>
                <div class="stat-label">活跃用户</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon order-icon">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">5,678</div>
                <div class="stat-label">今日订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon revenue-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">¥ 123,456</div>
                <div class="stat-label">今日收入</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>用户增长趋势</span>
                <el-select v-model="trendPeriod" size="small" style="width: 120px">
                  <el-option label="近7天" value="7d" />
                  <el-option label="近30天" value="30d" />
                  <el-option label="近90天" value="90d" />
                </el-select>
              </div>
            </template>
            <div class="chart-container">
              <!-- 这里可以放置图表组件，暂时用占位图 -->
              <div class="chart-placeholder">
                <el-icon><TrendCharts /></el-icon>
                <p>用户增长趋势图表</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span>用户分布</span>
              </div>
            </template>
            <div class="chart-container">
              <!-- 这里可以放置饼图组件，暂时用占位图 -->
              <div class="chart-placeholder pie">
                <el-icon><PieChart /></el-icon>
                <p>用户分布饼图</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activities">
      <el-card class="activities-card" shadow="hover">
        <template #header>
          <div class="activities-header">
            <span>最近活动</span>
            <el-button link size="small">查看全部</el-button>
          </div>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in activities"
            :key="index"
            :timestamp="activity.time"
            placement="top"
          >
            <div class="activity-item">
              <div class="activity-avatar">
                <el-avatar :size="32" :src="activity.avatar">{{ activity.user.charAt(0) }}</el-avatar>
              </div>
              <div class="activity-content">
                <div class="activity-user">{{ activity.user }}</div>
                <div class="activity-action">{{ activity.action }}</div>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  User,
  UserFilled,
  ShoppingCart,
  Money,
  TrendCharts,
  PieChart
} from '@element-plus/icons-vue'

// 趋势周期
const trendPeriod = ref('7d')

// 最近活动数据
const activities = ref([
  {
    user: '管理员',
    action: '新增了用户 "testuser"',
    time: '2024-01-01 10:30:00',
    avatar: ''
  },
  {
    user: '张三',
    action: '修改了个人资料',
    time: '2024-01-01 09:15:00',
    avatar: ''
  },
  {
    user: '李四',
    action: '提交了订单 #123456',
    time: '2024-01-01 08:45:00',
    avatar: ''
  },
  {
    user: '王五',
    action: '发表了评论',
    time: '2024-01-01 08:00:00',
    avatar: ''
  },
  {
    user: '赵六',
    action: '登录了系统',
    time: '2024-01-01 07:30:00',
    avatar: ''
  }
])
</script>

<style scoped>
.dashboard-overview {
  padding: 20px;
}

.page-title {
  margin: 0 0 24px 0;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

/* 统计卡片样式 */
.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  border: none;
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.active-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.revenue-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 图表区域样式 */
.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  border: none;
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
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
  font-size: 64px;
  margin-bottom: 16px;
}

.chart-placeholder.pie .el-icon {
  color: #409eff;
}

.chart-placeholder p {
  margin: 0;
  font-size: 14px;
}

/* 最近活动样式 */
.activities-card {
  border: none;
}

.activities-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.activity-avatar {
  margin-right: 12px;
}

.activity-content {
  flex: 1;
}

.activity-user {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.activity-action {
  color: #666;
  font-size: 14px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 12px;
  }
}
</style>