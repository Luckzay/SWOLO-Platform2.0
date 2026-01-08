<template>
  <div class="experiment-container">
    <h2>智能滴定实验</h2>
    <div class="experiment-controls">
      <div class="camera-section">
        <label for="camera-select">选择摄像头:</label>
        <select id="camera-select" v-model="selectedCamera">
          <option value="">选择摄像头</option>
          <option 
            v-for="camera in cameras" 
            :key="camera.deviceId" 
            :value="camera.deviceId"
          >
            {{ camera.label || `摄像头 ${camera.deviceId}` }}
          </option>
        </select>
        <button @click="startCamera" :disabled="cameraActive">启动摄像头</button>
        <button @click="stopCamera" :disabled="!cameraActive">停止摄像头</button>
      </div>
      <div class="port-section">
        <label for="port-select">选择串口:</label>
        <select id="port-select" v-model="selectedPort">
          <option value="">选择串口</option>
          <option 
            v-for="port in ports" 
            :key="port.path" 
            :value="port.path"
          >
            {{ port.path }}
          </option>
        </select>
        <button @click="refreshPorts">刷新串口</button>
        <button @click="connectPort" :disabled="portConnected">连接串口</button>
      </div>
    </div>
    <div class="camera-preview">
      <video ref="videoRef" id="camera-feed" autoplay playsinline v-show="cameraActive"></video>
      <div class="detection-overlay">
        <div id="detection-result">{{ detectionResult }}</div>
      </div>
    </div>
    <div class="experiment-data">
      <h3>实验数据</h3>
      <div id="titration-data">
        <div v-if="titrationData.length > 0">
          <table>
            <thead>
              <tr>
                <th>时间</th>
                <th>体积 (mL)</th>
                <th>浓度 (M)</th>
                <th>颜色</th>
                <th>终点</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(data, index) in titrationData" :key="index">
                <td>{{ data.time }}</td>
                <td>{{ data.volume }}</td>
                <td>{{ data.concentration }}</td>
                <td>{{ data.color }}</td>
                <td>{{ data.endPointReached ? '是' : '否' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else>暂无实验数据</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

// 响应式数据
const selectedCamera = ref<string>('');
const selectedPort = ref<string>('');
const cameraActive = ref<boolean>(false);
const portConnected = ref<boolean>(false);
const detectionResult = ref<string>('');
const cameras = ref<any[]>([]);
const ports = ref<any[]>([]);
const titrationData = ref<any[]>([]);
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

// 获取串口列表
const getPorts = async () => {
  // 这里应该通过Electron的API获取串口列表
  // 模拟一些串口数据
  ports.value = [
    { path: 'COM1', manufacturer: 'Mock Manufacturer' },
    { path: 'COM2', manufacturer: 'Mock Manufacturer' },
    { path: 'COM3', manufacturer: 'Mock Manufacturer' }
  ];
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
      
      // 开始实时检测
      startRealTimeDetection(stream);
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

// 开始实时检测
const startRealTimeDetection = (stream: MediaStream) => {
  // 模拟定期检测
  const interval = setInterval(async () => {
    if (!cameraActive.value) {
      clearInterval(interval);
      return;
    }

    try {
      // 模拟检测结果
      detectionResult.value = '检测中...';
      
      // 模拟API调用
      // 这里应该实际捕获视频帧并发送到AI服务器
      const mockResult = {
        color: 'colorless',
        endPointReached: Math.random() > 0.7,
        volume: (Math.random() * 50).toFixed(2),
        concentration: (Math.random() * 0.2).toFixed(4),
        confidence: (Math.random() * 0.5 + 0.5).toFixed(2)
      };
      
      detectionResult.value = `颜色: ${mockResult.color}, 体积: ${mockResult.volume}mL, 浓度: ${mockResult.concentration}M`;
      
      // 添加到实验数据
      titrationData.value.push({
        time: new Date().toLocaleTimeString(),
        volume: mockResult.volume,
        concentration: mockResult.concentration,
        color: mockResult.color,
        endPointReached: mockResult.endPointReached
      });
      
      // 限制数据量，只保留最近20条
      if (titrationData.value.length > 20) {
        titrationData.value.shift();
      }
    } catch (error) {
      console.error('Detection error:', error);
      detectionResult.value = '检测失败';
    }
  }, 2000); // 每2秒检测一次

  // 组件卸载时清理定时器
  onUnmounted(() => {
    clearInterval(interval);
  });
};

// 刷新串口列表
const refreshPorts = () => {
  getPorts();
};

// 连接串口
const connectPort = () => {
  if (!selectedPort.value) {
    alert('请选择串口');
    return;
  }
  
  // 这里应该实际连接串口
  portConnected.value = true;
  alert(`已连接到串口: ${selectedPort.value}`);
};

// 组件挂载时获取设备列表
onMounted(async () => {
  await getCameras();
  await getPorts();
});
</script>

<style scoped>
.camera-preview {
  position: relative;
  width: 100%;
  max-width: 800px;
  height: 400px;
  background: #000;
  border-radius: 12px;
  margin: 1.5rem 0;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-color);
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.5);
}

#camera-feed {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detection-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

#detection-result {
  position: absolute;
  top: 1rem;
  left: 1rem;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 1rem;
  backdrop-filter: blur(5px);
}

.experiment-data {
  margin-top: 2rem;
}

.experiment-data h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

#titration-data {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 1.5rem;
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid var(--border-color);
}
</style>