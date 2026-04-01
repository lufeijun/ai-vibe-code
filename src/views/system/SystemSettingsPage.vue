<template>
  <div class="system-settings-page">
    <div class="page-header">
      <h2 class="page-title">系统设置</h2>
      <div class="page-actions">
        <el-button type="primary" :icon="Check" @click="handleSave">保存设置</el-button>
        <el-button type="success" :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
    </div>

    <div class="page-content">
      <el-card class="content-card">
        <el-tabs v-model="activeTab">
          <!-- 基本设置 -->
          <el-tab-pane label="基本设置" name="basic">
            <el-form
              ref="basicFormRef"
              :model="basicSettings"
              :rules="basicRules"
              label-width="120px"
              class="settings-form"
            >
              <el-form-item label="系统名称" prop="systemName">
                <el-input
                  v-model="basicSettings.systemName"
                  placeholder="请输入系统名称"
                  style="width: 300px"
                />
              </el-form-item>
              <el-form-item label="系统Logo" prop="logo">
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :show-file-list="false"
                  :auto-upload="false"
                  :on-change="handleLogoChange"
                >
                  <img v-if="basicSettings.logo" :src="basicSettings.logo" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon">
                    <Plus />
                  </el-icon>
                </el-upload>
                <div class="upload-tip">建议尺寸：200x60像素，支持JPG、PNG格式</div>
              </el-form-item>
              <el-form-item label="版权信息" prop="copyright">
                <el-input
                  v-model="basicSettings.copyright"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入版权信息"
                  style="width: 400px"
                />
              </el-form-item>
              <el-form-item label="备案号" prop="icp">
                <el-input
                  v-model="basicSettings.icp"
                  placeholder="请输入备案号"
                  style="width: 300px"
                />
              </el-form-item>
              <el-form-item label="系统版本" prop="version">
                <el-input
                  v-model="basicSettings.version"
                  placeholder="请输入系统版本"
                  style="width: 200px"
                  disabled
                />
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 安全设置 -->
          <el-tab-pane label="安全设置" name="security">
            <el-form
              ref="securityFormRef"
              :model="securitySettings"
              :rules="securityRules"
              label-width="140px"
              class="settings-form"
            >
              <el-form-item label="登录失败锁定" prop="loginLockEnabled">
                <el-switch v-model="securitySettings.loginLockEnabled" />
                <div class="form-tip">
                  开启后，用户连续登录失败5次将被锁定30分钟
                </div>
              </el-form-item>
              <el-form-item label="密码强度要求" prop="passwordStrength">
                <el-select
                  v-model="securitySettings.passwordStrength"
                  placeholder="请选择密码强度"
                  style="width: 200px"
                >
                  <el-option label="低 (至少6位)" value="low" />
                  <el-option label="中 (至少8位，含字母数字)" value="medium" />
                  <el-option label="高 (至少10位，含大小写字母、数字、特殊字符)" value="high" />
                </el-select>
              </el-form-item>
              <el-form-item label="会话超时时间" prop="sessionTimeout">
                <el-input-number
                  v-model="securitySettings.sessionTimeout"
                  :min="10"
                  :max="480"
                  :step="10"
                  style="width: 120px"
                />
                <span class="unit">分钟</span>
                <div class="form-tip">
                  用户无操作后自动退出登录的时间，建议设置为30-60分钟
                </div>
              </el-form-item>
              <el-form-item label="IP访问限制" prop="ipWhitelist">
                <el-input
                  v-model="securitySettings.ipWhitelist"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入允许访问的IP地址，每行一个"
                  style="width: 400px"
                />
                <div class="form-tip">
                  留空表示不限制IP访问。支持IP段，如：192.168.1.0/24
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 邮件设置 -->
          <el-tab-pane label="邮件设置" name="email">
            <el-form
              ref="emailFormRef"
              :model="emailSettings"
              :rules="emailRules"
              label-width="120px"
              class="settings-form"
            >
              <el-form-item label="SMTP服务器" prop="smtpHost">
                <el-input
                  v-model="emailSettings.smtpHost"
                  placeholder="请输入SMTP服务器地址"
                  style="width: 300px"
                />
              </el-form-item>
              <el-form-item label="SMTP端口" prop="smtpPort">
                <el-input-number
                  v-model="emailSettings.smtpPort"
                  :min="1"
                  :max="65535"
                  style="width: 120px"
                />
              </el-form-item>
              <el-form-item label="发件人邮箱" prop="senderEmail">
                <el-input
                  v-model="emailSettings.senderEmail"
                  placeholder="请输入发件人邮箱"
                  style="width: 300px"
                />
              </el-form-item>
              <el-form-item label="发件人名称" prop="senderName">
                <el-input
                  v-model="emailSettings.senderName"
                  placeholder="请输入发件人名称"
                  style="width: 300px"
                />
              </el-form-item>
              <el-form-item label="邮箱密码" prop="smtpPassword">
                <el-input
                  v-model="emailSettings.smtpPassword"
                  type="password"
                  placeholder="请输入邮箱密码或授权码"
                  style="width: 300px"
                  show-password
                />
              </el-form-item>
              <el-form-item label="SSL加密" prop="sslEnabled">
                <el-switch v-model="emailSettings.sslEnabled" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :icon="Message" @click="handleTestEmail">
                  测试邮件发送
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import {
  Check,
  Refresh,
  Plus,
  Message
} from '@element-plus/icons-vue'

