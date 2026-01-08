<template>
  <div class="experiment-container">
    <h2>溶液浓度检测</h2>
    
    <!-- 模型管理区域 -->
    <ModelManager moduleType="concentration" />
    
    <div class="controls-section">
      <div class="controls-card">
        <div class="controls-row">
          <div class="camera-controls">
            <label for="conc-camera-select">选择摄像头:</label>
            <select id="conc-camera-select" v-model="selectedCamera">
              <option value="">选择摄像头</option>
              <option 
                v-for="camera in cameras" 
                :key="camera.deviceId" 
                :value="camera.deviceId"
              >
                {{ camera.label || `摄像头 ${camera.deviceId}` }}
              </option>
            </select>
          </div>
          <div class="camera-buttons">
            <button @click="startCamera" :disabled="cameraActive" class="start-btn">启动摄像头</button>
            <button @click="stopCamera" :disabled="!cameraActive" class="stop-btn">停止摄像头</button>
          </div>
          <div class="upload-controls">
            <input 
              type="file" 
              id="image-upload" 
              accept="image/*" 
              @change="handleImageUpload"
            />
            <label for="image-upload" class="upload-btn">选择图片</label>
          </div>
          <div class="analyze-controls">
            <button @click="analyzeConcentration" :disabled="!selectedFile && !cameraActive" class="analyze-btn">
              分析浓度
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="preview-result-section">
      <div class="preview-result-card">
        <div class="preview-result-container">
          <div class="preview-column">
            <h3>图像预览</h3>
            <div class="Preview-container">
              <img 
                v-if="previewImage && !cameraActive" 
                :src="previewImage" 
                id="conc-preview" 
                alt="预览" 
              />
              <video 
                ref="videoRef" 
                id="conc-camera-feed" 
                autoplay 
                playsinline 
                v-show="cameraActive"
              ></video>
              <div v-if="!previewImage && !cameraActive" class="no-preview">
                <p>暂无预览图像</p>
              </div>
            </div>
          </div>
          <div class="result-column">
            <h3>检测结果</h3>
            <div id="concentration-result">
              <div v-if="loading" class="loading">正在分析图像...</div>
              <div v-else-if="analysisResult" class="result-content">
                <h4>浓度检测结果</h4>
                <div v-if="!analysisResult.detections || analysisResult.detections.length === 0">
                  <div class="basic-result">
                    <p><strong>溶液浓度:</strong> {{ analysisResult.concentration || '未知' }} M</p>
                    <p><strong>溶液颜色:</strong> {{ analysisResult.color || '未知' }}</p>
                    <p><strong>置信度:</strong> {{ (analysisResult.confidence * 100).toFixed(2) || '未知' }}%</p>
                  </div>
                </div>
                
                <div v-if="resultImage" class="result-image-container">
                  <h4>检测结果图像</h4>
                  <img :src="`data:image/png;base64,${resultImage}`" alt="检测结果" />
                </div>
              </div>
              <div v-else class="no-result">请上传图像或启动摄像头进行分析</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="data-section">
      <div class="data-card">
        <h3>分析数据</h3>
        <div class="data-content">
          <div v-if="analysisResult" class="detailed-data">
            <h4>详细数据</h4>
            <div class="data-grid">
              <div class="data-item">
                <span class="data-label">检测时间:</span>
                <span class="data-value">{{ new Date().toLocaleString() }}</span>
              </div>
              <div class="data-item" v-if="analysisResult.concentration">
                <span class="data-label">溶液浓度:</span>
                <span class="data-value">{{ analysisResult.concentration }} M</span>
              </div>
              <div class="data-item" v-if="analysisResult.color">
                <span class="data-label">溶液颜色:</span>
                <span class="data-value">{{ analysisResult.color }}</span>
              </div>
              <div class="data-item" v-if="analysisResult.confidence">
                <span class="data-label">置信度:</span>
                <span class="data-value">{{ (analysisResult.confidence * 100).toFixed(2) }}%</span>
              </div>
              <div class="data-item" v-if="analysisResult.detections && analysisResult.detections.length > 0">
                <span class="data-label">检测对象数量:</span>
                <span class="data-value">{{ analysisResult.detections.length }}</span>
              </div>
            </div>
            
            <!-- 检测到的容器及浓度信息 -->
            <div v-if="analysisResult.detections && analysisResult.detections.length > 0" class="detection-details">
              <h5>检测到的容器及浓度信息</h5>
              <div class="containers-grid">
                <div 
                  v-for="(detection, index) in analysisResult.detections" 
                  :key="index"
                  class="container-card"
                >
                  <h6>容器 {{ index + 1 }}</h6>
                  <div class="detection-info">
                    <p><strong>类别:</strong> {{ detection.class || '未知' }}</p>
                    <p><strong>置信度:</strong> {{ (detection.confidence * 100).toFixed(2) }}%</p>
                    <p><strong>位置:</strong> ({{ detection.x }}, {{ detection.y }})</p>
                    <p><strong>尺寸:</strong> {{ detection.width }}×{{ detection.height }}</p>
                    <p><strong>浓度:</strong> {{ detection.concentration || '未知' }} M</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="no-data">
            <p>暂无分析数据</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { getConcentrationPrediction } from '../services/apiService';
