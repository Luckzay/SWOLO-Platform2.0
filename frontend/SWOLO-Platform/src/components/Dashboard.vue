<template>
  <div class="dashboard">
    <div class="welcome-card">
      <h2>欢迎使用 SWOLO-Platform</h2>
      <p>化学实验智能平台为您提供先进的实验辅助功能</p>
    </div>
    <div class="feature-grid">
      <div class="feature-card" @click="goToModule('titration')">
        <h3>滴定实验</h3>
        <p>智能滴定辅助，实时颜色检测，自动控制滴定速度</p>
      </div>
      <div class="feature-card" @click="goToModule('concentration')">
        <h3>浓度检测</h3>
        <p>基于图像分析的溶液浓度检测</p>
      </div>
      <div class="feature-card" @click="goToModule('characterization')">
        <h3>智能表征</h3>
        <p>TEM图像分析，粒径分布绘制</p>
      </div>
      <div class="feature-card" @click="goToModule('data-analysis')">
        <h3>数据分析</h3>
        <p>实验数据可视化与分析</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits } from 'vue';

const emit = defineEmits<{
  'navigate-to-module': [module: string]
}>();

const goToModule = (module: string) => {
  emit('navigate-to-module', module);
};
</script>

<style scoped>
.dashboard {
  /* 样式已在父组件中定义 */
}
</style>