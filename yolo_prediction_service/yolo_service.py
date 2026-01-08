import os
import base64
from io import BytesIO
from PIL import Image
from yolo import YOLO as YOLOv5
from yolov8 import YOLO as YOLOv8
import torch


class YOLOService:
    def __init__(self, model_path=None, classes_path=None, anchors_path=None, model_type='v5'):
        """
        初始化YOLO服务
        :param model_path: 模型文件路径
        :param classes_path: 类别文件路径
        :param anchors_path: 锚框文件路径（YOLOv5需要，YOLOv8不需要）
        :param model_type: 模型类型 ('v5' 或 'v8')
        """
        self.model_type = model_type
        
        if model_type == 'v8':
            # 初始化YOLOv8
            self.yolo = YOLOv8(
                model_path=model_path,
                classes_path=classes_path
            )
        else:
            # 初始化YOLOv5 (原始实现)
            self.yolo = YOLOv5()
            
            # 更新模型路径（如果提供了的话）
            if model_path:
                self.yolo._defaults["model_path"] = model_path
            if classes_path:
                self.yolo._defaults["classes_path"] = classes_path
            if anchors_path:
                self.yolo._defaults["anchors_path"] = anchors_path
                
            # 重新初始化模型
            self.yolo.init()
    
    def set_cuda(self, use_cuda):
        """
        设置是否使用CUDA
        :param use_cuda: 是否使用CUDA
        """
        # 检查CUDA是否可用
        cuda_available = torch.cuda.is_available()
        
        if use_cuda and not cuda_available:
            print("警告: 您选择了使用CUDA，但CUDA不可用。将使用CPU进行推理。")
            if self.model_type == 'v8':
                self.yolo.cuda = False
            else:
                self.yolo.cuda = False
        elif use_cuda and cuda_available:
            print("使用CUDA进行推理")
            if self.model_type == 'v8':
                self.yolo.cuda = True
            else:
                self.yolo.cuda = True
        else:
            print("使用CPU进行推理")
            if self.model_type == 'v8':
                self.yolo.cuda = False
            else:
                self.yolo.cuda = False
            
        # 重新生成模型
        if self.model_type == 'v8':
            self.yolo.generate()
        else:
            self.yolo.generate()
    
    def switch_model(self, model_path, classes_path, anchors_path=None, model_type='v5'):
        """
        切换模型
        :param model_path: 新模型文件路径
        :param classes_path: 新类别文件路径
        :param anchors_path: 新锚框文件路径（可选，默认使用当前锚框）
        :param model_type: 模型类型 ('v5' 或 'v8')
        """
        # 根据模型类型创建新的模型实例
        if model_type == 'v8':
            new_yolo = YOLOv8(
                model_path=model_path,
                classes_path=classes_path
            )
        else:
            new_yolo = YOLOv5()
            new_yolo._defaults["model_path"] = model_path
            new_yolo._defaults["classes_path"] = classes_path
            if anchors_path:
                new_yolo._defaults["anchors_path"] = anchors_path
            new_yolo.init()
        
        # 设置CUDA状态
        if hasattr(self, 'cuda'):
            cuda_available = torch.cuda.is_available()
            if cuda_available and self.yolo.cuda:
                if model_type == 'v8':
                    new_yolo.cuda = True
                    new_yolo.generate()
                else:
                    new_yolo.cuda = True
                    new_yolo.generate()
            else:
                if model_type == 'v8':
                    new_yolo.cuda = False
                    new_yolo.generate()
                else:
                    new_yolo.cuda = False
                    new_yolo.generate()
        
        # 替换当前模型
        old_model_type = self.model_type
        self.yolo = new_yolo
        self.model_type = model_type

        # 设置CUDA状态
        if hasattr(self, 'cuda'):
            if (old_model_type == 'v8' and self.yolo.cuda) or (old_model_type != 'v8' and self.yolo.cuda):
                cuda_available = torch.cuda.is_available()
                if cuda_available:
                    if model_type == 'v8':
                        self.yolo.cuda = True
                        self.yolo.generate()
                    else:
                        self.yolo.cuda = True
                        self.yolo.generate()
                else:
                    if model_type == 'v8':
                        self.yolo.cuda = False
                        self.yolo.generate()
                    else:
                        self.yolo.cuda = False
                        self.yolo.generate()
        
        return True
    
    def get_current_model_info(self):
        """
        获取当前模型信息
        :return: 包含当前模型路径、类别路径等信息的字典
        """
        if self.model_type == 'v8':
            return {
                'model_type': 'YOLOv8',
                'model_path': self.yolo.model_path,
                'classes_path': self.yolo.classes_path,
                'num_classes': self.yolo.num_classes,
                'class_names': self.yolo.class_names,
                'input_shape': self.yolo.input_shape,
                'confidence': self.yolo.confidence,
                'nms_iou': self.yolo.nms_iou,
                'phi': getattr(self.yolo, 'phi', 's')  # YOLOv8 版本
            }
        else:
            return {
                'model_type': 'YOLOv5',
                'model_path': self.yolo.model_path if hasattr(self.yolo, 'model_path') else self.yolo.get_defaults('model_path'),
                'classes_path': self.yolo.classes_path if hasattr(self.yolo, 'classes_path') else self.yolo.get_defaults('classes_path'),
                'anchors_path': self.yolo.anchors_path if hasattr(self.yolo, 'anchors_path') else self.yolo.get_defaults('anchors_path'),
                'num_classes': self.yolo.num_classes,
                'class_names': self.yolo.class_names,
                'input_shape': self.yolo.input_shape,
                'confidence': self.yolo.confidence,
                'nms_iou': self.yolo.nms_iou
            }
    
    def get_available_models(self):
        """
        获取可用模型列表
        :return: 可用模型配置列表
        """
        # 获取当前脚本的目录
        current_dir = os.path.dirname(os.path.abspath(__file__))
        model_dir = os.path.join(current_dir, "model_data")
        
        # 检查目录是否存在
        if not os.path.exists(model_dir):
            print(f"模型目录不存在: {model_dir}")
            return []
        
        models = []
        
        # 查找模型文件
        for file in os.listdir(model_dir):
            if file.endswith('.pth'):
                model_name = file[:-4]  # 去掉 .pth 后缀
                model_path = os.path.join(model_dir, file)
                
                # 确定对应的类别文件和模型类型
                if model_name == 'MOF':
                    classes_path = os.path.join(model_dir, 'voc_classes.txt')
                    model_type = 'v5'  # 使用YOLOv5
                elif model_name == '5101520':
                    classes_path = os.path.join(model_dir, 'concentration_classes.txt')
                    model_type = 'v5'  # 使用YOLOv5
                elif model_name == 'GLU':
                    classes_path = os.path.join(model_dir, 'glu_classes.txt')
                    model_type = 'v8'  # 使用YOLOv8
                else:
                    # 默认使用voc_classes.txt和YOLOv5
                    classes_path = os.path.join(model_dir, 'voc_classes.txt')
                    model_type = 'v5'
                
                models.append({
                    'name': model_name,
                    'model_path': model_path,
                    'classes_path': classes_path,
                    'model_type': model_type,
                    'is_current': self._is_current_model(model_path, model_type)
                })
        
        return models
    
    def _is_current_model(self, model_path, model_type):
        """
        检查指定模型路径是否为当前模型
        :param model_path: 模型路径
        :param model_type: 模型类型
        :return: 是否为当前模型
        """
        if self.model_type == 'v8':
            current_path = getattr(self.yolo, 'model_path', '')
        else:
            current_path = self.yolo.model_path if hasattr(self.yolo, 'model_path') else self.yolo.get_defaults('model_path')
        
        return os.path.abspath(model_path) == os.path.abspath(current_path)
    
    def predict_from_file(self, image_path):
        """
        从文件路径进行预测
        :param image_path: 图像文件路径
        :return: 处理后的图像, 检测结果列表
        """
        image = Image.open(image_path)
        return self.predict(image)
    
    def predict_from_bytes(self, image_bytes):
        """
        从字节数据进行预测
        :param image_bytes: 图像字节数据
        :return: 处理后的图像, 检测结果列表
        """
        image = Image.open(BytesIO(image_bytes))
        return self.predict(image)
    
    def predict_from_base64(self, base64_string):
        """
        从base64字符串进行预测
        :param base64_string: base64编码的图像数据
        :return: 处理后的图像, 检测结果列表
        """
        image_bytes = base64.b64decode(base64_string)
        image = Image.open(BytesIO(image_bytes))
        return self.predict(image)
    
    def predict(self, image):
        """
        执行预测
        :param image: PIL图像对象
        :return: 处理后的图像, 检测结果列表
        """
        # 根据模型类型执行检测
        if self.model_type == 'v8':
            # 对于YOLOv8，使用其检测方法
            result_image, detections = self.yolo.detect_image(image, crop=False, count=False, return_detections=True)
        else:
            # 执行检测并返回检测结果
            result_image, detections = self.yolo.detect_image(image, crop=False, count=False, return_detections=True)
        
        return result_image, detections
    
    def get_simple_detections(self, image):
        """
        获取简化版的检测结果，只返回类别、置信度和边界框信息
        :param image: PIL图像对象
        :return: 检测结果列表，每个元素为[类别, 置信度, x, y, width, height]
        """
        _, detections = self.predict(image)
        return detections


# 示例用法
if __name__ == "__main__":
    # 创建服务实例
    service = YOLOService()
    
    # 设置是否使用CUDA（根据需要）
    service.set_cuda(torch.cuda.is_available())
    
    # 示例：从文件预测
    # result_image, detections = service.predict_from_file("path/to/your/image.jpg")
    
    print("YOLO预测服务已初始化")
    print(f"支持的类别数量: {service.yolo.num_classes}")
    print(f"类别名称: {service.yolo.class_names}")