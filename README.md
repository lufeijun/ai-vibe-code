# Vue 3 + Element Plus 后台管理系统 Demo

这是一个完整的 Vue 3 后台管理系统前端 demo，使用现代前端技术栈构建。

## 技术栈

- **Vue 3** - 前端框架
- **TypeScript** - 类型安全
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Element Plus** - UI 组件库
- **Vite** - 构建工具
- **Axios** - HTTP 客户端

## 功能特性

1. **登录页面**
   - 用户名/密码表单验证
   - 记住我功能
   - 模拟登录请求
   - 路由守卫认证

2. **后台首页**
   - 用户信息展示
   - 响应式布局
   - 退出登录功能

3. **状态管理**
   - Pinia 状态持久化
   - localStorage 存储登录状态
   - 类型安全的 Store

## 项目结构

```
vue/
├── src/
│   ├── assets/          # 静态资源
│   │   └── styles/      # 全局样式
│   ├── router/          # 路由配置
│   │   └── index.ts     # 路由定义和守卫
│   ├── stores/          # 状态管理
│   │   ├── auth.ts      # 认证状态
│   │   └── index.ts     # Pinia 配置
│   ├── views/           # 页面组件
│   │   ├── login/       # 登录页面
│   │   └── dashboard/   # 后台首页
│   ├── App.vue          # 根组件
│   └── main.ts          # 应用入口
├── index.html           # HTML 模板
├── vite.config.ts       # Vite 配置
├── tsconfig.json        # TypeScript 配置
├── package.json         # 依赖配置
└── README.md            # 项目说明
```

## 快速开始

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

## 使用说明

1. 启动项目后，访问 http://localhost:3000
2. 输入任意用户名和密码即可登录
3. 登录成功后跳转到后台首页
4. 点击退出登录按钮返回登录页面

## 核心实现

### 路由守卫
在 `src/router/index.ts` 中实现了路由守卫，自动检查登录状态并重定向。

### 状态管理
使用 Pinia 管理登录状态，支持 localStorage 持久化。

### 表单验证
使用 Element Plus 的表单验证规则，确保输入有效性。

### 响应式设计
使用 CSS Flexbox 和 Grid 实现响应式布局。

## 扩展建议

如需扩展功能，可参考以下方向：

1. **用户管理模块** - 用户列表、增删改查
2. **角色管理模块** - 角色权限分配
3. **菜单管理模块** - 动态侧边栏菜单
4. **API 集成** - 连接真实后端接口
5. **国际化** - 多语言支持
6. **主题切换** - 亮色/暗色模式

## 技术细节

- **自动导入**：使用 `unplugin-auto-import` 和 `unplugin-vue-components` 自动导入 Element Plus 组件
- **类型安全**：完整的 TypeScript 类型定义
- **代码规范**：ESLint 和 Prettier 配置
- **开发体验**：热重载、TypeScript 智能提示

## 许可证

MIT