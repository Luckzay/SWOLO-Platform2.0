from flask import Flask, request, jsonify, send_file
from flask_cors import CORS
from PIL import Image
from io import BytesIO
import base64
import os
from yolo_service import YOLOService

# 创建Flask应用，并设置上传文件大小限制
app = Flask(__name__)
CORS(app)  # 启用CORS支持
# 设置最大内容长度为100MB
app.config['MAX_CONTENT_LENGTH'] = 100 * 1024 * 1024

# 全局服务实例
yolo_service = None


@app.before_request
def limit_request_size():
    """限制请求大小"""
    # 限制请求体大小为100MB
    if request.content_length and request.content_length > 100 * 1024 * 1024:
        from flask import abort
        abort(413, "Request body too large")


@app.route('/health', methods=['GET'])
def health_check():
    """健康检查端点"""
    return jsonify({
        'status': 'healthy',
        'version': '1.0.0',
        'model_loaded': yolo_service is not None
    })


@app.route('/predict/titration', methods=['POST'])
def predict_titration():
    """滴定实验分析端点"""
    global yolo_service
    
    try:
        data = request.get_json()
        
        if not data or 'image_data' not in data:
            return jsonify({
                'success': False,
                'error': {'code': 'INVALID_REQUEST', 'message': 'No image data provided'},
                'message': 'Request must include image_data field'
            }), 400
        
        # 解码base64图像数据
        image_data = data['image_data']
        image_bytes = base64.b64decode(image_data)
        image = Image.open(BytesIO(image_bytes))
        
        # 获取选项
        options = data.get('options', {})
        
        # 执行滴定分析 - 这里使用YOLO服务进行颜色和终点检测
        # 在实际实现中，这里会调用专门的滴定分析模型
        detections = yolo_service.get_simple_detections(image)
        
        # 模拟滴定分析结果
        # 在实际实现中，这应该包含专门的滴定终点检测逻辑
        result = {
            'color': 'colorless',  # 模拟颜色检测
            'endPointReached': False,  # 模拟终点检测
            'volume': 24.5,  # 模拟体积
            'concentration': 0.1023,  # 模拟浓度
            'confidence': 0.95  # 模拟置信度
        }
        
        # 如果有检测到相关对象，更新结果
        if detections:
            # 在实际实现中，这里会分析检测结果以确定滴定终点
            for det in detections:
                class_name = det[0]
                confidence = det[1]
                if 'end' in class_name.lower() or 'point' in class_name.lower():
                    result['endPointReached'] = True
                    result['confidence'] = max(result['confidence'], confidence)
        
        return jsonify({
            'success': True,
            'result': result,
            'message': 'Titration analysis completed successfully'
        })
    
    except Exception as e:
        return jsonify({
            'success': False,
            'error': {'code': 'MODEL_ERROR', 'message': str(e)},
            'message': 'Titration analysis failed'
        }), 500


@app.route('/predict/concentration', methods=['POST'])
def predict_concentration():
    """浓度检测端点"""
    global yolo_service
    
    try:
        data = request.get_json()
        
        if not data or 'image_data' not in data:
            return jsonify({
                'success': False,
                'error': {'code': 'INVALID_REQUEST', 'message': 'No image data provided'},
                'message': 'Request must include image_data field'
            }), 400
        
        # 解码base64图像数据
        image_data = data['image_data']
        image_bytes = base64.b64decode(image_data)
        image = Image.open(BytesIO(image_bytes))
        
        # 获取选项
        options = data.get('options', {})
        
        # 执行浓度分析 - 同时获取处理后的图像和检测结果
        result_image, detections = yolo_service.predict(image)
        
        # 准备返回结果
        detection_results = []
        for det in detections:
            class_name, confidence, x, y, w, h = det
            detection_info = {
                'class': class_name,
                'confidence': float(confidence),
                'x': int(x),
                'y': int(y),
                'width': int(w),
                'height': int(h)
            }
            detection_results.append(detection_info)
        
        # 检查是否需要返回处理后的图像
        return_image = options.get('return_image', False)
        if return_image:
            # 将处理后的图像转换为base64
            img_io = BytesIO()
            result_image.save(img_io, 'PNG')
            img_io.seek(0)
            img_base64 = base64.b64encode(img_io.getvalue()).decode('utf-8')
            
            return jsonify({
                'success': True,
                'result': {
                    'detections': detection_results,
                    'detection_count': len(detection_results),
                },
                'result_image': img_base64,  # 添加处理后的图像
                'message': 'Concentration detection completed successfully'
            })
        else:
            return jsonify({
                'success': True,
                'result': {
                    'detections': detection_results,
                    'detection_count': len(detection_results)
                },
                'message': 'Concentration detection completed successfully'
            })
    
    except Exception as e:
        return jsonify({
            'success': False,
            'error': {'code': 'MODEL_ERROR', 'message': str(e)},
            'message': 'Concentration detection failed'
        }), 500


