<template>
  <div class="experiment-container">
    <h2>智能表征 - TEM图像分析</h2>

    <!-- 模型管理区域 -->
    <ModelManager moduleType="characterization" />

    <!-- 控制面板卡片 -->
    <div class="control-panel-card">
      <h3>控制面板</h3>
      <div class="control-content">
        <div class="upload-section">
          <input
            type="file"
            id="tem-upload"
            accept="image/*"
            multiple
            @change="handleImageUpload"
          />
          <label for="tem-upload" class="upload-btn">上传TEM图像</label>
          <button @click="analyzeCharacterization" :disabled="!selectedFiles || selectedFiles.length === 0">
            分析图像
          </button>
        </div>

        <div class="calibration-section">
          <h4>比例尺标定</h4>
          <div class="calibration-controls">
            <label for="scale-length">实际长度 (nm):</label>
            <input
              type="number"
              id="scale-length"
              placeholder="输入比例尺实际长度"
              v-model.number="scaleLength"
              :min="0.1"
              step="0.1"
            />
            <button @click="startCalibration" :disabled="!imagePreview">标定比例尺</button>
            <span id="scale-factor-display">比例因子: {{ scaleFactor > 0 ? `${scaleFactor.toFixed(4)} nm/像素` : '未标定' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图像预览与结果卡片 -->
    <div class="image-preview-result-card">
      <h3>图像预览与分析结果</h3>
      <div class="image-content">
        <div class="image-preview-section">
          <h4>图像预览</h4>
          <div class="Preview-container">
            <img
              v-if="imagePreview"
              :src="imagePreview"
              id="tem-preview"
              alt="TEM预览"
              @click="handleImageClick"
              ref="imageRef"
            />
            <div v-else class="no-image">请选择图像文件</div>
          </div>
        </div>

        <div class="image-result-section">
          <h4>分析结果</h4>
          <div class="result-container">
            <img
              v-if="resultImage"
              :src="resultImage"
              id="result-image"
              alt="分析结果"
            />
            <div v-else class="no-result">暂无分析结果</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 粒径分布图 -->
    <div class="distribution-chart-card">
      <div class="chart-header">
        <h3>粒径分布直方图</h3>
        <button @click="exportChartData" class="export-btn">导出粒径数据</button>
      </div>
      <div class="chart-container">
        <div ref="chartContainerRef" id="size-distribution-chart" style="width: 100%; height: 100%;"></div>
      </div>
    </div>

    <!-- 检测到的颗粒信息 -->
    <div class="particles-data-card">
      <div class="particles-header">
        <h3>检测到的颗粒</h3>
        <button @click="exportParticlesData" class="export-btn">导出颗粒数据</button>
      </div>

      <div id="particles-summary" v-if="particles.length > 0">
        <h4>统计信息</h4>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-label">检测到粒子数量</div>
            <div class="stat-value">{{ particles.length }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">平均粒径</div>
            <div class="stat-value">{{ averageDiameter.toFixed(2) }} nm</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">标准差</div>
            <div class="stat-value">{{ stdDeviation.toFixed(2) }} nm</div>
          </div>
        </div>
      </div>

      <div class="particles-table-container">
        <table id="particles-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>类别</th>
              <th>置信度</th>
              <th>X坐标</th>
              <th>Y坐标</th>
              <th>直径(nm)</th>
            </tr>
          </thead>
          <tbody id="particles-tbody">
            <tr v-for="(particle, index) in paginatedParticles" :key="index">
              <td>{{ getCurrentIndex(index) }}</td>
              <td>{{ particle.class || '未知' }}</td>
              <td>{{ (particle.confidence * 100).toFixed(2) }}%</td>
              <td>{{ particle.x.toFixed(2) }}</td>
              <td>{{ particle.y.toFixed(2) }}</td>
              <td>{{ calculateActualDiameter(particle).toFixed(2) }} nm</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
          <span id="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
          <select id="page-size" v-model.number="itemsPerPage">
            <option value="10">每页10条</option>
            <option value="20">每页20条</option>
            <option value="50">每页50条</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue';
import { getCharacterizationPrediction } from '../services/apiService';
import ModelManager from './ModelManager.vue';
import * as echarts from 'echarts';

// 响应式数据
const selectedFiles = ref<File[] | null>(null);
const imagePreview = ref<string | null>(null);
const resultImage = ref<string | null>(null); // 新增：存储分析结果图像
const particles = ref<any[]>([]);
const loading = ref<boolean>(false);
const scaleLength = ref<number>(100); // 默认100nm
const scaleFactor = ref<number>(0); // 比例因子，nm/像素
const calibrationPoints = ref<number[]>([]);
const currentPage = ref<number>(1);
const itemsPerPage = ref<number>(20);
const chartContainerRef = ref<HTMLDivElement | null>(null);
const chartInstance = ref<echarts.ECharts | null>(null);
const imageRef = ref<HTMLImageElement | null>(null);

// 分页相关计算
const totalPages = computed(() => {
  return Math.ceil(particles.value.length / itemsPerPage.value);
});

const paginatedParticles = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return particles.value.slice(start, end);
});

// 计算平均粒径
const averageDiameter = computed(() => {
  if (particles.value.length === 0) return 0;

  const total = particles.value.reduce((sum, particle) => {
    return sum + calculateActualDiameter(particle);
  }, 0);

  return total / particles.value.length;
});

// 计算标准差
const stdDeviation = computed(() => {
  if (particles.value.length === 0) return 0;

  const avg = averageDiameter.value;
  const squaredDifferences = particles.value.map(particle => {
    const actualDiameter = calculateActualDiameter(particle);
    return Math.pow(actualDiameter - avg, 2);
  });

  const avgSquaredDiff = squaredDifferences.reduce((sum, val) => sum + val, 0) / squaredDifferences.length;
  return Math.sqrt(avgSquaredDiff);
});

// 获取当前行的实际索引
const getCurrentIndex = (index: number) => {
  return (currentPage.value - 1) * itemsPerPage.value + index + 1;
};

// 计算实际直径
const calculateActualDiameter = (particle: any) => {
  if (scaleFactor.value <= 0) {
    // 如果未标定，返回像素直径
    return (particle.width + particle.height) / 2;
  }

  // 使用标定后的比例因子计算实际直径
  const avgDiameterPx = (particle.width + particle.height) / 2;
  return avgDiameterPx * scaleFactor.value;
};

// 处理图像上传
const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    selectedFiles.value = Array.from(target.files);

    // 预览第一张图像
    const file = target.files[0];
    const reader = new FileReader();
    reader.onload = (e) => {
      imagePreview.value = e.target?.result as string;
      resultImage.value = null; // 清空之前的分析结果
    };
    reader.readAsDataURL(file);
  }
};

