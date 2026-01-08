# SWOLO-Platform 开发和部署指南

本文档详细介绍SWOLO-Platform的开发流程、环境配置、部署步骤和最佳实践。

## 开发环境搭建

### 系统要求

- **操作系统**: Windows 7+, macOS 10.12+, Linux (Ubuntu 16.04+, CentOS 7+)
- **Node.js**: 16.x 或更高版本
- **Python**: 3.7 - 3.11
- **npm**: 8.x 或更高版本
- **Git**: 2.x 或更高版本

### 安装步骤

1. **克隆项目仓库**
```bash
git clone https://github.com/Luckzay/SWOLO-Platform2.0
cd SWOLO-Platform2.0
```

2. **安装前端依赖**
```bash
cd frontend/SWOLO-Platform
npm install
```

3. **安装后端依赖**
```bash
cd ../../yolo_prediction_service
pip install -r requirements.txt
```

## 开发流程

### 前端开发

1. **启动开发服务器**
```bash
# 进入前端目录
cd frontend/SWOLO-Platform

# 启动Vite开发服务器
npm run dev

# 在新终端启动Electron应用
npm run electron:dev
```

2. **开发工具**
- 使用VS Code推荐的扩展
- 启用ESLint和Prettier进行代码检查和格式化
- 使用Vue DevTools进行调试

3. **代码规范**
- 遵循Vue 3 Composition API风格
- 使用TypeScript进行类型安全编程
- 组件命名采用PascalCase
- 文件命名采用kebab-case

### 后端开发

1. **启动YOLO服务**
```bash
# 进入YOLO服务目录
cd yolo_prediction_service

# 启动服务（Windows）
start_server.bat

# 启动服务（Linux/Mac）
./start_server.sh
```

2. **模型开发**
- 在[nets](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/nets/)目录下开发YOLOv5模型
- 在[netsv8](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/netsv8/)目录下开发YOLOv8模型
- 在[utils](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/utils/)和[utilsv8](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/utilsv8/)目录下添加工具函数

3. **测试验证**
```bash
# 验证GLU模型
python verify_glu.py

# 运行简单测试
python simple_test.py

# 运行完整测试
python test_glu_final.py
```

## 部署指南

### 生产环境部署

#### 1. 构建前端应用

```bash
cd frontend/SWOLO-Platform

# 构建Web应用
npm run build

# 构建Electron桌面应用
npm run electron:dist
```

#### 2. 部署YOLO服务

##### 方法一：独立服务部署

```bash
cd yolo_prediction_service

# 安装生产依赖
pip install -r requirements.txt

# 设置环境变量
export HOST=0.0.0.0
export PORT=5000
export USE_CUDA=true  # 如有GPU支持

# 启动服务
python server.py
```

##### 方法二：使用Gunicorn部署（推荐用于生产环境）

```bash
# 安装Gunicorn
pip install gunicorn

# 启动服务
gunicorn -w 4 -b 0.0.0.0:5000 server:app
```

##### 方法三：使用Docker部署

创建 [Dockerfile](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/Dockerfile):

```dockerfile
FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY . .

EXPOSE 5000

CMD ["python", "server.py"]
```

构建并运行容器：

```bash
docker build -t swolo-yolo-service .
docker run -d -p 5000:5000 --name swolo-service swolo-yolo-service
```

#### 3. 配置反向代理（可选）

如使用Nginx作为反向代理：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:5173;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/ {
        proxy_pass http://localhost:5000/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 部署注意事项

1. **安全配置**
   - 使用HTTPS加密通信
   - 设置适当的CORS策略
   - 实施API速率限制
   - 验证和清理用户输入

2. **性能优化**
   - 启用GPU加速（如果有CUDA支持）
   - 调整模型推理参数以平衡准确性和速度
   - 使用缓存减少重复计算

3. **日志和监控**
   - 配置适当的日志级别
   - 监控服务健康状况
   - 记录关键操作和错误

## 模型管理

### 模型训练

1. **准备训练数据**
   - 将图像数据放在指定目录
   - 创建标注文件（XML或TXT格式）
   - 生成类别文件

2. **训练新模型**
   - 修改训练配置文件
   - 运行训练脚本
   - 验证模型性能

3. **模型评估**
   - 使用验证集评估模型
   - 计算mAP等指标
   - 调整超参数

### 模型部署

1. **模型转换**
   - 将训练好的模型转换为推理格式
   - 优化模型大小和性能

2. **模型更新**
   - 将新模型文件复制到[model_data](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/model_data/)目录
   - 更新相应的配置文件
   - 重启服务或使用模型切换功能

## 环境变量配置

### 前端环境变量

- `YOLO_SERVER_URL`: YOLO服务API基础URL
- `VUE_APP_API_TIMEOUT`: API请求超时时间

### 后端环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| HOST | 0.0.0.0 | 服务器监听地址 |
| PORT | 5000 | 服务器端口 |
| MODEL_PATH | ./model_data/MOF.pth | 模型文件路径 |
| CLASSES_PATH | ./model_data/voc_classes.txt | 类别文件路径 |
| ANCHORS_PATH | ./model_data/yolo_anchors.txt | 锚框文件路径 |
| USE_CUDA | false | 是否启用CUDA |
| LOG_LEVEL | INFO | 日志级别 |

## 故障排除

### 常见问题

1. **服务无法启动**
   - 检查端口是否被占用
   - 验证依赖包是否正确安装
   - 检查模型文件路径是否正确

2. **模型加载失败**
   - 验证模型文件完整性
   - 检查模型版本兼容性
   - 确认类别文件匹配

3. **CUDA相关错误**
   - 检查CUDA版本兼容性
   - 验证GPU驱动程序
   - 确认PyTorch CUDA版本

### 调试技巧

1. **前端调试**
   - 使用Electron DevTools
   - 检查网络请求
   - 查看控制台错误

2. **后端调试**
   - 查看服务日志
   - 使用Python调试器
   - 检查API响应

## 性能调优

### 前端性能优化

- 组件懒加载
- 图片压缩和缓存
- 代码分割
- 使用虚拟滚动处理大量数据

### 后端性能优化

- 模型量化和压缩
- 批处理请求
- GPU内存管理
- 异步处理

## 备份和恢复

### 数据备份

定期备份以下内容：
- 模型文件
- 配置文件
- 用户数据（如果有）
- 日志文件

### 系统恢复

1. 恢复备份文件
2. 重新安装依赖
3. 重启服务

## 更新和维护

### 版本升级

1. **备份现有系统**
2. **下载新版本**
3. **更新依赖**
4. **迁移配置**
5. **测试功能**
6. **上线新版本**

### 维护任务

- 定期更新依赖包
- 监控系统性能
- 清理日志文件
- 备份重要数据

## 最佳实践

1. **代码质量**
   - 编写单元测试
   - 代码审查
   - 文档更新

2. **安全性**
   - 输入验证
   - 权限控制
   - 数据加密

3. **可维护性**
   - 模块化设计
   - 清晰的文档
   - 版本控制

4. **性能**
   - 负载测试
   - 性能监控
   - 优化瓶颈

## 联系支持

如遇到问题，请参考：
- 项目文档
- 提交GitHub Issue
- 联系技术支持团队