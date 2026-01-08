import { contextBridge, ipcRenderer } from 'electron';

// 安全地暴露API给渲染进程
contextBridge.exposeInMainWorld('electronAPI', {
  // 打开文件对话框
  openFile: () => ipcRenderer.invoke('dialog:openFile'),
  
  // 加载配置
  loadConfig: () => ipcRenderer.invoke('load-config'),
  
  // 检查服务器连接
  checkServerConnection: () => ipcRenderer.invoke('server:check-connection'),
  
  // 其他API...
});