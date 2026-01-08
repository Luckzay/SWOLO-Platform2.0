<template>
  <div class="model-manager">
    <div class="model-info-container">
      <h3 class="section-title">模型信息</h3>
      <div v-if="modelInfo" class="model-info-content">
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">模型路径:</span>
            <span class="info-value">{{ modelInfo.model_path }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">类别文件:</span>
            <span class="info-value">{{ modelInfo.classes_path }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">锚框文件:</span>
            <span class="info-value">{{ modelInfo.anchors_path }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">类别数量:</span>
            <span class="info-value">{{ modelInfo.num_classes }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">输入形状:</span>
            <span class="info-value">{{ modelInfo.input_shape ? modelInfo.input_shape.join('×') : 'N/A' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">置信度阈值:</span>
            <span class="info-value">{{ modelInfo.confidence }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">NMS IOU:</span>
            <span class="info-value">{{ modelInfo.nms_iou }}</span>
          </div>
        </div>
      </div>
      <div v-else-if="modelInfo === null && !loadingError" class="loading">
        <div class="loading-spinner"></div>
        <span>加载模型信息...</span>
      </div>
      <div v-else-if="loadingError" class="error-message">
        <div class="error-icon">⚠️</div>
        <div class="error-text">
          <h4>模型信息加载失败</h4>
          <p>{{ loadingError }}</p>
          <button @click="retryLoadModelInfo" class="retry-button">重新加载</button>
        </div>
      </div>
    </div>
    
    <div class="model-controls">
      <h3 class="section-title">模型选择</h3>
      <div class="select-container">
        <select id="model-select" v-model="selectedModel" @change="handleModelChange" :disabled="modelsLoading">
          <option value="" v-if="!models.length && !modelsLoading">暂无模型可用</option>
          <option value="" v-else-if="modelsLoading">加载模型列表...</option>
          <option 
            v-for="model in models" 
            :key="model.name" 
            :value="model.name"
            :selected="model.is_current"
          >
            {{ model.name }} ({{ model.model_type }}) - {{ model.model_path }}
          </option>
        </select>
        <div v-if="modelsLoading" class="select-loading">
          <div class="loading-spinner"></div>
        </div>
      </div>
      <button @click="switchModel" :disabled="!selectedModel || switchingModel || modelsLoading" class="switch-button">
        <span v-if="!switchingModel">切换模型</span>
        <span v-else class="switching-text">
          <span class="loading-spinner-small"></span> 切换中...
        </span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps } from 'vue';
import { getModelInfo, getAvailableModels, switchModel as switchModelAPI } from '../services/apiService';

// 定义组件属性
const props = defineProps<{
  moduleType: string; // 'titration', 'concentration', 'characterization', 'data-analysis'
}>();

// 定义响应式数据
const modelInfo = ref<any>(null);
const models = ref<any[]>([]);
const selectedModel = ref<string>('');
const switchingModel = ref<boolean>(false);
const modelsLoading = ref<boolean>(false);
const loadingError = ref<string | null>(null);

// 加载模型信息
const loadModelInfo = async () => {
  loadingError.value = null; // 重置错误信息
  try {
    const info = await getModelInfo();
    modelInfo.value = info;
  } catch (error) {
    console.error('Failed to load model info:', error);
    modelInfo.value = null;
    loadingError.value = (error as Error).message || '获取模型信息失败';
  }
};

// 加载可用模型
const loadModels = async () => {
  modelsLoading.value = true;
  try {
    const response = await getAvailableModels();
    if (response.success) {
      models.value = response.models;
      // 设置当前模型为默认选中
      const currentModel = response.models.find((model: any) => model.is_current);
      if (currentModel) {
        selectedModel.value = currentModel.name;
      }
    } else {
      models.value = [];
      console.error('Failed to get models: ', response.error?.message || 'Unknown error');
    }
  } catch (error) {
    console.error('Failed to load models:', error);
    models.value = [];
  } finally {
    modelsLoading.value = false;
  }
};

// 切换模型
const switchModel = async () => {
  if (!selectedModel.value) return;
  
  switchingModel.value = true;
  try {
    await switchModelAPI(selectedModel.value);
    // 切换成功后重新加载模型信息
    await loadModelInfo();
    // 发出模型切换成功的事件
    emit('model-switched', selectedModel.value);
  } catch (error) {
    console.error('Failed to switch model:', error);
    alert('模型切换失败: ' + (error as Error).message);
  } finally {
    switchingModel.value = false;
  }
};

// 处理模型选择变化
const handleModelChange = () => {
  // 这里可以添加一些验证逻辑
};

// 重新加载模型信息
const retryLoadModelInfo = async () => {
  await loadModelInfo();
};

// 组件挂载时加载数据
onMounted(async () => {
  await loadModels();
  await loadModelInfo();
});

// 定义事件
const emit = defineEmits<{
  'model-switched': [modelName: string]
}>();
</script>

<style scoped>
.model-manager {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: var(--card-shadow);
  transition: var(--transition);
}

.model-manager:hover {
  box-shadow: var(--card-shadow-hover);
}

.section-title {
  color: var(--text-title);
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-color);
  font-size: 1.2rem;
}

.model-info-container {
  margin-bottom: 1.5rem;
}

.model-info-content {
  background: var(--bg-secondary);
  border-radius: 8px;
  padding: 1rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 0.75rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 0.5rem;
}

.info-label {
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.info-value {
  color: var(--text-primary);
  word-break: break-all;
  font-family: monospace;
  font-size: 0.9rem;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  color: var(--text-secondary);
  font-style: italic;
  padding: 2rem 0;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid var(--border-color);
  border-top: 2px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-spinner-small {
  width: 12px;
  height: 12px;
  border: 2px solid var(--border-color);
  border-top: 2px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
  margin-right: 0.5rem;
  vertical-align: middle;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: var(--bg-error);
  border: 1px solid var(--border-error);
  border-radius: 8px;
  color: var(--text-error);
}

.error-icon {
  font-size: 2rem;
}

.error-text h4 {
  margin: 0 0 0.5rem 0;
  color: var(--text-error);
}

.error-text p {
  margin: 0 0 1rem 0;
}

.retry-button {
  background: var(--primary);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: var(--transition);
}

.retry-button:hover {
  background: var(--primary-hover);
}

.model-controls {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.select-container {
  position: relative;
  width: 100%;
  max-width: 400px;
}

.select-container select {
  width: 100%;
  padding: 0.75rem;
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 1rem;
  appearance: none;
  padding-right: 2.5rem;
}

.select-container .select-loading {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
}

.switch-button {
  background: var(--primary);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: var(--transition);
  max-width: 200px;
}

.switch-button:hover:not(:disabled) {
  background: var(--primary-hover);
}

.switch-button:disabled {
  background: var(--bg-disabled);
  cursor: not-allowed;
  opacity: 0.6;
}

.switching-text {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (min-width: 768px) {
  .model-controls {
    flex-direction: row;
    align-items: end;
  }
  
  .select-container {
    flex-grow: 1;
  }
}
</style>