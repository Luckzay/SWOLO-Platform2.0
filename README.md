# SWOLO-Platform2.0

现代化科学实验分析平台，结合了前端界面、后端服务和YOLO预测服务，提供完整的实验数据处理和分析解决方案。

## 项目结构

- `frontend/SWOLO-Platform`: 基于Vue.js的前端应用，提供现代化科技感界面
- `backend`: 基于Spring Boot的后端服务，使用MySQL数据库
- `yolo_prediction_service`: YOLO目标检测和浓度预测服务

## 功能特性

### 前端 (Vue.js)
- 现代化科技感界面设计
- 实验数据可视化
- 用户友好的交互体验
- 响应式布局

### 后端 (Spring Boot)
- 完整的用户管理系统
- 基于JWT的身份验证
- 实验数据管理API
- 支持多种实验数据类型：
  - 目标检测数据
  - 浓度测量数据
  - 通用实验数据
- RESTful API设计

### YOLO预测服务 (Python)
- 目标检测功能
- 浓度预测功能
- 基于深度学习的图像分析
- 高精度预测算法

## 技术栈

### 前端
- Vue.js 3
- TypeScript
- Vite
- CSS

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL
- JWT

### 预测服务
- Python
- PyTorch
- YOLO算法
- OpenCV

## 快速开始

### 后端服务设置

1. 确保已安装Java 17和Maven
2. 安装并启动MySQL服务
3. 创建数据库：
   ```sql
   CREATE DATABASE swole_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
4. 进入后端目录并运行：
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   或在IDE中运行[PlatformApplication.java](file:///F:/SWOLO-Platform2.0/backend/src/main/java/com/swole/platform/PlatformApplication.java)

### 前端设置

1. 确保已安装Node.js和npm
2. 进入前端目录：
   ```bash
   cd frontend/SWOLO-Platform
   npm install
   npm run dev
   ```

### YOLO预测服务设置

1. 确保已安装Python 3.8+
2. 进入预测服务目录：
   ```bash
   cd yolo_prediction_service
   pip install -r requirements.txt
   ```
3. 启动服务：
   ```bash
   python server.py
   ```

## API文档

- 后端API文档: [backend/API_DOCUMENTATION.md](file:///F:/SWOLO-Platform2.0/backend/API_DOCUMENTATION.md)
- YOLO服务API文档: [yolo_prediction_service/API_DOCUMENTATION.md](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/API_DOCUMENTATION.md)

## 开发指南

详细开发指南请参见[DEVELOPMENT_GUIDE.md](file:///F:/SWOLO-Platform2.0/DEVELOPMENT_GUIDE.md)。

## 贡献

欢迎贡献代码！请遵循以下步骤：
1. Fork仓库
2. 创建功能分支
3. 提交更改
4. 发起Pull Request

## 许可证

本项目采用MIT许可证。