// 分析表征
const analyzeCharacterization = async () => {
  if (!selectedFiles.value || selectedFiles.value.length === 0) {
    alert('请先上传图片');
    return;
  }

  loading.value = true;
  particles.value = []; // 清空之前的粒子数据

  try {
    // 只处理第一张图像
    const file = selectedFiles.value[0];
    const reader = new FileReader();
    reader.onload = async (e) => {
      const imageData = (e.target?.result as string).split(',')[1];

      try {
        // 调用AI服务进行粒子检测
        const result = await getCharacterizationPrediction(imageData);

        if (result.success) {
          particles.value = result.result?.detections || [];
          currentPage.value = 1; // 重置到第一页

          // 如果服务器返回了处理后的图像，显示它
          if (result.result_image) {
            resultImage.value = `data:image/jpeg;base64,${result.result_image}`;
          }

          // 绘制粒径分布图
          await nextTick();
          drawParticleSizeDistribution();
        } else {
          alert(`分析失败: ${result.message || '未知错误'}`);
        }
      } catch (error) {
        console.error('Characterization error:', error);
        alert('粒子检测失败: ' + (error as Error).message);
      } finally {
        loading.value = false;
      }
    };
    reader.readAsDataURL(file);
  } catch (error) {
    console.error('File reading error:', error);
    alert('读取文件失败: ' + (error as Error).message);
    loading.value = false;
  }
};