@app.route('/predict/characterization', methods=['POST'])
def predict_characterization():
    """粒子表征分析端点"""
    global yolo_service
    
    try:
        data = request.get_json()
        
        if not data or 'image_data' not in data:
            return jsonify({
                'success': False,
                'error': {'code': 'INVALID_REQUEST', 'message': 'No image data provided'},
                'message': 'Request must include image_data field'
            }), 400
        
        # 解码base64图像数据
        image_data = data['image_data']
        image_bytes = base64.b64decode(image_data)
        image = Image.open(BytesIO(image_bytes))
        
        # 获取选项
        options = data.get('options', {})
        
        # 执行粒子检测 - 同时获取处理后的图像和检测结果
        result_image, detections = yolo_service.predict(image)
        
        # 直接返回检测数据（类、预测概率、x,y,w,h）
        detection_results = []
        
        for det in detections:
            class_name, confidence, x, y, w, h = det
            detection_info = {
                'class': class_name,
                'confidence': float(confidence),
                'x': int(x),
                'y': int(y),
                'width': int(w),
                'height': int(h)
            }
            detection_results.append(detection_info)
        
        # 检查是否需要返回处理后的图像
        return_image = options.get('return_image', False)
        if return_image:
            # 将处理后的图像转换为base64
            img_io = BytesIO()
            result_image.save(img_io, 'PNG')
            img_io.seek(0)
            img_base64 = base64.b64encode(img_io.getvalue()).decode('utf-8')
            
            return jsonify({
                'success': True,
                'result': {
                    'detections': detection_results,
                    'detection_count': len(detection_results)
                },
                'result_image': img_base64,  # 添加处理后的图像
                'message': 'Particle characterization completed successfully'
            })
        else:
            return jsonify({
                'success': True,
                'result': {
                    'detections': detection_results,
                    'detection_count': len(detection_results)
                },
                'message': 'Particle characterization completed successfully'
            })
    
    except Exception as e:
        return jsonify({
            'success': False,
            'error': {'code': 'MODEL_ERROR', 'message': str(e)},
            'message': 'Particle characterization failed'
        }), 500


@app.route('/predict', methods=['POST'])
def predict_legacy():
    """遗留的通用预测端点"""
    global yolo_service
    
    try:
        # 检查请求中是否包含图像
        if 'image' not in request.files and 'image_base64' not in request.form:
            return jsonify({'error': 'No image provided'}), 400
        
        # 获取图像
        if 'image' in request.files:
            file = request.files['image']
            if file.filename == '':
                return jsonify({'error': 'No image provided'}), 400
            
            # 读取图像
            image_bytes = file.read()
            image = Image.open(BytesIO(image_bytes))
        elif 'image_base64' in request.form:
            # 从base64字符串读取图像
            base64_string = request.form['image_base64']
            image = yolo_service.predict_from_base64(base64_string)[0]
        else:
            return jsonify({'error': 'Invalid image format'}), 400
        
        # 执行预测
        result_image, detections = yolo_service.predict(image)
        
        # 准备返回结果
        result = {
            'detections': [
                {
                    'class': det[0],
                    'confidence': float(det[1]),
                    'x': int(det[2]),
                    'y': int(det[3]),
                    'width': int(det[4]),
                    'height': int(det[5])
                } for det in detections
            ],
            'detection_count': len(detections)
        }
        
        # 如果请求需要返回处理后的图像
        if request.form.get('return_image', 'false').lower() == 'true':
            # 将处理后的图像转换为base64
            img_io = BytesIO()
            result_image.save(img_io, 'PNG')
            img_io.seek(0)
            img_base64 = base64.b64encode(img_io.getvalue()).decode('utf-8')
            result['result_image'] = img_base64
        
        return jsonify(result)
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/predict_simple', methods=['POST'])
def predict_simple_legacy():
    """遗留的简单预测端点"""
    global yolo_service
    
    try:
        # 检查请求中是否包含图像
        if 'image' not in request.files and 'image_base64' not in request.form:
            return jsonify({'error': 'No image provided'}), 400
        
        # 获取图像
        if 'image' in request.files:
            file = request.files['image']
            if file.filename == '':
                return jsonify({'error': 'No image provided'}), 400
            
            # 读取图像
            image_bytes = file.read()
            image = Image.open(BytesIO(image_bytes))
        elif 'image_base64' in request.form:
            # 从base64字符串读取图像
            base64_string = request.form['image_base64']
            image = Image.open(BytesIO(base64.b64decode(base64_string)))
        else:
            return jsonify({'error': 'Invalid image format'}), 400
        
        # 执行预测
        detections = yolo_service.get_simple_detections(image)
        
        # 准备返回结果
        result = {
            'detections': [
                {
                    'class': det[0],
                    'confidence': float(det[1]),
                    'x': int(det[2]),
                    'y': int(det[3]),
                    'width': int(det[4]),
                    'height': int(det[5])
                } for det in detections
            ],
            'detection_count': len(detections)
        }
        
        return jsonify(result)
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/info', methods=['GET'])
def get_model_info():
    """获取模型信息"""
    global yolo_service
    
    if yolo_service is None:
        return jsonify({'error': 'YOLO service not initialized'}), 500
    
    info = yolo_service.get_current_model_info()
    
    return jsonify(info)


