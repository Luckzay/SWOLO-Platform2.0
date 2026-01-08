import { app, BrowserWindow, ipcMain, dialog, Menu } from 'electron';
import * as path from 'path';

// 保持对窗口对象的全局引用，如果不这样做，窗口被关闭时JavaScript对象将被GC
let mainWindow: BrowserWindow | null;

function createWindow() {
  // 创建浏览器窗口
  mainWindow = new BrowserWindow({
    height: 900,
    webPreferences: {
      nodeIntegration: false, // 禁用nodeIntegration以提高安全性
      contextIsolation: true, // 启用上下文隔离
      preload: path.join(__dirname, 'preload.cjs') // 预加载脚本
    },
    icon: path.join(__dirname, 'public', 'favicon.ico'), // 设置应用图标
    width: 1400,
    minWidth: 1024, // 设置最小窗口尺寸
    minHeight: 768, // 设置最小高度
    backgroundColor: '#f5f7fa' // 设置背景色
  });

  // 加载应用的index.html
  if (process.env.NODE_ENV === 'development') {
    mainWindow.loadURL('http://localhost:5173'); // Vite默认开发服务器端口
  } else {
    mainWindow.loadFile(
      path.join(__dirname, '../dist/index.html')
    );
  }

  // 打开开发工具
  if (process.env.NODE_ENV === 'development') {
    mainWindow.webContents.openDevTools();
  }

  // 当窗口关闭时的事件处理
  mainWindow.on('closed', () => {
    mainWindow = null;
  });
  
  // 禁用右键菜单（在生产环境中）
  if (process.env.NODE_ENV !== 'development') {
    mainWindow.webContents.setContextMenu(false);
  }
}

// 创建菜单栏
function createMenu() {
  const template: Electron.MenuItemConstructorOptions[] = [
    {
      label: '文件',
      submenu: [
        { 
          label: '退出', 
          role: 'quit',
          accelerator: 'Ctrl+Q'
        }
      ]
    },
    {
      label: '编辑',
      submenu: [
        { label: '撤销', role: 'undo' },
        { label: '重做', role: 'redo' },
        { type: 'separator' },
        { label: '剪切', role: 'cut' },
        { label: '复制', role: 'copy' },
        { label: '粘贴', role: 'paste' }
      ]
    },
    {
      label: '视图',
      submenu: [
        { label: '重新加载', role: 'reload' },
        { label: '强制重新加载', role: 'forceReload' },
        { label: '开发者工具', role: 'toggleDevTools' },
        { type: 'separator' },
        { label: '实际大小', role: 'resetZoom' },
        { label: '放大', role: 'zoomIn' },
        { label: '缩小', role: 'zoomOut' }
      ]
    },
    {
      label: '窗口',
      submenu: [
        { label: '最小化', role: 'minimize' },
        { label: '最大化', role: 'maximize' },
        { label: '关闭', role: 'close' }
      ]
    }
  ];

  const menu = Menu.buildFromTemplate(template);
  Menu.setApplicationMenu(menu);
}

// 当Electron完成初始化并准备好创建浏览器窗口时
app.whenReady().then(() => {
  createMenu();
  createWindow();
  
  app.on('activate', () => {
    // 在macOS上，当点击dock图标且没有其他窗口打开时，通常会在应用中重新创建一个窗口
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

// 当所有窗口都关闭时退出应用（macOS除外）
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// IPC处理函数 - 用于从渲染进程接收消息
ipcMain.handle('dialog:openFile', async (_, options?: any) => {
  const result = await dialog.showOpenDialog({
    properties: options?.properties || ['openFile'],
    filters: options?.filters || [
      { name: 'Images', extensions: ['jpg', 'jpeg', 'png', 'gif', 'bmp'] },
      { name: 'All Files', extensions: ['*'] }
    ]
  });
  
  return result;
});

// 用于加载配置文件
ipcMain.handle('load-config', async () => {
  // 这里可以实现配置加载逻辑
  return {
    api: {
      baseUrl: process.env.YOLO_SERVER_URL || 'http://localhost:5000',
      timeout: 30000,
      retries: 3
    },
    serial: {
      baudRate: 9600,
      dataBits: 8,
      stopBits: 1,
      parity: "none"
    },
    camera: {
      defaultResolution: {
        width: 640,
        height: 480
      },
      frameRate: 30
    },
    ui: {
      theme: "dark",
      language: "zh-CN"
    }
  };
});

// 服务器连接检查
ipcMain.handle('server:check-connection', async (_, url: string) => {
  // 这里可以实现服务器连接检查逻辑
  // 注意：在主进程中不能直接使用fetch，需要使用node-fetch或其他方法
  const fetch = (await import('node-fetch')).default;
  
  try {
    const response = await fetch(url || 'http://localhost:5000/health', {
      method: 'GET',
      timeout: 5000
    });
    
    return { success: response.ok, status: response.status };
  } catch (error) {
    return { success: false, error: (error as Error).message };
  }
});