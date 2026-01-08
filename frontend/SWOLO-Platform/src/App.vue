<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Layout from './components/Layout.vue'
import Dashboard from './components/Dashboard.vue'
import Titration from './components/Titration.vue'
import Concentration from './components/Concentration.vue'
import Characterization from './components/Characterization.vue'
import DataAnalysis from './components/DataAnalysis.vue'
import { checkServerConnection } from './services/apiService'

const currentSection = ref('dashboard')
const serverStatus = ref('服务器连接: 未连接')
const serverConnected = ref(false)

// 检查服务器连接状态
const checkServerStatus = async () => {
  const isConnected = await checkServerConnection()
  serverConnected.value = isConnected
  serverStatus.value = isConnected ? '服务器连接: 已连接' : '服务器连接: 未连接'
}

// 导航到模块
const navigateToModule = (module: string) => {
  currentSection.value = module
}

onMounted(() => {
  // 初始化时检查服务器状态
  checkServerStatus()
})
</script>

<template>
  <Layout 
    :current-section="currentSection" 
    :server-status="serverStatus"
    :server-connected="serverConnected"
    @section-change="currentSection = $event"
    @check-connection="checkServerStatus"
  >
    <Dashboard 
      v-if="currentSection === 'dashboard'"
      @navigate-to-module="navigateToModule"
    />
    
    <Titration v-if="currentSection === 'titration'" />
    <Concentration v-if="currentSection === 'concentration'" />
    <Characterization v-if="currentSection === 'characterization'" />
    <DataAnalysis v-if="currentSection === 'data-analysis'" />
  </Layout>
</template>

<style scoped>
/* 仪表板样式 - 科技感 */
.welcome-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 2.5rem;
  text-align: center;
  box-shadow: var(--shadow);
  margin-bottom: 2rem;
  background: var(--primary-gradient);
  color: white;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0, 102, 204, 0.3);
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: rotate 10s linear infinite;
}

.welcome-card h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
  position: relative;
  z-index: 1;
}

.welcome-card p {
  font-size: 1.1rem;
  opacity: 0.9;
  position: relative;
  z-index: 1;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.feature-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 1.8rem;
  text-align: center;
  box-shadow: var(--shadow);
  transition: var(--transition);
  cursor: pointer;
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
  z-index: 1;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0, 102, 204, 0.1) 0%, transparent 50%);
  z-index: -1;
  opacity: 0;
  transition: var(--transition);
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
  border-color: var(--primary-color);
  background: var(--card-bg-hover);
}

.feature-card h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
  font-size: 1.3rem;
  position: relative;
  z-index: 2;
}

.feature-card p {
  color: var(--text-secondary);
  font-size: 0.95rem;
  line-height: 1.6;
  position: relative;
  z-index: 2;
}

.experiment-container {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: var(--shadow);
  min-height: 600px;
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
}

.experiment-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--primary-gradient);
  z-index: 2;
}

.experiment-container h2 {
  color: var(--secondary-color);
  margin-bottom: 1.5rem;
  font-size: 1.6rem;
  position: relative;
  z-index: 2;
}

.experiment-controls {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  position: relative;
  z-index: 2;
}

.camera-section, .port-section {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  flex: 1;
  min-width: 250px;
}

.camera-section label, .port-section label {
  font-weight: 600;
  color: var(--text-primary);
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
}

.analysis-result {
  flex: 1;
  min-width: 300px;
}

.calibration-section {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 2;
}

.calibration-section h3 {
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.calibration-controls {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-top: 2rem;
}

.Preview-section {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
}

.results-section {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
}

.chart-container {
  width: 100%;
  height: 300px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
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

#particles-summary {
  margin-top: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid var(--border-color);
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

.data-controls {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  position: relative;
  z-index: 2;
}

.analysis-content {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.data-table {
  flex: 1;
  min-width: 400px;
  overflow-x: auto;
}

#size-distribution-chart {
  width: 100% !important;
  height: 100% !important;
  max-width: 100%;
  max-height: 100%;
}

#analysis-chart {
  width: 100%;
  height: 100%;
}

#concentration-result, #particles-data {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 1.5rem;
  min-height: 150px;
  border: 1px solid var(--border-color);
}

#size-distribution-chart {
  width: 100% !important;
  height: 300px !important;
  margin-bottom: 1.5rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 1rem;
}
</style>