// 当前激活的选项卡
const activeTab = ref('basic')

// 表单引用
const basicFormRef = ref<FormInstance>()
const securityFormRef = ref<FormInstance>()
const emailFormRef = ref<FormInstance>()

// 基本设置
const basicSettings = reactive({
  systemName: '后台管理系统',
  logo: '',
  copyright: '© 2024 后台管理系统 版权所有',
  icp: '京ICP备12345678号',
  version: 'v1.0.0'
})

const basicRules = {
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 安全设置
const securitySettings = reactive({
  loginLockEnabled: true,
  passwordStrength: 'medium',
  sessionTimeout: 30,
  ipWhitelist: ''
})

const securityRules = {
  passwordStrength: [
    { required: true, message: '请选择密码强度', trigger: 'change' }
  ]
}

// 邮件设置
const emailSettings = reactive({
  smtpHost: 'smtp.example.com',
  smtpPort: 465,
  senderEmail: 'noreply@example.com',
  senderName: '后台管理系统',
  smtpPassword: '',
  sslEnabled: true
})

const emailRules = {
  smtpHost: [
    { required: true, message: '请输入SMTP服务器地址', trigger: 'blur' }
  ],
  senderEmail: [
    { required: true, message: '请输入发件人邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 处理Logo上传
const handleLogoChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    basicSettings.logo = e.target?.result as string
  }
  reader.readAsDataURL(file.raw)
}

// 保存设置
const handleSave = async () => {
  try {
    // 验证并保存基本设置
    if (activeTab.value === 'basic') {
      await basicFormRef.value?.validate()
    }
    // 验证并保存安全设置
    if (activeTab.value === 'security') {
      await securityFormRef.value?.validate()
    }
    // 验证并保存邮件设置
    if (activeTab.value === 'email') {
      await emailFormRef.value?.validate()
    }

    // TODO: 调用API保存设置
    ElMessage.success('设置保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请检查表单')
  }
}

// 刷新设置
const handleRefresh = () => {
  ElMessage.info('刷新设置')
  // TODO: 从API重新加载设置
}

// 测试邮件发送
const handleTestEmail = async () => {
  try {
    await emailFormRef.value?.validate()
    ElMessage.info('正在发送测试邮件...')
    // TODO: 调用API发送测试邮件
    setTimeout(() => {
      ElMessage.success('测试邮件发送成功')
    }, 1000)
  } catch (error) {
    console.error('邮件测试失败:', error)
    ElMessage.error('邮件测试失败，请检查配置')
  }
}
</script>

<style scoped>
.system-settings-page {
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

.settings-form {
  padding: 20px;
}

.form-tip {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
  line-height: 1.4;
}

.unit {
  margin-left: 10px;
  color: #666;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 60px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 60px;
  line-height: 60px;
  text-align: center;
}

.avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: contain;
}

.upload-tip {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
</style>