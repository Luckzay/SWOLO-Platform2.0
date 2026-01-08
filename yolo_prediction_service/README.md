# YOLO 预测服务

YOLO预测服务是一个独立的HTTP API服务，用于提供基于YOLOv5/v8的目标检测功能。该服务专门针对化学实验场景进行了优化，能够检测和分析化学实验中的各种元素。

## 概述

YOLO预测服务实现了以下功能：
- 支持YOLOv5和YOLOv8模型
- 化学实验相关物体检测
- 滴定终点检测
- 浓度分析
- 实时图像处理

## 项目结构

```
yolo_prediction_service/
├── nets/                       # YOLOv5网络结构
│   ├── darknet.py             # DarkNet骨干网络
│   └── yolo.py                # YOLOv5实现
├── netsv8/                     # YOLOv8网络结构
│   ├── backbone.py            # YOLOv8骨干网络
│   ├── yolov8.py              # YOLOv8实现
│   └── yolo_training.py       # YOLOv8训练相关
├── utils/                      # YOLOv5工具函数
│   ├── utils.py               # 通用工具函数
│   └── utils_bbox.py          # 边界框处理工具
├── utilsv8/                    # YOLOv8工具函数
│   ├── callbacks.py           # 训练回调函数
│   ├── dataloader.py          # 数据加载器
│   ├── utils.py               # 通用工具函数
│   ├── utils_bbox.py          # 边界框处理工具
│   ├── utils_fit.py           # 训练适配函数
│   └── utils_map.py           # mAP计算工具
├── model_data/                 # 模型数据文件
│   ├── MOF.pth                # MOF模型权重
│   ├── GLU.pth                # GLU模型权重
│   ├── 5101520.pth            # 浓度检测模型权重
│   ├── voc_classes.txt        # VOC类别文件
│   ├── glu_classes.txt        # GLU类别文件
│   ├── concentration_classes.txt # 浓度检测类别文件
│   └── yolo_anchors.txt       # YOLO锚框文件
├── server.py                  # Flask API服务
├── yolo_service.py            # YOLO服务封装类
├── yolo.py                    # YOLOv5封装类
├── yolov8.py                  # YOLOv8封装类
├── model.py                   # 模型管理
├── predictor_burette.py       # 滴定管检测器
├── requirements.txt           # Python依赖
├── start_server.bat           # Windows启动脚本
├── start_server.sh            # Linux/Mac启动脚本
├── simple_test.py             # 简单测试脚本
├── verify_glu.py              # GLU模型验证脚本
├── test_glu_final.py          # GLU模型测试脚本
├── API_DOCUMENTATION.md       # API文档
└── START_GUIDE.md             # 启动指南
```

## 模型说明

### MOF模型
- **类型**: YOLOv5
- **用途**: 一般化学物质检测
- **权重文件**: model_data/MOF.pth
- **类别文件**: model_data/voc_classes.txt

### GLU模型
- **类型**: YOLOv8
- **用途**: 特定化学物质检测
- **权重文件**: model_data/GLU.pth
- **类别文件**: model_data/glu_classes.txt

### 浓度检测模型
- **类型**: YOLOv5
- **用途**: TEM检测
- **权重文件**: model_data/TEM.pth
- **类别文件**: model_data/voc_classes.txt

## 快速启动

### 环境准备
```bash
# 安装依赖
pip install -r requirements.txt
```

### 启动服务

#### Windows
```cmd
start_server.bat
```

#### Linux/macOS
```bash
chmod +x start_server.sh
./start_server.sh
```

#### 手动启动
```bash
python server.py
```

## 配置选项

### 环境变量
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

### 模型管理
```
GET /models     # 获取可用模型列表
POST /models/switch  # 切换模型
```

## 服务类说明

### YOLOService
[yolo_service.py](./yolo_service.py) 文件中定义了 [YOLOService](file:///F:/SWOLO-Platform2.0/yolo_prediction_service/yolo_service.py#L27-L114) 类，提供以下功能：
- 模型加载和管理
- CUDA状态切换
- 模型切换功能
- 图像预测接口
- 模型信息查询

### YOLO类
[yolo.py](./yolo.py) 文件中定义了 YOLOv5 的封装类，包含：
- 模型初始化
- 图像检测功能
- 参数配置

### YOLOv8类
[yolov8.py](./yolov8.py) 文件中定义了 YOLOv8 的封装类，包含：
- YOLOv8模型初始化
- 模型融合优化
- 图像检测功能

## 依赖项

### 主要依赖
- **PyTorch** - 深度学习框架
- **Flask** - Web框架
- **Pillow** - 图像处理
- **NumPy** - 数值计算
- **OpenCV** - 计算机视觉库

### 安装依赖
```bash
pip install -r requirements.txt
```

## 测试和验证

### 模型验证
```bash
python verify_glu.py
```

### 简单测试
```bash
python simple_test.py
```

### 完整测试
```bash
python test_glu_final.py
```

## 性能优化

### CUDA加速
服务支持CUDA加速，可通过设置 `USE_CUDA=true` 启用。

### 模型融合
YOLOv8支持模型融合优化，可提升推理速度。

## 安全考虑
- 使用HTTPS进行生产部署
- 实现API访问频率限制
- 验证输入图像格式和大小

## 故障排除

### 常见问题
1. **CUDA不可用**: 检查GPU驱动和CUDA版本兼容性
2. **模型加载失败**: 确保模型文件路径正确
3. **内存不足**: 减少批量大小或使用CPU模式

### 日志记录
服务会记录重要操作和错误信息，便于调试。

## 贡献

请遵循项目代码规范：
- 保持代码风格一致性
- 添加适当的文档字符串
- 编写单元测试
- 遵循PEP 8代码规范

