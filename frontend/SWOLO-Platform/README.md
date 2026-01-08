# SWOLO-Platform 前端

SWOLO-Platform 前端是一个基于 Vue 3 和 Electron 构建的现代化桌面应用程序，为化学实验提供直观易用的用户界面。

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **TypeScript** - JavaScript的类型超集
- **Electron** - 跨平台桌面应用开发框架
- **Vite** - 下一代前端构建工具
- **Tailwind CSS** - 实用优先的CSS框架

## 功能模块

### 1. 仪表板 (Dashboard)
- 系统状态概览
- 服务器连接状态监控
- 最近实验数据展示

### 2. 滴定实验 (Titration)
- 实时摄像头预览
- 滴定过程监测
- 颜色变化检测
- 终点判断
- 体积和浓度计算
- 实验数据记录

### 3. 浓度检测 (Concentration Detection)
- 溶液浓度分析
- 实时检测结果显示
- 数据可视化

### 4. 智能表征 (Characterization)
- 化学物质识别
- 特征分析
- 模型管理

### 5. 数据分析 (Data Analysis)
- 实验数据统计
- 结果可视化
- 数据导出功能

## 项目结构

```
frontend/SWOLO-Platform/
├── src/
│   ├── components/           # Vue组件
│   │   ├── Layout.vue        # 主布局组件
│   │   ├── Dashboard.vue     # 仪表板组件
│   │   ├── Titration.vue     # 滴定实验组件
│   │   ├── Concentration.vue # 浓度检测组件
│   │   ├── Characterization.vue # 智能表征组件
│   │   ├── DataAnalysis.vue  # 数据分析组件
│   │   └── ModelManager.vue  # 模型管理组件
│   ├── services/             # API服务
│   │   └── apiService.ts     # API服务封装
│   ├── router/               # 路由配置
│   │   └── index.ts          # 路由定义
│   ├── App.vue               # 根组件
│   └── main.ts               # 应用入口
├── public/                   # 静态资源
├── electron-main.ts          # Electron主进程
├── preload.ts                # Electron预加载脚本
├── vite.config.ts            # Vite配置
├── package.json              # 项目配置
└── tsconfig.json             # TypeScript配置
```

## 组件说明

### Layout.vue
应用的主布局组件，包含：
- 顶部导航栏
- 侧边菜单
- 服务器连接状态显示
- 页面切换功能

### Titration.vue
滴定实验功能组件，包含：
- 摄像头控制
- 串口通信
- 实时检测结果显示
- 实验数据表格

### ModelManager.vue
模型管理组件，提供：
- 模型切换功能
- 模型状态监控
- 模型性能指标

## API 服务集成

前端通过 [apiService.ts](./src/services/apiService.ts) 与后端YOLO预测服务通信，支持以下功能：
- 模型切换
- 图像预测
- 服务器状态检查
- 实验数据传输

## Electron 集成

### 主进程 (electron-main.ts)
- 窗口管理
- 系统菜单
- IPC通信处理
- 配置加载
- 服务器连接检查

### 预加载脚本 (preload.ts)
- 安全的API暴露
- IPC通信桥接
- 系统功能访问

## 开发设置

### 安装依赖
```bash
npm install
```

### 开发模式
```bash
# 启动Vite开发服务器
npm run dev

# 启动Electron应用（需先启动开发服务器）
npm run electron:dev
```

### 构建应用
```bash
# 构建Web应用
npm run build

# 构建Electron应用
npm run electron:dist
```

## 环境配置

项目支持以下环境变量：
- `YOLO_SERVER_URL` - YOLO预测服务地址（默认：http://localhost:5000）

## 测试

### 单元测试
```bash
npm run test:unit
```

### 端到端测试
```bash
npx playwright test
```

## 贡献

请遵循项目代码规范：
- 使用TypeScript进行类型安全编程
- 遵循Vue 3 Composition API风格
- 组件命名采用PascalCase
- 代码格式化使用Prettier
- 提交信息遵循约定式提交规范

## 许可证

MIT License