import { createApp } from 'vue'
import App from './App.vue'

// 导入样式
import '../style.css'

// 创建Vue应用实例
const app = createApp(App)

// 挂载应用
app.mount('#app')

// 如果在Electron环境中，调整一些设置
declare global {
  interface Window {
    process?: {
      type?: string;
    };
  }
}

if (window.process && window.process.type === 'renderer') {
  // 这是Electron渲染进程
  console.log('Running in Electron environment')
}