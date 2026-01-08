# YOLO 预测服务 API 文档

本文档详细描述了 YOLO 预测服务的所有 API 端点、请求格式和响应格式。

## 服务信息

- **服务地址**: http://[HOST]:[PORT] (默认: http://0.0.0.0:5000)
- **内容类型**: application/json (除特殊说明外)
- **最大请求大小**: 100MB

## 通用响应格式

所有 API 响应都遵循以下格式：

```json
{
  "success": true/false,
  "result": {...},  // 成功时包含结果数据
  "error": {        // 失败时包含错误信息
    "code": "错误代码",
    "message": "错误消息"
  },
  "message": "描述性消息"
}
```

## API 端点

### 1. 健康检查

#### GET /health

检查服务的健康状态。

**请求参数**: 无

**成功响应示例**:
```json
{
  "status": "healthy",
  "version": "1.0.0",
  "model_loaded": true
}
```

### 2. 滴定实验分析

#### POST /predict/titration

分析滴定实验图像，检测颜色变化、终点、体积和浓度等信息。

**请求格式**:
```json
{
  "image_data": "base64编码的图像数据",
  "options": {  // 可选字段
    // 分析选项，具体取决于实现
  }
}
```

**请求参数**:
- `image_data` (string, 必需): base64编码的图像数据
- `options` (object, 可选): 分析选项

**成功响应示例**:
```json
{
  "success": true,
  "result": {
    "color": "colorless",      // 检测到的颜色
    "endPointReached": false,  // 是否到达滴定终点
    "volume": 24.5,            // 检测到的体积值
    "concentration": 0.1023,   // 检测到的浓度值
    "confidence": 0.95         // 检测置信度
  },
  "message": "Titration analysis completed successfully"
}
```

**错误响应示例**:
```json
{
  "success": false,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "No image data provided"
  },
  "message": "Request must include image_data field"
}
```

### 3. 浓度检测

#### POST /predict/concentration

检测图像中的浓度值。

**请求格式**:
```json
{
  "image_data": "base64编码的图像数据",
  "options": {  // 可选字段
    // 分析选项，具体取决于实现
  }
}
```

**请求参数**:
- `image_data` (string, 必需): base64编码的图像数据
- `options` (object, 可选): 分析选项

**成功响应示例**:
```json
{
  "success": true,
  "result": {
    "concentration": 0.1023,  // 检测到的浓度值
    "color": "light_blue",    // 检测到的颜色
    "confidence": 0.92        // 检测置信度
  },
  "message": "Concentration detection completed successfully"
}
```

### 4. 粒子表征分析

#### POST /predict/characterization

执行粒子检测并返回详细的检测结果。

**请求格式**:
```json
{
  "image_data": "base64编码的图像数据",
  "options": {  // 可选字段
    // 分析选项，具体取决于实现
  }
}
```

**请求参数**:
- `image_data` (string, 必需): base64编码的图像数据
- `options` (object, 可选): 分析选项

**成功响应示例**:
```json
{
  "success": true,
  "result": {
    "detections": [
      {
        "class": "particle_type",  // 检测到的粒子类别
        "confidence": 0.95,        // 检测置信度
        "x": 100,                  // 边界框左上角x坐标
        "y": 150,                  // 边界框左上角y坐标
        "width": 50,               // 边界框宽度
        "height": 60               // 边界框高度
      }
    ],
    "detection_count": 1           // 检测到的对象数量
  },
  "message": "Particle characterization completed successfully"
}
```

### 5. 通用预测 (遗留接口)

#### POST /predict

执行通用对象检测（遗留接口）。

**请求方式 1: 文件上传**
- **内容类型**: multipart/form-data
- **参数**: 
  - `image`: 图像文件
  - `return_image`: (可选) 是否返回处理后的图像 (true/false)

**请求方式 2: 表单数据**
- **内容类型**: application/x-www-form-urlencoded
- **参数**:
  - `image_base64`: base64编码的图像数据
  - `return_image`: (可选) 是否返回处理后的图像 (true/false)

**成功响应示例**:
```
{
  "detections": [
    {
      "class": "object_class",   // 检测到的对象类别
      "confidence": 0.85,        // 检测置信度
      "x": 100,                  // 边界框左上角x坐标
      "y": 150,                  // 边界框左上角y坐标
      "width": 50,               // 边界框宽度
      "height": 60               // 边界框高度
    }
  ],
  "detection_count": 1,         // 检测到的对象数量
  "result_image": "base64..."   // (可选) 处理后的图像，当return_image=true时返回
}
```

### 6. 简单预测 (遗留接口)

#### POST /predict_simple

执行简单对象检测（遗留接口）。

**请求方式 1: 文件上传**
- **内容类型**: multipart/form-data
- **参数**: 
  - `image`: 图像文件

**请求方式 2: 表单数据**
- **内容类型**: application/x-www-form-urlencoded
- **参数**:
  - `image_base64`: base64编码的图像数据