import ModelManager from './ModelManager.vue';

// 响应式数据
const selectedCamera = ref<string>('');
const cameraActive = ref<boolean>(false);
const selectedFile = ref<File | null>(null);
const previewImage = ref<string | null>(null);
const analysisResult = ref<any>(null);
const resultImage = ref<string | null>(null);
const loading = ref<boolean>(false);
const cameras = ref<any[]>([]);
const videoRef = ref<HTMLVideoElement | null>(null);

// 获取摄像头列表
const getCameras = async () => {
  try {
    const devices = await navigator.mediaDevices.enumerateDevices();
    const videoDevices = devices.filter(device => device.kind === 'videoinput');
    cameras.value = videoDevices;
  } catch (error) {
    console.error('Error getting cameras:', error);
  }
};

// 处理图像上传
const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0];
    
    // 创建预览
    const file = target.files[0];
    const reader = new FileReader();
    reader.onload = (e) => {
      previewImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

// 启动摄像头
const startCamera = async () => {
  if (!selectedCamera.value) {
    alert('请选择摄像头');
    return;
  }

  try {
    const stream = await navigator.mediaDevices.getUserMedia({ 
      video: { 
        deviceId: selectedCamera.value ? { exact: selectedCamera.value } : undefined,
        width: { ideal: 640 },
        height: { ideal: 480 }
      } 
    });
    
    if (videoRef.value) {
      videoRef.value.srcObject = stream;
      cameraActive.value = true;
      previewImage.value = null; // 清除预览图像
    }
  } catch (error) {
    console.error('Error accessing camera:', error);
    alert('无法访问摄像头: ' + (error as Error).message);
  }
};

// 停止摄像头
const stopCamera = () => {
  if (videoRef.value && videoRef.value.srcObject) {
    const tracks = (videoRef.value.srcObject as MediaStream).getTracks();
    tracks.forEach(track => track.stop());
    videoRef.value.srcObject = null;
    cameraActive.value = false;
  }
};

// 分析浓度
const analyzeConcentration = async () => {
  if (!selectedFile.value && !cameraActive) {
    alert('请上传图像或启动摄像头');
    return;
  }

  loading.value = true;
  resultImage.value = null;

  try {
    let imageData: string;

    if (cameraActive.value && videoRef.value) {
      // 从摄像头捕获图像
      const canvas = document.createElement('canvas');
      canvas.width = videoRef.value.videoWidth;
      canvas.height = videoRef.value.videoHeight;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(videoRef.value, 0, 0, canvas.width, canvas.height);
        imageData = canvas.toDataURL('image/jpeg').split(',')[1];
      } else {
        throw new Error('Could not get canvas context');
      }
    } else if (selectedFile.value) {
      // 从文件读取图像
      const file = selectedFile.value;
      const reader = new FileReader();
      reader.readAsDataURL(file);
      imageData = await new Promise<string>((resolve, reject) => {
        reader.onload = () => {
          const result = reader.result as string;
          if (result) {
            resolve(result.split(',')[1]);
          } else {
            reject(new Error('Failed to read file'));
          }
        };
        reader.onerror = () => reject(new Error('Failed to read file'));
      });
    } else {
      throw new Error('No image source available');
    }

    // 调用AI服务进行浓度检测
    const result = await getConcentrationPrediction(imageData);
    
    if (result.success) {
      analysisResult.value = result.result;
      resultImage.value = result.result_image || null;
    } else {
      alert(`分析失败: ${result.message || '未知错误'}`);
    }
  } catch (error) {
    console.error('Concentration analysis error:', error);
    alert('浓度分析失败: ' + (error as Error).message);
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取摄像头
getCameras();
</script>

<style scoped>
.model-management {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.model-management h3 {
  color: var(--secondary-color);
  margin: 0 0 1rem 0;
}

.model-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.model-info-content {
  background: rgba(255, 255, 255, 0.05);
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.model-info-content h4 {
  color: var(--secondary-color);
  margin-bottom: 0.5rem;
}

.model-info-content p {
  margin: 0.3rem 0;
  color: var(--text-secondary);
}

.model-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.model-controls label {
  font-weight: 600;
  color: var(--text-primary);
}

.model-controls select {
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  min-width: 200px;
}

.model-controls button {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: none;
  background: var(--primary-gradient);
  color: white;
  cursor: pointer;
  transition: var(--transition);
}

.model-controls button:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
}

.upload-section {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  align-items: flex-end;
  position: relative;
  z-index: 2;
}

.camera-section {
  flex: 1;
  min-width: 250px;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.camera-section label {
  font-weight: 600;
  color: var(--text-primary);
}

.camera-section select {
  width: 100%;
  padding: 0.7rem 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: 0.95rem;
  transition: var(--transition);
}

.camera-section select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 102, 204, 0.3);
}

.camera-section button {
  margin-top: 0.5rem;
  padding: 0.7rem 1.2rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: var(--primary-gradient);
  color: white;
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.camera-section button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.camera-section button:hover::before {
  left: 100%;
}

.camera-section button:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
  transform: translateY(-2px);
}

.camera-section button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.upload-controls {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

#image-upload {
  display: none;
}

.upload-btn {
  display: inline-block;
  padding: 0.7rem 1.2rem;
  background: var(--success-color);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  text-align: center;
  border: none;
  font-size: 0.95rem;
  transition: var(--transition);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.upload-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.upload-btn:hover::before {
  left: 100%;
}

.upload-btn:hover {
  background: #00b359;
  box-shadow: 0 0 20px rgba(0, 204, 102, 0.5);
}

.upload-controls button {
  padding: 0.7rem 1.2rem;
  background: var(--primary-gradient);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: var(--transition);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.upload-controls button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.upload-controls button:hover::before {
  left: 100%;
}

.upload-controls button:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
  transform: translateY(-2px);
}

.upload-controls button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.result-section {
  display: flex;
  gap: 2rem;
  margin-top: 2rem;
  flex-wrap: wrap;
}

.Preview-container {
  flex: 1;
  min-width: 300px;
  max-width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  min-height: 400px;
}

#conc-preview, #conc-camera-feed {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  object-fit: contain;
}

.analysis-result {
  flex: 1;
  min-width: 300px;
}

.analysis-result h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

#concentration-result {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 1.5rem;
  min-height: 150px;
  border: 1px solid var(--border-color);
}

.result-content {
  background: rgba(255, 255, 255, 0.05);
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.result-content h4 {
  margin-top: 0;
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.result-image-container {
  margin-top: 1rem;
  text-align: center;
}

.result-image-container h4 {
  color: var(--secondary-color);
  margin-bottom: 0.5rem;
}

.result-image-container img {
  max-width: 100%;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.loading {
  text-align: center;
  padding: 2rem;
  color: var(--text-secondary);
  font-style: italic;
}

.no-result {
  text-align: center;
  padding: 2rem;
  color: var(--text-secondary);
  font-style: italic;
}

.containers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.container-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem;
  transition: var(--transition);
}

.container-card:hover {
  background: rgba(0, 102, 204, 0.1);
  border-color: var(--primary-color);
}

.container-card h6 {
  color: var(--secondary-color);
  margin: 0 0 0.75rem 0;
  font-size: 1rem;
}

.controls-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.camera-controls {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.camera-controls label {
  font-weight: 600;
  color: var(--text-primary);
}

.camera-controls select {
  width: 100%;
  max-width: 300px;
  padding: 0.7rem 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: 0.95rem;
  transition: var(--transition);
}

.camera-controls select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 102, 204, 0.3);
}

.camera-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.start-btn {
  padding: 0.7rem 1.2rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: var(--primary-gradient);
  color: white;
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.start-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.start-btn:hover::before {
  left: 100%;
}

.start-btn:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
  transform: translateY(-2px);
}

.start-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.stop-btn {
  padding: 0.7rem 1.2rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: var(--danger-color);
  color: white;
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.stop-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.stop-btn:hover::before {
  left: 100%;
}

.stop-btn:hover {
  background: #e62e5c;
  box-shadow: 0 0 20px rgba(255, 51, 102, 0.5);
  transform: translateY(-2px);
}

.stop-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.controls-section {
  margin-bottom: 1.5rem;
}

.controls-card {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
}

.controls-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: flex-end;
}

.camera-controls {
  display: flex;
  flex-direction: column;
  min-width: 200px;
}

.camera-controls label {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.camera-controls select {
  padding: 0.7rem 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: 0.95rem;
  transition: var(--transition);
  min-width: 200px;
}

.camera-controls select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 102, 204, 0.3);
}

.camera-buttons {
  display: flex;
  gap: 0.5rem;
}

.analyze-controls {
  display: flex;
  align-self: flex-end;
}

.upload-controls {
  display: flex;
  flex-direction: column;
}

#image-upload {
  display: none;
}

