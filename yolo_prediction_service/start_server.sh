#!/bin/bash

echo "Starting YOLO Prediction Service..."
echo

# 设置环境变量（可选）
export HOST="0.0.0.0"
export PORT="5000"
export MODEL_PATH="./model_data/MOF.pth"
export CLASSES_PATH="./model_data/voc_classes.txt"
export ANCHORS_PATH="./model_data/yolo_anchors.txt"

# 检查Python是否已安装
if ! command -v python3 &> /dev/null; then
    echo "Error: Python3 is not installed or not in PATH."
    echo "Please install Python 3.7+ and add it to PATH."
    exit 1
fi

# 检查依赖是否已安装
echo "Checking dependencies..."

# 检查并安装PyTorch
python3 -c "import torch" 2> /dev/null
if [ $? -ne 0 ]; then
    echo "Installing PyTorch dependencies..."
    pip3 install torch torchvision
fi

# 检查并安装Flask
python3 -c "import flask" 2> /dev/null
if [ $? -ne 0 ]; then
    echo "Installing Flask..."
    pip3 install flask
fi

echo "Dependencies check complete."
echo

# 启动服务器
echo "Starting YOLO prediction server on $HOST:$PORT..."
python3 server.py