// 开始标定
const startCalibration = () => {
  if (!imagePreview.value) {
    alert('请先上传图像');
    return;
  }

  alert('请在图像上点击比例尺的起点和终点');
  calibrationPoints.value = [];
};

// 处理图像点击事件（用于标定）
const handleImageClick = (event: MouseEvent) => {
  if (calibrationPoints.value.length >= 2) {
    return; // 已经选择了两个点，不能再选
  }

  if (!imageRef.value) return;

  const rect = imageRef.value.getBoundingClientRect();
  const x = event.clientX - rect.left;

  calibrationPoints.value.push(x);

  if (calibrationPoints.value.length === 1) {
    console.log(`起点X坐标: ${x}`);
  } else if (calibrationPoints.value.length === 2) {
    // 计算像素距离
    if (calibrationPoints.value[0] !== undefined && calibrationPoints.value[1] !== undefined) {
      const pixelDistance = Math.abs(calibrationPoints.value[1] - calibrationPoints.value[0]);

      // 计算比例因子
      scaleFactor.value = scaleLength.value / pixelDistance;

      console.log(`标定完成: ${scaleLength.value} nm = ${pixelDistance} 像素, 比例因子 = ${scaleFactor.value} nm/像素`);

      // 重新绘制图表
      drawParticleSizeDistribution();
    }
  }
};

// 绘制粒径分布图
const drawParticleSizeDistribution = () => {
  if (!chartContainerRef.value || particles.value.length === 0) {
    // 如果没有数据，尝试清除之前的图表
    if (chartInstance.value) {
      chartInstance.value.dispose();
      chartInstance.value = null;
    }
    return;
  }

  // 计算粒径分布 - 使用实际直径而不是原始的像素尺寸
  const sizes = particles.value.map(particle => calculateActualDiameter(particle));
  if (sizes.length === 0) {
    // 如果没有数据，尝试清除之前的图表
    if (chartInstance.value) {
      chartInstance.value.dispose();
      chartInstance.value = null;
    }
    return;
  }

  const minSize = Math.min(...sizes);
  const maxSize = Math.max(...sizes);
  const range = maxSize - minSize || 1;

  // 创建分布直方图数据
  const bins = 10;
  const binSize = range / bins;
  const histogram: number[] = new Array(bins).fill(0);
  const binRanges: string[] = [];

  // 计算每个区间的范围和计数
  for (let i = 0; i < bins; i++) {
    const rangeStart = minSize + i * binSize;
    const rangeEnd = minSize + (i + 1) * binSize;
    binRanges.push(`${rangeStart.toFixed(1)}-${rangeEnd.toFixed(1)}`);

    sizes.forEach(size => {
      if (size >= rangeStart && size < rangeEnd) {
        histogram[i]++;
      }
    });
  }

  // 最后一个区间包括最大值
  if (bins > 0) {
    const lastBinIndex = bins - 1;
    const lastRangeStart = minSize + lastBinIndex * binSize;
    const lastRangeEnd = minSize + (lastBinIndex + 1) * binSize;
    binRanges[lastBinIndex] = `${lastRangeStart.toFixed(1)}-${lastRangeEnd.toFixed(1)}`;

    sizes.forEach(size => {
      if (size >= lastRangeStart && size <= lastRangeEnd) {
        histogram[lastBinIndex]++;
      }
    });
  }

  // 初始化ECharts实例
  if (chartInstance.value) {
    chartInstance.value.dispose();
  }

  chartInstance.value = echarts.init(chartContainerRef.value);

  // 配置图表选项
  const option: echarts.EChartsOption = {
    title: {
      text: `平均粒径: ${averageDiameter.value.toFixed(2)} nm | 标准差: ${stdDeviation.value.toFixed(2)} nm | 粒子总数: ${particles.value.length}`,
      left: 'center',
      textStyle: {
        fontSize: 12,
        color: '#f5f5f5' // 更深的颜色
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      textStyle: {
        color: '#f1f0f0', // 更深的提示文字颜色
        fontSize: 12
      },
      formatter: (params: any) => {
        const param = params[0];
        return `${param.name}<br/>${param.seriesName}: ${param.value}`;
      }
    },
    grid: {
      left: '10%',
      right: '5%',
      bottom: '15%',
      top: '15%'
    },
    xAxis: {
      type: 'category',
      data: binRanges,
      name: '粒径 (nm)',
      nameLocation: 'middle',
      nameGap: 30,
      axisLabel: {
        interval: 0,
        rotate: 45,
        color: '#f1f0f0' // X轴标签颜色
      },
      axisLine: {
        lineStyle: {
          color: '#666' // X轴线颜色
        }
      },
      nameTextStyle: {
        color: '#f1f0f0' // X轴名称颜色
      }
    },
    yAxis: {
      type: 'value',
      name: '粒子数量',
      nameLocation: 'middle',
      nameGap: 40,
      axisLabel: {
        color: '#f1f0f0' // Y轴标签颜色
      },
      axisLine: {
        lineStyle: {
          color: '#666' // Y轴线颜色
        }
      },
      nameTextStyle: {
        color: '#f1f0f0' // Y轴名称颜色
      }
    },
    series: [{
      data: histogram,
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#2378f7' },
            { offset: 0.7, color: '#2378f7' },
            { offset: 1, color: '#83bff6' }
          ])
        }
      }
    }]
  };

  // 设置图表选项
  chartInstance.value.setOption(option);

  // 监听窗口大小变化，自动调整图表大小
  const handleResize = () => {
    if (chartInstance.value) {
      chartInstance.value.resize();
    }
  };

  window.addEventListener('resize', handleResize);

  // 组件卸载时销毁图表实例
  onUnmounted(() => {
    if (chartInstance.value) {
      chartInstance.value.dispose();
    }
    window.removeEventListener('resize', handleResize);
  });
};

