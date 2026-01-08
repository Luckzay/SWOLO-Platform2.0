// API配置
const API_CONFIG = {
  baseUrl: typeof process !== 'undefined' && process.env?.VUE_APP_YOLO_SERVER_URL 
    ? process.env.VUE_APP_YOLO_SERVER_URL 
    : 'http://localhost:5000', // 可以通过环境变量配置
  timeout: 30000,
  retries: 3,
  endpoints: {
    titration: '/predict/titration',
    concentration: '/predict/concentration',
    characterization: '/predict/characterization',
    health: '/health',
    info: '/info',
    models: '/models',
    switchModel: '/models/switch',
    predict: '/predict',
    predictSimple: '/predict_simple'
  }
};

// 检查服务器连接状态
export async function checkServerConnection(): Promise<boolean> {
  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.timeout);

    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.health}`, {
      signal: controller.signal
    });
    
    clearTimeout(timeoutId);
    return response.ok;
  } catch (error) {
    console.error('Server connection check failed:', error);
    return false;
  }
}

// 重试机制的滴定预测函数
export async function getTitrationPredictionWithRetry(
  imageData: string, 
  options: Record<string, any> = {}, 
  retryCount: number = 0
): Promise<any> {
  try {
    // 确保options中包含return_image参数
    const requestOptions = {
      ...options,
      return_image: true  // 添加此参数以请求返回处理后的图像
    };
    
    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.titration}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        image_data: imageData,
        options: requestOptions
      })
    });

    if (!response.ok) {
      if (response.status >= 500 && retryCount < API_CONFIG.retries) {
        // 服务器错误，重试
        await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1))); // 指数退避
        return getTitrationPredictionWithRetry(imageData, options, retryCount + 1);
      }
      throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    if (retryCount < API_CONFIG.retries) {
      console.log(`Retry ${retryCount + 1}/${API_CONFIG.retries} for titration:`, (error as Error).message);
      await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
      return getTitrationPredictionWithRetry(imageData, options, retryCount + 1);
    }
    console.error(`Titration prediction request failed after ${API_CONFIG.retries} retries:`, error);
    throw error;
  }
}

// 重试机制的浓度预测函数
export async function getConcentrationPredictionWithRetry(
  imageData: string, 
  options: Record<string, any> = {}, 
  retryCount: number = 0
): Promise<any> {
  try {
    // 确保options中包含return_image参数
    const requestOptions = {
      ...options,
      return_image: true  // 添加此参数以请求返回处理后的图像
    };
    
    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.concentration}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        image_data: imageData,
        options: requestOptions
      })
    });

    if (!response.ok) {
      if (response.status >= 500 && retryCount < API_CONFIG.retries) {
        // 服务器错误，重试
        await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1))); // 指数退避
        return getConcentrationPredictionWithRetry(imageData, options, retryCount + 1);
      }
      throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    if (retryCount < API_CONFIG.retries) {
      console.log(`Retry ${retryCount + 1}/${API_CONFIG.retries} for concentration:`, (error as Error).message);
      await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
      return getConcentrationPredictionWithRetry(imageData, options, retryCount + 1);
    }
    console.error(`Concentration prediction request failed after ${API_CONFIG.retries} retries:`, error);
    throw error;
  }
}

// 重试机制的表征预测函数
export async function getCharacterizationPredictionWithRetry(
  imageData: string, 
  options: Record<string, any> = {}, 
  retryCount: number = 0
): Promise<any> {
  try {
    // 确保options中包含return_image参数
    const requestOptions = {
      ...options,
      return_image: true  // 添加此参数以请求返回处理后的图像
    };
    
    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.characterization}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        image_data: imageData,
        options: requestOptions
      })
    });

    if (!response.ok) {
      if (response.status >= 500 && retryCount < API_CONFIG.retries) {
        // 服务器错误，重试
        await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1))); // 指数退避
        return getCharacterizationPredictionWithRetry(imageData, options, retryCount + 1);
      }
      throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    if (retryCount < API_CONFIG.retries) {
      console.log(`Retry ${retryCount + 1}/${API_CONFIG.retries} for characterization:`, (error as Error).message);
      await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
      return getCharacterizationPredictionWithRetry(imageData, options, retryCount + 1);
    }
    console.error(`Characterization prediction request failed after ${API_CONFIG.retries} retries:`, error);
    throw error;
  }
}

// 从远程服务器获取滴定预测结果
export async function getTitrationPrediction(imageData: string, options: Record<string, any> = {}) {
  return getTitrationPredictionWithRetry(imageData, options, 0);
}

// 从远程服务器获取浓度预测结果
export async function getConcentrationPrediction(imageData: string, options: Record<string, any> = {}) {
  return getConcentrationPredictionWithRetry(imageData, options, 0);
}

// 从远程服务器获取表征预测结果
export async function getCharacterizationPrediction(imageData: string, options: Record<string, any> = {}) {
  return getCharacterizationPredictionWithRetry(imageData, options, 0);
}

// 从远程服务器获取预测结果的通用函数（保留以兼容旧代码）
export async function getPrediction(taskType: string, imageData: string, options: Record<string, any> = {}) {
  switch(taskType) {
    case 'titration':
      return getTitrationPredictionWithRetry(imageData, options, 0);
    case 'concentration':
      return getConcentrationPredictionWithRetry(imageData, options, 0);
    case 'characterization':
      return getCharacterizationPredictionWithRetry(imageData, options, 0);
    default:
      throw new Error(`Unsupported task type: ${taskType}`);
  }
}

// 获取模型信息
export async function getModelInfo(): Promise<any> {
  try {
    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.info}`);
    if (!response.ok) {
      throw new Error(`Failed to get model info: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error getting model info:', error);
    throw error;
  }
}

// 获取可用模型列表
export async function getAvailableModels(): Promise<any> {
  try {
    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.models}`);
    if (!response.ok) {
      throw new Error(`Failed to get models: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error getting models:', error);
    throw error;
  }
}

// 切换模型
export async function switchModel(modelName: string): Promise<any> {
  try {
    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.switchModel}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        model_name: modelName
      })
    });
    
    if (!response.ok) {
      throw new Error(`Failed to switch model: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error switching model:', error);
    throw error;
  }
}

// 通用预测函数（用于兼容旧版API）
export async function predictImage(imageData: string, returnImage: boolean = true): Promise<any> {
  try {
    const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.predict}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        image_base64: imageData,
        return_image: returnImage
      })
    });
    
    if (!response.ok) {
      throw new Error(`Prediction request failed: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error in predictImage:', error);
    throw error;
  }
}

// 从Electron主进程获取配置
export async function getConfig(): Promise<any> {
  if ((window as any).electronAPI) {
    // 在Electron环境中
    return await (window as any).electronAPI.loadConfig();
  } else {
    // 在浏览器环境中，返回默认配置
    return {
      api: {
        baseUrl: API_CONFIG.baseUrl,
        timeout: API_CONFIG.timeout,
        retries: API_CONFIG.retries
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
  }
}