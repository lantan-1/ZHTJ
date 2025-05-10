# 蓝炭系统前端

这是蓝炭系统的前端项目，基于Vue 3 + TypeScript + Element Plus + Vue Router + Pinia构建。

## 项目介绍

蓝炭系统是一个组织活动管理系统，用于管理组织成员和组织活动。系统功能包括：

- 用户注册与登录
- 个人信息管理
- 活动管理（添加、编辑、删除、查询）
- 成员管理

## 技术栈

- Vue 3 - 渐进式JavaScript框架
- TypeScript - JavaScript的超集，提供静态类型
- Element Plus - 基于Vue 3的组件库
- Vue Router - Vue.js官方路由管理器
- Pinia - Vue的状态管理库
- Axios - 基于Promise的HTTP客户端

## 开发环境配置

### 前提条件

- Node.js 18.x或更高版本
- npm 9.x或更高版本

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

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
src/
├── api/              # API请求
├── assets/           # 静态资源
├── components/       # 公共组件
├── router/           # 路由配置
├── stores/           # Pinia状态管理
├── types/            # TypeScript类型声明
├── utils/            # 工具函数
├── views/            # 页面组件
├── App.vue           # 根组件
├── main.ts           # 入口文件
└── style.css         # 全局样式
```

## 快速开始

1. 确保已安装Node.js和npm
2. 克隆项目到本地
3. 在项目根目录运行 `npm install` 安装依赖
4. 使用 `npm run dev` 启动开发服务器
5. 打开浏览器访问 `http://localhost:5173`

## 登录页面预览

登录页面已完全复刻原系统的设计，包括背景、Logo和表单样式。用户可以使用身份证号码和密码登录，也支持验证码验证。

## 后端API配置

系统默认连接到 `http://localhost:8080` 作为后端API服务器。如需修改，请编辑 `vite.config.ts` 文件中的proxy配置：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-server.com',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## 贡献指南

欢迎提交问题报告或功能建议。如果您想贡献代码，请先fork本仓库，然后提交pull request。

## API接口

API接口文档详见`api.txt`文件。
