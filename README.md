igno# SWOLO-Platform 2.0

SWOLO-Platform 2.0 是一个基于人工智能技术的化学实验智能平台，结合了YOLO目标检测算法与现代化桌面应用框架，用于化学实验过程中的自动化分析与检测。

## 项目概述

本项目主要分为两个核心部分：

1. **前端界面 (Frontend)** - 基于 Vue 3 和 Electron 构建的现代化桌面应用程序
2. **YOLO预测服务 (Prediction Service)** - 基于YOLOv5/v8的目标检测模型，用于化学实验图像分析

## 核心功能

- **滴定实验分析** - 自动识别滴定过程中的颜色变化、终点判断、体积和浓度检测
- **浓度检测** - 通过图像分析技术精确测量溶液浓度
- **智能表征** - 化学物质的自动识别与特征分析
- **数据分析** - 实验数据记录、可视化和统计分析

## 技术栈

### 前端技术
- **Vue 3** - 响应式前端框架
- **TypeScript** - 类型安全的JavaScript超集
- **Electron** - 桌面应用框架
- **Vite** - 现代化构建工具
- **Tailwind CSS** - 样式框架

### AI/ML 技术
- **PyTorch** - 深度学习框架
- **YOLOv5/YOLOv8** - 目标检测算法
- **Flask** - Python Web框架用于API服务

## 项目结构

```
SWOLO-Platform2.0/
├── frontend/
│   └── SWOLO-Platform/           # Electron桌面应用
│       ├── src/
│       │   ├── components/       # Vue组件
│       │   ├── services/         # API服务
│       │   └── router/           # 路由配置
│       ├── public/               # 静态资源
│       ├── electron-main.ts      # Electron主进程
│       └── preload.ts            # 预加载脚本
└── yolo_prediction_service/      # YOLO预测服务
    ├── nets/                     # YOLOv5网络结构
    ├── netsv8/                   # YOLOv8网络结构
    ├── utils/                    # 工具函数
    ├── utilsv8/                  # YOLOv8工具函数
    ├── model_data/               # 模型数据文件
    ├── server.py                 # Flask API服务
    ├── yolo_service.py           # YOLO服务封装
    └── requirements.txt          # Python依赖
```

## 快速开始

### 环境要求

- Node.js >= 16.0.0
- Python >= 3.7
- npm 或 yarn

### 前端启动

```bash
# 进入前端目录
cd frontend/SWOLO-Platform

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 启动Electron应用
npm run electron:dev
```

### YOLO服务启动

```bash
# 进入YOLO服务目录
cd yolo_prediction_service

# 安装Python依赖
pip install -r requirements.txt

# 启动服务 (Windows)
start_server.bat

# 启动服务 (Linux/Mac)
./start_server.sh
```

## 架构说明

### 通信流程

1. Electron前端应用通过HTTP API与YOLO预测服务通信
2. 图像数据从前端传输到后端进行AI分析
3. 检测结果返回前端进行显示和处理

### 模型支持

- **MOF模型** - 用于常规化学物质检测
- **浓度检测模型** - 专门用于浓度分析
- **GLU模型** - 用于特定化学物质检测(YOLOv8)

## 贡献指南

请参阅 [CONTRIBUTING.md](CONTRIBUTING.md) 获取详细的贡献指南。

## 许可证

本项目采用 MIT 许可证 - 详情请参见 [LICENSE](LICENSE) 文件。

## 联系方式

如有任何问题或建议，请联系1617022583@qq.com。