// 分页控制
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

// 导出粒径数据
const exportChartData = () => {
  if (particles.value.length === 0) {
    alert('暂无数据可导出');
    return;
  }

  // 计算粒径分布数据
  const sizes = particles.value.map(particle => calculateActualDiameter(particle));
  if (sizes.length === 0) {
    alert('暂无数据可导出');
    return;
  }

  const minSize = Math.min(...sizes);
  const maxSize = Math.max(...sizes);
  const range = maxSize - minSize || 1;

  // 创建分布直方图
  const bins = 10;
  const binSize = range / bins;
  const histogram = new Array(bins).fill(0);

  sizes.forEach(size => {
    const binIndex = Math.min(Math.floor((size - minSize) / binSize), bins - 1);
    histogram[binIndex]++;
  });

  // 生成CSV内容 - 使用BOM确保中文正确显示
  let csvContent = '\uFEFF'; // 添加BOM以确保中文正确显示
  csvContent += '区间,数量\n';
  for (let i = 0; i < bins; i++) {
    const rangeStart = (minSize + i * binSize).toFixed(2);
    const rangeEnd = (minSize + (i + 1) * binSize).toFixed(2);
    csvContent += `"${rangeStart}-${rangeEnd}nm",${histogram[i]}\n`;
  }

  // 创建下载链接
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', '粒径分布数据.csv');
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

// 导出颗粒数据
const exportParticlesData = () => {
  if (particles.value.length === 0) {
    alert('暂无数据可导出');
    return;
  }

  // 生成CSV内容 - 使用BOM确保中文正确显示
  let csvContent = '\uFEFF'; // 添加BOM以确保中文正确显示
  csvContent += '序号,类别,置信度,X坐标,Y坐标,直径(nm)\n';
  particles.value.forEach((particle, index) => {
    csvContent +=
      `${index + 1},` +
      `"${particle.class || '未知'}",` +
      `${(particle.confidence * 100).toFixed(2)},` +
      `${particle.x.toFixed(2)},` +
      `${particle.y.toFixed(2)},` +
      `${calculateActualDiameter(particle).toFixed(2)}\n`;
  });

  // 创建下载链接
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', '颗粒检测数据.csv');
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};
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

.control-panel-card {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 2;
}

.control-panel-card h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.control-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.upload-section {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

#tem-upload {
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

.upload-section button {
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

.upload-section button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.upload-section button:hover::before {
  left: 100%;
}

.upload-section button:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
  transform: translateY(-2px);
}

.upload-section button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.calibration-section {
  margin: 1rem 0;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.calibration-section h4 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.calibration-controls {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

.calibration-controls label {
  font-weight: 600;
  color: var(--text-primary);
}

.calibration-controls input {
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  min-width: 150px;
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.calibration-controls input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 102, 204, 0.3);
}

.calibration-controls #scale-factor-display {
  font-weight: 600;
  color: var(--secondary-color);
  background: rgba(0, 102, 204, 0.2);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.image-preview-result-card {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.image-preview-result-card h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.image-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.image-preview-section, .image-result-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.image-preview-section h4, .image-result-section h4 {
  color: var(--text-secondary);
  margin: 0;
}

.Preview-container {
  text-align: center;
  border: 2px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
}

#tem-preview {
  max-width: 100%;
  max-height: 400px;
  cursor: crosshair;
  border-radius: 4px;
}

.result-container {
  text-align: center;
  border: 2px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
}

#result-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
}

.no-image, .no-result {
  color: var(--text-secondary);
  font-style: italic;
}

.distribution-chart-card {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  text-align: center;
  position: relative;
}

.distribution-chart-card .chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.distribution-chart-card h3 {
  color: var(--secondary-color);
  margin: 0;
}

.chart-container {
  width: 100%;
  max-width: 600px; /* 限制最大宽度 */
  height: 400px; /* 固定高度 */
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 1rem;
  margin: 0 auto; /* 居中 */
  display: flex;
  align-items: center;
  justify-content: center;
}

#size-distribution-chart {
  width: 100% !important;
  height: 100% !important;
  max-width: 100%;
  max-height: 100%;
}

.particles-data-card {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  position: relative;
}

.particles-data-card .particles-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.particles-data-card h3 {
  color: var(--secondary-color);
  margin: 0;
}

#particles-summary {
  margin: 1rem 0;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.stat-label {
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--secondary-color);
}

.particles-table-container {
  margin-top: 1rem;
  overflow-x: auto;
}

#particles-table {
  width: 100%;
  border-collapse: collapse;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--shadow);
  margin-bottom: 1rem;
  border: 1px solid var(--border-color);
}

#particles-table th,
#particles-table td {
  padding: 0.8rem 1rem;
  text-align: center;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-primary);
}

#particles-table th {
  background: rgba(0, 102, 204, 0.3);
  color: white;
  font-weight: 600;
}

#particles-table tr:nth-child(even) {
  background: rgba(255, 255, 255, 0.03);
}

#particles-table tr:hover {
  background: rgba(0, 102, 204, 0.2);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.05);
  cursor: pointer;
  border-radius: 8px;
  transition: var(--transition);
  color: var(--text-primary);
}

.pagination button:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination #page-info {
  min-width: 80px;
  text-align: center;
  font-weight: 600;
  color: var(--text-secondary);
}

.pagination select {
  padding: 0.5rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.export-btn {
  padding: 0.5rem 1rem;
  background: var(--success-color);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: var(--transition);
  font-weight: 500;
}

.export-btn:hover {
  background: #00b359;
  box-shadow: 0 0 10px rgba(0, 204, 102, 0.5);
}
</style>
