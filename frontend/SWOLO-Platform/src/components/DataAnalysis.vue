<template>
  <div class="experiment-container">
    <h2>实验数据分析</h2>
    <div class="data-controls">
      <button @click="importData">导入数据</button>
      <button @click="exportData">导出数据</button>
      <select id="analysis-type" v-model="analysisType">
        <option value="chart">图表分析</option>
        <option value="statistic">统计分析</option>
        <option value="report">生成报告</option>
      </select>
      <button @click="fetchBackendStats" :disabled="loadingStats">刷新统计</button>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview" v-if="backendStats && backendStats.totalUsers !== undefined">
      <div class="stat-card">
        <h4>总用户数</h4>
        <p>{{ backendStats.totalUsers || 0 }}</p>
      </div>
      <div class="stat-card">
        <h4>总实验数</h4>
        <p>{{ backendStats.overallExperimentStats?.totalDataPoints || 0 }}</p>
      </div>
      <div class="stat-card">
        <h4>平均浓度</h4>
        <p>{{ (backendStats.overallExperimentStats?.averageConcentration !== undefined) ? Number(backendStats.overallExperimentStats.averageConcentration).toFixed(2) : 0 }}</p>
      </div>
    </div>

    <div class="analysis-content">
      <div class="chart-container">
        <canvas id="analysis-chart" ref="chartCanvasRef"></canvas>
      </div>
      <div class="data-table">
        <table id="data-table">
          <thead>
            <tr>
              <th>实验编号</th>
              <th>实验类型</th>
              <th>时间</th>
              <th>结果</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(data, index) in mockData" :key="index">
              <td>{{ data.id }}</td>
              <td>{{ data.type }}</td>
              <td>{{ data.time }}</td>
              <td>{{ data.result }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 加载指示器 -->
    <div v-if="loadingStats" class="loading-stats">
      正在加载统计数据...
    </div>

    <!-- 模型管理区域 -->
    <ModelManager moduleType="data-analysis" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import ModelManager from './ModelManager.vue';
import { getComprehensiveStats } from '../services/apiService';
import { isAuthenticated } from '../services/authService';

// 响应式数据
const analysisType = ref<string>('chart');
const chartCanvasRef = ref<HTMLCanvasElement | null>(null);
const mockData = ref([
  { id: 'EXP001', type: '滴定实验', time: '2023-05-01 10:30', result: '成功' },
  { id: 'EXP002', type: '浓度检测', time: '2023-05-02 14:15', result: '成功' },
  { id: 'EXP003', type: '智能表征', time: '2023-05-03 09:45', result: '成功' },
  { id: 'EXP004', type: '滴定实验', time: '2023-05-04 16:20', result: '失败' },
  { id: 'EXP005', type: '浓度检测', time: '2023-05-05 11:10', result: '成功' }
]);
const backendStats = ref<any>(null);
const loadingStats = ref(false);

// 导入数据
const importData = () => {
  alert('导入数据功能正在开发中...');
  // 这里应该实现数据导入逻辑
};

// 导出数据
const exportData = () => {
  alert('导出数据功能正在开发中...');
  // 这里应该实现数据导出逻辑
};

// 从后端获取统计数据
const fetchBackendStats = async () => {
  // 检查用户是否已认证
  if (!isAuthenticated()) {
    console.warn('用户未认证，无法获取统计数据');
    backendStats.value = null;
    loadingStats.value = false;
    return;
  }

  loadingStats.value = true;
  try {
    const stats = await getComprehensiveStats();
    backendStats.value = stats;
  } catch (error) {
    console.error('获取统计数据失败:', error);
    // 不再弹窗提醒，避免未登录时频繁弹窗
    if (error.message.includes('401') || error.message.includes('403')) {
      console.warn('认证失败，无法获取统计数据');
    } else {
      alert('获取统计数据失败，请检查后端连接');
    }
  } finally {
    loadingStats.value = false;
  }
};

// 绘制图表
const drawChart = () => {
  if (!chartCanvasRef.value) return;

  const canvas = chartCanvasRef.value;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // 设置画布尺寸
  canvas.width = canvas.clientWidth;
  canvas.height = canvas.clientHeight;

  // 清除画布
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  // 绘制图表标题
  ctx.font = '16px Arial';
  ctx.fillStyle = '#e6f0ff'; // 使用CSS变量的值
  ctx.textAlign = 'center';
  ctx.fillText('实验数据分析图表', canvas.width / 2, 30);

  // 如果有后端统计数据，使用真实数据绘制图表
  if (backendStats.value && backendStats.value.overallExperimentStats) {
    // 绘制简单的柱状图示例
    const totalUsers = backendStats.value.totalUsers || 0;
    const totalDataPoints = backendStats.value.overallExperimentStats.totalDataPoints || 0;
    const averageConcentration = Number((backendStats.value.overallExperimentStats.averageConcentration || 0).toFixed(2));

    const data = [totalUsers, totalDataPoints, averageConcentration]; // [用户数, 数据点数, 平均浓度]
    const labels = ['用户数', '数据点', '平均浓度'];
    const barWidth = (canvas.width - 100) / data.length;
    const maxValue = Math.max(...data.map(Math.abs), 10) || 10; // 避免除零，使用绝对值

    ctx.fillStyle = '#007bff';
    for (let i = 0; i < data.length; i++) {
      const barHeight = (Math.abs(data[i]) / maxValue) * (canvas.height - 100); // 使用绝对值计算高度
      const x = 50 + i * barWidth + 10;
      const y = canvas.height - 50 - barHeight;

      ctx.fillRect(x, y, barWidth - 20, barHeight);

      // 添加数值标签
      ctx.fillStyle = '#e6f0ff'; // 使用CSS变量的值
      ctx.textAlign = 'center';
      ctx.fillText(data[i].toString(), x + (barWidth - 20) / 2, y - 10);

      // 添加标签
      ctx.fillText(labels[i], x + (barWidth - 20) / 2, canvas.height - 30);
      ctx.fillStyle = '#007bff';
    }
  } else {
    // 绘制示例数据
    const data = [12, 19, 3, 5, 2, 3]; // 示例数据
    const barWidth = (canvas.width - 100) / data.length;
    const maxValue = Math.max(...data) || 1; // 避免除零

    ctx.fillStyle = '#007bff';
    for (let i = 0; i < data.length; i++) {
      const barHeight = (data[i] / maxValue) * (canvas.height - 100);
      const x = 50 + i * barWidth + 10;
      const y = canvas.height - 50 - barHeight;

      ctx.fillRect(x, y, barWidth - 20, barHeight);

      // 添加数值标签
      ctx.fillStyle = '#e6f0ff'; // 使用CSS变量的值
      ctx.textAlign = 'center';
      ctx.fillText(data[i].toString(), x + (barWidth - 20) / 2, y - 10);
      ctx.fillStyle = '#007bff';
    }
  }
};

// 组件挂载时绘制图表
onMounted(async () => {
  await nextTick();
  drawChart();

  // 尝试获取后端统计数据
  await fetchBackendStats();
});
</script>

<style scoped>
.data-controls {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  position: relative;
  z-index: 2;
}

.data-controls button,
.data-controls select {
  padding: 0.7rem 1.2rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition);
}

