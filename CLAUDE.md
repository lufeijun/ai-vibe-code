# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供操作本代码仓库的指引。

## 项目概览

基于 Vue 3 和 Element Plus 构建的后台管理面板演示项目，包含完整的认证流程和模块化布局。包括登录页面、仪表板概览、用户管理和系统设置模块。使用现代 Vue 3 生态工具：TypeScript、Vue Router 4、Pinia 状态管理和 Vite 构建工具。

## 开发命令

来自 `package.json` 的标准 npm 脚本：

- `npm run dev` - 启动开发服务器，访问 http://localhost:3000
- `npm run build` - 使用 vue-tsc 进行类型检查，然后构建生产版本
- `npm run preview` - 本地预览生产构建版本

目前未配置 lint 或测试命令。

## 架构与核心模式

### 技术栈
- **Vue 3** - 使用 `<script setup>` 语法的组合式 API
- **TypeScript** - 类型安全
- **Vue Router 4** - 基于路由的代码分割路由管理
- **Pinia** - 集中式状态管理
- **Element Plus** - 自动导入的 UI 组件库
- **Vite** - 支持 HMR 的构建工具
- **Axios** - 用于 API 请求的 HTTP 客户端

### 项目结构
- `src/views/` - 页面级 Vue 组件（登录、仪表板、用户管理、系统设置）
- `src/layouts/` - 布局组件（认证区域使用 `DashboardLayout.vue`）
- `src/router/` - 路由定义和导航守卫
- `src/stores/` - Pinia 状态管理（`auth.ts` 处理认证）
- `src/utils/` - 工具函数（`request.ts` 用于 API 调用）
- `src/assets/styles/` - 全局 SCSS 样式
- 自动生成的类型文件：`auto-imports.d.ts` 和 `components.d.ts`

### 认证流程
1. 使用 Pinia 在 `stores/auth.ts` 中管理登录状态
2. JWT 令牌存储在 `localStorage` 中（键名：`token`）
3. 状态持久化到 `localStorage`（键名：`isLoggedIn`、`username`、`remember`）
4. `router/index.ts` 中的路由守卫直接检查 `localStorage` 进行认证
5. 未认证用户访问受保护路由会被重定向到 `/login`
6. 已认证用户访问 `/login` 会被重定向到 `/dashboard`
7. API 请求通过请求拦截器自动包含 `Authorization: Bearer <token>` 请求头

**重要**：认证状态存储和路由守卫都与 `localStorage` 交互——修改认证逻辑时确保同步。

### 布局模式
- 认证路由使用 `DashboardLayout.vue` 布局组件
- 布局包含顶部导航菜单（主模块）和动态侧边菜单（次级路由）
- 侧边菜单配置在 `DashboardLayout.vue` 中根据活动顶部菜单集中管理
- 路由结构使用嵌套路由，布局作为父组件

### 路由配置
路由定义在 `router/index.ts` 中，包含元数据：
- `requiresAuth: boolean` - 路由是否需要认证
- `title: string` - 浏览器标签页标题

路由结构：
- `/login` - 公共登录页面
- `/dashboard` - 仪表板模块，包含嵌套路由（`/dashboard/overview`、`/dashboard/stats`）
- `/user` - 用户管理模块，包含嵌套路由（`/user/list`、`/user/role`）
- `/system` - 系统设置模块，包含嵌套路由（`/system/settings`、`/system/logs`）

### 关键实现细节
- **路径别名**：`@` 指向 `src/` 目录（在 `vite.config.ts` 中配置）
- **自动导入**：通过 `unplugin-auto-import` 和 `unplugin-vue-components` 自动导入 Element Plus 组件和 Vue 组合式函数
- **Element Plus CSS**：仅导入 CSS（无主题自定义），通过 `vite.config.ts` 中的 `importStyle: 'css'` 配置
- **表单验证**：使用 `el-form` 组件配合 Element Plus 表单验证规则
- **响应式设计**：使用 Flexbox/Grid 自定义 CSS 实现响应式布局
- **图标使用**：从 `@element-plus/icons-vue` 导入 Element Plus 图标
- **样式**：使用 `<style scoped>` 的 SCSS 作用域样式
- **API 代理**：Vite 开发服务器将 `/api` 代理到 `http://192.168.69.132:8080`

### 组件规范
- 页面组件使用 TypeScript 和 `<script setup lang="ts">`
- Element Plus 组件自动导入（无需手动导入）
- 图标从 `@element-plus/icons-vue` 手动导入
- SCSS 样式使用 `<style scoped>` 进行组件隔离
- 表单组件使用 `el-form` 配合验证规则，通过 `ref` 引用
- 布局组件使用基于路由的计算属性控制活动菜单状态

## 开发注意事项

- 开发服务器运行在 3000 端口（可在 `vite.config.ts` 中配置）
- 目前未配置 ESLint 或 Prettier
- 目前未配置测试框架
- 登录表单接受任意用户输入（无真实认证验证）
- 项目 UI 标签和注释使用中文
- **API 集成**：Axios 在 `src/utils/request.ts` 中配置，包含用于令牌处理的拦截器
- **扩展计划**：参考 README.md 了解建议的功能扩展

## 常见问题与解决方案

### Vue Router 警告：child without a name
**警告信息**：`The route named "X" has a child without a name and an empty path`
**原因**：父路由有 `name`，但其空路径子路由（`path: ''`）没有 `name`
**解决**：给空路径子路由添加 `name` 属性，如 `name: 'DashboardIndex'`

### Element Plus 废弃警告：type.text
**警告信息**：`type.text is about to be deprecated in version 3.0.0, please use link instead`
**原因**：`el-button type="text"` 在 Element Plus 3.0 中将被废弃
**解决**：将 `type="text"` 改为 `link`，即 `<el-button link>`

### Element Plus 标签类型验证失败
**警告信息**：`Invalid prop: validation failed for prop "type". Expected one of [...], got value ""`
**原因**：`el-tag` 的 `type` 属性不能接收空字符串，有效值为：`'primary' | 'success' | 'info' | 'warning' | 'danger'`
**解决**：将 `:type="condition ? 'danger' : ''"` 改为 `:type="condition ? 'danger' : 'info'"`

### 新增记录时ID为0的问题
**问题**：使用PostgreSQL + MyBatis-Plus时，新增记录后返回的对象ID为0，而不是数据库生成的自增ID
**原因**：前端在调用创建接口时发送了`id: 0`字段，导致MyBatis-Plus不能正确处理自增ID回填
**解决**：
1. 前端：在调用创建接口前，从payload中删除id字段
```typescript
const payload = { ...formData }
if (!isEdit.value) {
  delete payload.id
}
```
2. 后端实体：添加`@KeySequence`注解指定PostgreSQL序列名，并确保`@TableId(type = IdType.AUTO)`
```java
@TableName("coupons")
@KeySequence(value = "coupons_id_seq", dbType = DbType.POSTGRE_SQL)
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
}
```
