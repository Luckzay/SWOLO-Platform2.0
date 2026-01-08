"""
模型管理示例脚本
展示如何使用新的API端点来管理不同的模型
"""

import requests
import base64
import json

# 服务器地址
BASE_URL = "http://localhost:5000"

def get_available_models():
    """获取可用模型列表"""
    try:
        response = requests.get(f"{BASE_URL}/models")
        if response.status_code == 200:
            data = response.json()
            print("可用模型列表:")
            for model in data['models']:
                status = "当前使用" if model['is_current'] else "可用"
                print(f"- {model['name']}: {status}")
                print(f"  模型路径: {model['model_path']}")
                print(f"  类别路径: {model['classes_path']}")
            return data['models']
        else:
            print(f"获取模型列表失败: {response.status_code}")
            print(response.text)
            return None
    except Exception as e:
        print(f"请求失败: {e}")
        return None

def switch_model(model_name):
    """切换模型"""
    try:
        payload = {
            "model_name": model_name
        }
        response = requests.post(f"{BASE_URL}/models/switch", json=payload)
        if response.status_code == 200:
            data = response.json()
            if data['success']:
                print(f"模型切换成功: {data['message']}")
                print(f"当前模型类别: {data['current_model']['class_names']}")
                print(f"类别数量: {data['current_model']['num_classes']}")
                return True
            else:
                print(f"模型切换失败: {data['message']}")
                return False
        else:
            print(f"切换模型请求失败: {response.status_code}")
            print(response.text)
            return False
    except Exception as e:
        print(f"请求失败: {e}")
        return False

def get_current_model_info():
    """获取当前模型信息"""
    try:
        response = requests.get(f"{BASE_URL}/info")
        if response.status_code == 200:
            info = response.json()
            print("当前模型信息:")
            print(f"- 模型路径: {info['model_path']}")
            print(f"- 类别路径: {info['classes_path']}")
            print(f"- 类别数量: {info['num_classes']}")
            print(f"- 类别名称: {info['class_names']}")
            print(f"- 输入形状: {info['input_shape']}")
            return info
        else:
            print(f"获取模型信息失败: {response.status_code}")
            print(response.text)
            return None
    except Exception as e:
        print(f"请求失败: {e}")
        return None

def test_prediction(image_path, model_name):
    """测试预测功能"""
    try:
        # 读取图像并编码为base64
        with open(image_path, "rb") as image_file:
            encoded_image = base64.b64encode(image_file.read()).decode('utf-8')
        
        # 发送预测请求
        payload = {
            "image_data": encoded_image,
            "options": {}
        }
        
        response = requests.post(f"{BASE_URL}/predict/characterization", json=payload)
        if response.status_code == 200:
            result = response.json()
            if result['success']:
                print(f"{model_name} 模型预测结果:")
                print(f"- 检测到 {result['result']['detection_count']} 个对象")
                for detection in result['result']['detections']:
                    print(f"  - 类别: {detection['class']}, 置信度: {detection['confidence']:.2f}")
            else:
                print(f"预测失败: {result['message']}")
        else:
            print(f"预测请求失败: {response.status_code}")
            print(response.text)
    except Exception as e:
        print(f"预测请求失败: {e}")

def main():
    print("=== YOLO模型管理示例 ===\n")
    
    # 1. 获取可用模型列表
    print("1. 获取可用模型列表:")
    models = get_available_models()
    print()
    
    if models:
        # 2. 获取当前模型信息
        print("2. 当前模型信息:")
        current_info = get_current_model_info()
        print()
        
        # 3. 尝试切换到不同的模型（如果存在）
        for model in models:
            if not model['is_current']:  # 切换到非当前模型
                print(f"3. 切换到 {model['name']} 模型:")
                success = switch_model(model['name'])
                if success:
                    print("切换后模型信息:")
                    get_current_model_info()
                    print()
                    
                    # 4. 再次切换回原始模型
                    original_model = None
                    for m in models:
                        if m['is_current']:  # 找到原来的模型
                            original_model = m
                            break
                    if original_model:
                        print(f"4. 切换回原始模型 {original_model['name']}:")
                        switch_model(original_model['name'])
                break
        else:
            print("3. 所有模型都是当前模型，无需切换")
    
    print("\n=== 示例完成 ===")

if __name__ == "__main__":
    main()