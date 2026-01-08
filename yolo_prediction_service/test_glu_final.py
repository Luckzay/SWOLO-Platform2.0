import os
import sys
import base64
from PIL import Image
import torch
from yolo_service import YOLOService

def test_glu_model():
    """
    测试GLU模型是否可以正常使用
    """
    print("开始测试GLU模型...")
    
    # 定义模型路径
    model_path = './model_data/GLU.pth'
    classes_path = './model_data/glu_classes.txt'
    
    # 检查文件是否存在
    if not os.path.exists(model_path):
        print(f"错误: 模型文件不存在 {model_path}")
        return False
    
    if not os.path.exists(classes_path):
        print(f"错误: 类别文件不存在 {classes_path}")
        return False
    
    try:
        # 创建YOLO服务实例
        print("创建YOLOv8服务实例...")
        yolo_service = YOLOService(
            model_path=model_path,
            classes_path=classes_path,
            model_type='v8'
        )
        
        # 设置CUDA状态
        yolo_service.set_cuda(torch.cuda.is_available())
        
        print("GLU模型加载成功!")
        print(f"模型信息: {yolo_service.get_current_model_info()}")
        
        # 测试使用用户提供的图像
        test_image_path = r"D:\Users\DELL\Desktop\mydata\Glu528\1748409265.508517.jpg"
        
        if os.path.exists(test_image_path):
            print(f"测试图像存在，开始检测: {test_image_path}")
            
            # 打开测试图像
            test_image = Image.open(test_image_path)
            print(f"图像尺寸: {test_image.size}")
            
            # 执行检测
            result_image, detections = yolo_service.predict(test_image)
            
            print(f"检测完成，发现 {len(detections)} 个对象:")
            for i, det in enumerate(detections):
                class_name, confidence, x, y, w, h = det
                print(f"  {i+1}. 类别: {class_name}, 置信度: {confidence:.2f}, 位置: ({x}, {y}), 尺寸: ({w}, {h})")
            
            # 保存结果图像
            output_path = "./test_result.jpg"
            result_image.save(output_path)
            print(f"结果图像已保存至: {output_path}")
            
            return True
        else:
            print(f"警告: 测试图像不存在 {test_image_path}")
            print("跳过图像检测测试")
            return True
            
    except Exception as e:
        print(f"测试GLU模型时出错: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

def test_model_switching():
    """
    测试模型切换功能
    """
    print("\n开始测试模型切换功能...")
    
    try:
        # 初始化服务，默认使用MOF模型
        yolo_service = YOLOService()
        
        print("初始模型信息:")
        print(yolo_service.get_current_model_info())
        
        # 获取可用模型列表
        available_models = yolo_service.get_available_models()
        print(f"\n可用模型数量: {len(available_models)}")
        for model in available_models:
            print(f"  - {model['name']}: {model['model_path']} (type: {model['model_type']})")
        
        # 尝试切换到GLU模型
        for model in available_models:
            if model['name'] == 'GLU':
                print(f"\n切换到GLU模型...")
                success = yolo_service.switch_model(
                    model['model_path'],
                    model['classes_path'],
                    model.get('anchors_path'),
                    model['model_type']
                )
                
                if success:
                    print("成功切换到GLU模型!")
                    print("当前模型信息:")
                    print(yolo_service.get_current_model_info())
                else:
                    print("切换模型失败!")
                
                break
        
        return True
        
    except Exception as e:
        print(f"测试模型切换时出错: {str(e)}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    print("开始测试GLU模型和功能...")
    
    # 测试GLU模型
    glu_test_result = test_glu_model()
    
    # 测试模型切换
    switch_test_result = test_model_switching()
    
    print(f"\n测试结果:")
    print(f"GLU模型测试: {'通过' if glu_test_result else '失败'}")
    print(f"模型切换测试: {'通过' if switch_test_result else '失败'}")
    
    if glu_test_result and switch_test_result:
        print("\n所有测试通过!")
    else:
        print("\n部分测试失败，请检查错误信息。")