.data-controls button {
  background: var(--primary-gradient);
  color: white;
  border: none;
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

.data-controls button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.data-controls button:hover::before {
  left: 100%;
}

.data-controls button:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
  transform: translateY(-2px);
}

.data-controls button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.data-controls select {
  min-width: 180px;
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.stats-overview {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 150px;
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
  transition: var(--transition);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--glow);
}

.stat-card h4 {
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

.stat-card p {
  color: var(--secondary-color);
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0;
}

.analysis-content {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
  margin-bottom: 1.5rem;
}

.chart-container {
  flex: 1;
  min-width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 1rem;
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

#analysis-chart {
  width: 100%;
  height: 100%;
}

.data-table {
  flex: 1;
  min-width: 400px;
  overflow-x: auto;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
}

#data-table {
  width: 100%;
  border-collapse: collapse;
}

#data-table th,
#data-table td {
  padding: 0.8rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-primary);
}

#data-table th {
  background: rgba(0, 102, 204, 0.3);
  color: white;
  font-weight: 600;
}

#data-table tr:nth-child(even) {
  background: rgba(255, 255, 255, 0.03);
}

#data-table tr:hover {
  background: rgba(0, 102, 204, 0.1);
}

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

.loading-stats {
  text-align: center;
  padding: 1rem;
  color: var(--text-secondary);
  font-style: italic;
}
</style>