**成功响应示例**:
```
{
  "detections": [
    {
      "class": "object_class",   // 检测到的对象类别
      "confidence": 0.85,        // 检测置信度
      "x": 100,                  // 边界框左上角x坐标
      "y": 150,                  // 边界框左上角y坐标
      "width": 50,               // 边界框宽度
      "height": 60               // 边界框高度
    }
  ],
  "detection_count": 1          // 检测到的对象数量
}
```

### 7. 模型信息

#### GET /info

获取当前加载模型的信息。

**请求参数**: 无

**成功响应示例**:
```
{
  "model_path": "./model_data/MOF.pth",     // 模型文件路径
  "classes_path": "./model_data/voc_classes.txt", // 类别文件路径
  "anchors_path": "./model_data/yolo_anchors.txt", // 锚框文件路径
  "num_classes": 80,                    // 类别数量
  "class_names": ["class1", "class2"],  // 类别名称列表
  "input_shape": [416, 416],            // 模型输入形状
  "confidence": 0.5,                    // 置信度阈值
  "nms_iou": 0.3                        // NMS IOU阈值
}
```

### 8. 可用模型列表

#### GET /models

获取所有可用模型的列表。

**请求参数**: 无

**成功响应示例**:
```
{
  "success": true,
  "models": [
    {
      "name": "MOF",                    // 模型名称
      "model_path": "./model_data/MOF.pth",     // 模型文件路径
      "classes_path": "./model_data/voc_classes.txt", // 对应类别文件路径
      "model_type": "v5",               // 模型类型 (v5 或 v8)
      "is_current": true                // 是否为当前使用的模型
    },
    {
      "name": "5101520",                // 模型名称
      "model_path": "./model_data/5101520.pth", // 模型文件路径
      "classes_path": "./model_data/concentration_classes.txt", // 对应类别文件路径
      "model_type": "v5",               // 模型类型 (v5 或 v8)
      "is_current": false               // 是否为当前使用的模型
    },
    {
      "name": "GLU",                    // 模型名称
      "model_path": "./model_data/GLU.pth",     // 模型文件路径
      "classes_path": "./model_data/glu_classes.txt", // 对应类别文件路径
      "model_type": "v8",               // 模型类型 (v5 或 v8)
      "is_current": false               // 是否为当前使用的模型
    }
  ]
}
```

### 9. 切换模型

#### POST /models/switch

切换到指定的模型。

**请求格式**:
```json
{
  "model_name": "MOF"  // 要切换到的模型名称
}
```

**请求参数**:
- `model_name` (string, 必需): 要切换到的模型名称

**成功响应示例**:
```
{
  "success": true,
  "message": "Model switched to MOF successfully",
  "current_model": {
    "model_path": "./model_data/MOF.pth",     // 模型文件路径
    "classes_path": "./model_data/voc_classes.txt", // 类别文件路径
    "anchors_path": "./model_data/yolo_anchors.txt", // 锚框文件路径
    "num_classes": 1,                     // 类别数量
    "class_names": ["MOF"],               // 类别名称列表
    "input_shape": [416, 416],            // 模型输入形状
    "confidence": 0.5,                    // 置信度阈值
    "nms_iou": 0.3                        // NMS IOU阈值
  }
}
```

**错误响应示例**:
```
{
  "success": false,
  "error": {
    "code": "MODEL_NOT_FOUND",
    "message": "Model nonexistent_model not found"
  },
  "message": "Model nonexistent_model not found"
}
```

## 错误代码

| 错误代码 | 说明 |
|---------|------|
| INVALID_REQUEST | 请求格式无效或缺少必需参数 |
| MODEL_ERROR | 模型处理时发生错误 |
| REQUEST_BODY_TOO_LARGE | 请求体过大 |
| SERVICE_ERROR | 服务未初始化 |
| MODEL_NOT_FOUND | 指定的模型未找到 |
| MODEL_SWITCH_ERROR | 模型切换失败 |

## 图像格式要求

- 支持的格式: JPEG, PNG, BMP, GIF, TIFF
- 最大文件大小: 100MB
- 图像数据应使用 base64 编码

## 使用示例

### Python 示例

```
import requests
import base64

# 读取图像并进行 base64 编码
with open("image.jpg", "rb") as image_file:
    encoded_image = base64.b64encode(image_file.read()).decode('utf-8')

# 发送请求
url = "http://localhost:5000/predict/characterization"
payload = {
    "image_data": encoded_image,
    "options": {}
}
response = requests.post(url, json=payload)

# 解析响应
result = response.json()
if result['success']:
    print(f"检测到 {result['result']['detection_count']} 个对象")
    for detection in result['result']['detections']:
        print(f"类别: {detection['class']}, 置信度: {detection['confidence']}")
else:
    print(f"错误: {result['error']['message']}")
```

### cURL 示例

```
curl -X POST http://localhost:5000/predict/characterization \
  -H "Content-Type: application/json" \
  -d '{
    "image_data": "'$(base64 -i image.jpg | tr -d '\n')'",
    "options": {}
  }'
```