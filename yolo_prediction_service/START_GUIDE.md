# YOLO预测服务启动指南

## 概述

YOLO预测服务是一个独立的HTTP API服务，用于提供目标检测功能。它允许客户端通过HTTP请求进行图像分析，而无需本地安装Python或PyTorch环境。

## 快速启动

### Windows
```cmd
cd yolo_prediction_service
start_server.bat
```

### Linux/macOS
```bash
cd yolo_prediction_service
chmod +x start_server.sh
./start_server.sh
```

### 手动启动
```bash
cd yolo_prediction_service
python server.py
```

## 环境变量

可以通过环境变量配置服务：

| 环境变量 | 默认值 | 说明 |
|---------|--------|------|
| HOST | 0.0.0.0 | 服务器地址 |
| PORT | 5000 | 服务器端口 |
| MODEL_PATH | ./model_data/MOF.pth | 模型文件路径 |
| CLASSES_PATH | ./model_data/voc_classes.txt | 类别文件路径 |
| ANCHORS_PATH | ./model_data/yolo_anchors.txt | 锚框文件路径 |
| USE_CUDA | false | 是否使用CUDA |

### 设置环境变量示例

**Windows:**
```cmd
set HOST=0.0.0.0
set PORT=8080
set USE_CUDA=true
python server.py
```

**Linux/macOS:**
```bash
export HOST=0.0.0.0
export PORT=8080
export USE_CUDA=true
python server.py
```

## API端点

### 健康检查
```
GET /
GET /health
```

### 图像预测
```
POST /predict
```

支持两种输入方式：
- 文件上传：将图像文件作为 `image` 参数上传
- Base64编码：将图像的base64编码作为 `image_base64` 参数传递

可选参数：
- `return_image`：是否返回处理后的图像（true/false）

### 简单预测
```
POST /predict_simple
```

与 `/predict` 接口类似，但只返回检测结果，不返回处理后的图像。

### 模型信息
```
GET /info
```

获取当前模型的配置信息。

## 依赖安装

```bash
pip install -r requirements.txt
```

或者单独安装：
```bash
pip install torch torchvision Pillow numpy Flask opencv-python
```

## 测试服务

使用提供的测试客户端：

```bash
python test_client.py
```

或者使用Python交互式测试：

```python
from client_example import YOLOClient

client = YOLOClient("http://localhost:5000")
health = client.health_check()
print(health)
```

## 停止服务

按 `Ctrl+C` 停止服务。

## 故障排除

### 端口被占用
更改PORT环境变量使用其他端口。

### 内存不足
- 减少批处理大小（如果支持）
- 确保有足够的系统内存
- 考虑使用CPU模式（USE_CUDA=false）

### 模型文件不存在
确保model_data目录中包含必要的模型文件：
- MOF.pth 或其他模型文件
- voc_classes.txt
- yolo_anchors.txt
- simhei.ttf（用于绘制中文标签）