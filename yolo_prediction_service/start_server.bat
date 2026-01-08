@echo off
echo Starting YOLO Prediction Service...
echo.

REM 设置环境变量（可选）
set HOST=0.0.0.0
set PORT=5000
set MODEL_PATH=./model_data/MOF.pth
set CLASSES_PATH=./model_data/voc_classes.txt
set ANCHORS_PATH=./model_data/yolo_anchors.txt

REM 检查Python是否已安装
python --version >nul 2>&1
if errorlevel 1 (
    echo Error: Python is not installed or not in PATH.
    echo Please install Python 3.7+ and add it to PATH.
    pause
    exit /b 1
)

REM 检查依赖是否已安装
echo Checking dependencies...
python -c "import torch" >nul 2>&1
if errorlevel 1 (
    echo Installing PyTorch dependencies...
    pip install torch torchvision
)

python -c "import flask" >nul 2>&1
if errorlevel 1 (
    echo Installing Flask...
    pip install flask
)

echo Dependencies check complete.
echo.

REM 启动服务器
echo Starting YOLO prediction server on %HOST%:%PORT%...
python server.py

pause