from xmlrpc.client import Error
import torch
from PIL import Image
import numpy as np
from yolo import YOLO
import cv2
import time
import serial

class predict:
    def __init__(self, model_path=None):
        # 使用相对路径或参数传入的模型路径
        self.model_path = model_path or os.path.join(os.path.dirname(os.path.abspath(__file__)), "model_data", "MOF.pth")
        self.yolo = YOLO()
        self.yolo._defaults["model_path"] = self.model_path
        print("predict_burette采用的模型文件位置", self.model_path)
        print(f"CUDA可用性: {torch.cuda.is_available()}")
        self.port = "COM6"


    def update_model_path(self, model_path):
        self.model_path = model_path
        self.yolo._defaults["model_path"] = self.model_path

    def update_COM(self, serial_number):
        self.port = "COM" + serial_number
        print(self.port)

    def start_move_1(self, port, baudrate):  # 快速加酸程序
        ser = serial.Serial(port, baudrate)
        data = b"q1h16d"  # 每分钟转16圈，一个比慢滴略高的旋转速度
        ser.write(data)
        time.sleep(0.01)
        data = b"q5h1d"  # 转1秒
        ser.write(data)
        time.sleep(0.01)
        data = b"q6h3d"  # 逆时针
        ser.write(data)
        time.sleep(20)  # 等待20秒，为快滴时间，注意，这里等待时间不能少于1秒（阀门开启的时间）
        data = b"q6h2d"  # 顺时针
        ser.write(data)
        ser.close()


    def start_move_2(self, port, baudrate):  # 缓慢加酸程序
        ser = serial.Serial(port, baudrate)
        data = b"q1h30d"  # 每分钟转15圈，每秒90°
        ser.write(data)
        time.sleep(0.01)
        data = b"q5h1d"  # 转1秒
        ser.write(data)
        time.sleep(0.01)
        data = b"q6h3d"  # 逆时针
        ser.write(data)
        # 注意，这里没有将阀门转回去，而是持续几秒钟滴加一次的状态
        ser.close()


    def start_move_3(self, port, baudrate):  # 停止加酸程序
        ser = serial.Serial(port, baudrate)
        data = b"q1h15d"  # 每分钟转6圈，每秒0.1圈
        ser.write(data)
        time.sleep(0.01)
        data = b"q5h1d"  # 转1秒
        ser.write(data)
        time.sleep(0.01)
        data = b"q6h2d"  # 顺时针
        ser.write(data)
        # 将阀门转回去
        ser.close()


    def operation(self):
        # 在这里实现具体的滴定操作
        pass


    def main(self):
        print(self.port)
        baudrate = 9600  # 波特率，根据实际情况修改
        videoSourceIndex = 0  # 摄像机编号，请根据自己的情况调整
        cap = cv2.VideoCapture(videoSourceIndex, cv2.CAP_DSHOW)  # 打开摄像头
        # 自动检测是否使用GPU
        cuda_available = torch.cuda.is_available()
        if cuda_available:
            print("使用CUDA进行推理")
            device = torch.device("cuda:0")
        else:
            print("使用CPU进行推理")
            device = torch.device("cpu")
        
        # 更新yolo的cuda设置
        self.yolo.cuda = cuda_available
        
        try:
            self.start_move_2(self.port, baudrate)  # 开启慢滴状态
        except:
            print("单片机未成功连接")


        while True:
            t1 = time.time()
            ref, frame = cap.read()
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            # 转变成Image
            frame = Image.fromarray(np.uint8(frame))
            # 进行检测
            try:
                frame, coordinates = self.yolo.detect_image(frame, return_detections=True)
            except Error:
                frame = self.yolo.detect_image(frame)
                coordinates = None
            frame = np.array(frame)
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            cv2.imshow("video", frame)

            if coordinates is not None:
                for name, x, y in coordinates:
                    if name == 'purple':
                        self.operation()

            c = cv2.waitKey(1) & 0xff
            if c == 'q':
                cap.release()
                break