@app.route('/models', methods=['GET'])
def get_available_models():
    """获取可用模型列表"""
    global yolo_service
    
    if yolo_service is None:
        return jsonify({'error': 'YOLO service not initialized'}), 500
    
    models = yolo_service.get_available_models()
    
    return jsonify({
        'success': True,
        'models': models
    })


@app.route('/models/switch', methods=['POST'])
def switch_model():
    """切换模型"""
    global yolo_service
    
    if yolo_service is None:
        return jsonify({
            'success': False,
            'error': {'code': 'SERVICE_ERROR', 'message': 'YOLO service not initialized'},
            'message': 'YOLO service not initialized'
        }), 500
    
    try:
        data = request.get_json()
        
        if not data or 'model_name' not in data:
            return jsonify({
                'success': False,
                'error': {'code': 'INVALID_REQUEST', 'message': 'Model name is required'},
                'message': 'Request must include model_name field'
            }), 400
        
        model_name = data['model_name']
        
        # 获取可用模型列表
        available_models = yolo_service.get_available_models()
        target_model = None
        
        for model in available_models:
            if model['name'] == model_name:
                target_model = model
                break
        
        if not target_model:
            return jsonify({
                'success': False,
                'error': {'code': 'MODEL_NOT_FOUND', 'message': f'Model {model_name} not found'},
                'message': f'Model {model_name} not found'
            }), 404

        # 切换模型，需要传递模型类型
        success = yolo_service.switch_model(
            target_model['model_path'],
            target_model['classes_path'],
            target_model.get('anchors_path'),  # 锚框路径是可选的
            target_model['model_type']  # 传递模型类型 (v5 或 v8)
        )

        if success:
            return jsonify({
                'success': True,
                'message': f'Model switched to {model_name} successfully',
                'current_model': yolo_service.get_current_model_info()
            })
        else:
            return jsonify({
                'success': False,
                'error': {'code': 'MODEL_SWITCH_ERROR', 'message': 'Failed to switch model'},
                'message': 'Failed to switch model'
            }), 500
    
    except Exception as e:
        return jsonify({
            'success': False,
            'error': {'code': 'MODEL_SWITCH_ERROR', 'message': str(e)},
            'message': 'Model switch failed'
        }), 500


def initialize_service():
    """初始化YOLO服务"""
    global yolo_service
    
    # 可以从环境变量或配置文件中读取模型路径
    model_path = os.environ.get('MODEL_PATH', './model_data/MOF.pth')
    classes_path = os.environ.get('CLASSES_PATH', './model_data/voc_classes.txt')
    anchors_path = os.environ.get('ANCHORS_PATH', './model_data/yolo_anchors.txt')
    
    print("正在初始化YOLO服务...")
    print(f"模型路径: {model_path}")
    print(f"类别路径: {classes_path}")
    print(f"锚框路径: {anchors_path}")
    
    yolo_service = YOLOService(
        model_path=model_path,
        classes_path=classes_path,
        anchors_path=anchors_path
    )
    
    # 根据环境变量决定是否使用CUDA
    use_cuda = os.environ.get('USE_CUDA', 'false').lower() == 'true'
    yolo_service.set_cuda(use_cuda)
    
    print("YOLO服务初始化完成")


if __name__ == '__main__':
    # 初始化服务
    initialize_service()
    
    # 启动服务器
    host = os.environ.get('HOST', '0.0.0.0')
    port = int(os.environ.get('PORT', 5000))
    
    print(f"启动服务器: {host}:{port}")
    app.run(host=host, port=port, debug=False)