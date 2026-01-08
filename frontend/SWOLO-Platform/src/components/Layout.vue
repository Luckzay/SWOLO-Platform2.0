<template>
  <div class="app-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="logo-section">
        <h1>SWOLO-Platform</h1>
        <p>化学实验智能平台</p>
      </div>
      <div class="status-section">
        <span 
          id="serverStatus" 
          :class="serverConnected ? 'status-connected' : 'status-disconnected'"
        >
          {{ serverStatus }}
        </span>
        <button 
          id="checkConnection" 
          class="status-btn"
          @click="$emit('checkConnection')"
        >
          检查连接
        </button>
      </div>
      <nav class="nav-menu">
        <button 
          class="nav-btn" 
          :class="{ active: currentSection === 'dashboard' }"
          @click="changeSection('dashboard')"
        >
          仪表板
        </button>
        <button 
          class="nav-btn" 
          :class="{ active: currentSection === 'titration' }"
          @click="changeSection('titration')"
        >
          滴定实验
        </button>
        <button 
          class="nav-btn" 
          :class="{ active: currentSection === 'concentration' }"
          @click="changeSection('concentration')"
        >
          浓度检测
        </button>
        <button 
          class="nav-btn" 
          :class="{ active: currentSection === 'characterization' }"
          @click="changeSection('characterization')"
        >
          智能表征
        </button>
        <button 
          class="nav-btn" 
          :class="{ active: currentSection === 'data-analysis' }"
          @click="changeSection('data-analysis')"
        >
          数据分析
        </button>
      </nav>
    </header>

    <!-- 主内容区域 -->
    <main class="main-content">
      <slot></slot>
    </main>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

interface Props {
  currentSection: string,
  serverStatus: string,
  serverConnected: boolean
}

const props = withDefaults(defineProps<Props>(), {
  serverStatus: '服务器连接: 未连接',
  serverConnected: false
})

const emit = defineEmits<{
  'section-change': [section: string],
  'check-connection': []
}>()

const changeSection = (section: string) => {
  emit('section-change', section)
}
</script>

<style scoped>
/* 头部导航样式 - 科技感 */
.header {
  background: rgba(26, 35, 50, 0.95);
  backdrop-filter: blur(10px);
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow);
  border-bottom: 1px solid rgba(0, 102, 204, 0.3);
  z-index: 100;
}

.logo-section h1 {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 0.2rem;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.logo-section p {
  font-size: 0.9rem;
  opacity: 0.8;
  color: var(--text-secondary);
}

.status-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-right: 1rem;
}

#serverStatus {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  background: rgba(255, 51, 102, 0.2);
  border: 1px solid rgba(255, 51, 102, 0.3);
  color: #ff9999;
}

.status-connected {
  background: rgba(0, 204, 102, 0.2) !important;
  border: 1px solid rgba(0, 204, 102, 0.3) !important;
  color: #99ffcc !important;
}

.status-btn {
  background: var(--primary-gradient);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: var(--transition);
  position: relative;
  overflow: hidden;
}

.status-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.status-btn:hover::before {
  left: 100%;
}

.status-btn:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
}

.nav-menu {
  display: flex;
  gap: 0.8rem;
}

.nav-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(0, 102, 204, 0.3);
  color: var(--text-secondary);
  padding: 0.6rem 1.2rem;
  border-radius: 30px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: var(--transition);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.nav-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 102, 204, 0.2), transparent);
  transition: var(--transition);
}

.nav-btn:hover::before {
  left: 100%;
}

.nav-btn:hover {
  background: rgba(0, 102, 204, 0.2);
  color: white;
  border-color: var(--primary-color);
  box-shadow: var(--glow);
}

.nav-btn.active {
  background: var(--primary-gradient);
  color: white;
  border-color: var(--primary-color);
  font-weight: 600;
  box-shadow: var(--glow);
}

/* 主内容区域 */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
  background: var(--dark-bg);
  background-image: 
    radial-gradient(circle at 10% 20%, rgba(0, 102, 204, 0.05) 0%, transparent 20%),
    radial-gradient(circle at 90% 80%, rgba(0, 195, 255, 0.05) 0%, transparent 20%);
}
</style>