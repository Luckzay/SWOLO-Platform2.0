"""
验证GLU模型是否正确加载的脚本
"""
import requests

def main():
    print("验证GLU模型加载情况...")
    print("="*50)
    
    # 1. 获取当前模型信息
    print("1. 获取当前模型信息:")
    try:
        response = requests.get("http://localhost:5000/info")
        info = response.json()
        print(f"   模型路径: {info['model_path']}")
        print(f"   模型类型: {info['model_type']}")
        print(f"   类别数量: {info['num_classes']}")
        print(f"   类别名称: {info['class_names']}")
        print(f"   输入形状: {info['input_shape']}")
    except Exception as e:
        print(f"   错误: {e}")
    
    print()
    
    # 2. 获取可用模型列表
    print("2. 获取可用模型列表:")
    try:
        response = requests.get("http://localhost:5000/models")
        models = response.json()
        for model in models['models']:
            status = "当前" if model['is_current'] else "可用"
            print(f"   {model['name']}: {model['model_type']} - {status}")
            print(f"     路径: {model['model_path']}")
    except Exception as e:
        print(f"   错误: {e}")
    
    print()
    
    # 3. 切换到GLU模型
    print("3. 切换到GLU模型:")
    try:
        response = requests.post("http://localhost:5000/models/switch", json={"model_name": "GLU"})
        result = response.json()
        print(f"   切换结果: {result['success']}")
        if result['success']:
            current_model = result['current_model']
            print(f"   当前模型路径: {current_model['model_path']}")
            print(f"   当前模型类型: {current_model['model_type']}")
            print(f"   当前类别数量: {current_model['num_classes']}")
            print(f"   当前类别名称: {current_model['class_names']}")
    except Exception as e:
        print(f"   错误: {e}")
    
    print()
    
    # 4. 再次获取当前模型信息
    print("4. 切换后获取当前模型信息:")
    try:
        response = requests.get("http://localhost:5000/info")
        info = response.json()
        print(f"   模型路径: {info['model_path']}")
        print(f"   模型类型: {info['model_type']}")
        print(f"   类别数量: {info['num_classes']}")
        print(f"   类别名称: {info['class_names']}")
    except Exception as e:
        print(f"   错误: {e}")
    
    print()
    print("验证完成！")

if __name__ == "__main__":
    main()