.upload-btn {
  display: inline-block;
  padding: 0.7rem 1.2rem;
  background: var(--success-color);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  text-align: center;
  border: none;
  font-size: 0.95rem;
  transition: var(--transition);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.upload-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.upload-btn:hover::before {
  left: 100%;
}

.upload-btn:hover {
  background: #00b359;
  box-shadow: 0 0 20px rgba(0, 204, 102, 0.5);
}

.preview-section {
  margin-bottom: 1.5rem;
}

.preview-container {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.preview-column {
  flex: 1;
  min-width: 300px;
}

.result-column {
  flex: 1;
  min-width: 300px;
}

.preview-column h3,
.result-column h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.Preview-container {
  flex: 1;
  min-width: 300px;
  max-width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  min-height: 400px;
  position: relative;
}

.no-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  font-style: italic;
  height: 100%;
  width: 100%;
}

#conc-preview, #conc-camera-feed {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  object-fit: contain;
}

.result-content {
  background: rgba(255, 255, 255, 0.05);
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.result-content h4 {
  margin-top: 0;
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.detection-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.detection-info p {
  margin: 0.25rem 0;
}

.basic-result {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.5rem;
}

.basic-result p {
  margin: 0.5rem 0;
}

.preview-result-section {
  margin-bottom: 1.5rem;
}

.preview-result-card {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
}

.preview-result-container {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.preview-column {
  flex: 1;
  min-width: 300px;
}

.result-column {
  flex: 1;
  min-width: 300px;
}

.preview-column h3,
.result-column h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.Preview-container {
  flex: 1;
  min-width: 300px;
  max-width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  min-height: 400px;
  position: relative;
}

.no-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  font-style: italic;
  height: 100%;
  width: 100%;
}

#conc-preview, #conc-camera-feed {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  object-fit: contain;
}

.result-content {
  background: rgba(255, 255, 255, 0.05);
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.result-content h4 {
  margin-top: 0;
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.detection-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.detection-info p {
  margin: 0.25rem 0;
}

.basic-result {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.5rem;
}

.basic-result p {
  margin: 0.5rem 0;
}

.data-section {
  margin-bottom: 1.5rem;
}

.data-card {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
}

.data-card h3 {
  color: var(--secondary-color);
  margin: 0 0 1rem 0;
}

.data-content {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 1rem;
  border: 1px solid var(--border-color);
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 1rem;
}

.data-item {
  display: flex;
  flex-direction: column;
}

.data-label {
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.data-value {
  color: var(--text-primary);
  font-size: 1rem;
}

.detection-details {
  margin-top: 1.5rem;
}

.detection-details h5 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

@media (min-width: 768px) {
  .controls-row {
    flex-wrap: nowrap;
  }
  
  .detection-info {
    grid-template-columns: 1fr 1fr;
  }
  
  .basic-result {
    grid-template-columns: 1fr 1fr;
  }
  
  .preview-result-container {
    flex-wrap: nowrap;
  }
}
</style>