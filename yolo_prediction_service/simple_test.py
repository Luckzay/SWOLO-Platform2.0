"""
简单的测试脚本，验证GLU模型功能
"""
import requests
import base64
import os
from PIL import Image

def test_api_endpoints():
    """测试API端点"""
    print("测试API端点...")
    
    # 测试健康检查
    try:
        response = requests.get("http://localhost:5000/health", timeout=5)
        print(f"健康检查: {response.status_code}, {response.json()}")
    except Exception as e:
        print(f"健康检查失败: {e}")
    
    # 测试模型列表
    try:
        response = requests.get("http://localhost:5000/models", timeout=5)
        print(f"模型列表: {response.status_code}, {response.json()}")
    except Exception as e:
        print(f"模型列表失败: {e}")
    
    # 测试当前模型信息
    try:
        response = requests.get("http://localhost:5000/info", timeout=5)
        print(f"当前模型信息: {response.status_code}, {response.json()['model_path']}")
    except Exception as e:
        print(f"当前模型信息失败: {e}")

def test_model_switch():
    """测试模型切换"""
    print("\n测试模型切换...")
    
    # 切换到GLU模型
    try:
        payload = {"model_name": "GLU"}
        response = requests.post("http://localhost:5000/models/switch", json=payload, timeout=30)
        result = response.json()
        print(f"切换到GLU模型: {response.status_code}, {result}")
        return result.get('success', False)
    except Exception as e:
        print(f"切换到GLU模型失败: {e}")
        return False

def test_with_sample_image():
    """使用示例图像测试"""
    print("\n使用示例图像测试...")
    
    # 创建一个简单的测试图像（如果真实图像不可用）
    test_image_path = "test_image.jpg"
    try:
        # 创建一个简单的测试图像
        img = Image.new('RGB', (416, 416), color='red')
        img.save(test_image_path)
        
        # 读取并转换为base64
        with open(test_image_path, "rb") as image_file:
            base64_image = base64.b64encode(image_file.read()).decode('utf-8')
        
        # 发送预测请求
        payload = {
            "image_data": base64_image,
            "options": {}
        }
        
        response = requests.post("http://localhost:5000/predict/characterization", json=payload, timeout=30)
        result = response.json()
        print(f"预测结果: {response.status_code}, {result}")
        
        # 删除临时文件
        if os.path.exists(test_image_path):
            os.remove(test_image_path)
            
        return True
    except Exception as e:
        print(f"预测测试失败: {e}")
        # 删除临时文件
        if os.path.exists(test_image_path):
            os.remove(test_image_path)
        return False

def main():
    print("开始测试GLU模型功能...")
    print("="*50)
    
    # 测试API端点
    test_api_endpoints()
    
    # 测试模型切换
    switch_success = test_model_switch()
    
    if switch_success:
        # 测试预测功能
        test_with_sample_image()
    
    print("\n测试完成！")

if __name__ == "__main__":
    main()