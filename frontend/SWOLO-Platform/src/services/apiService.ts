// AI后端API配置 (Flask)
export const AI_API_CONFIG = {
  baseUrl: typeof process !== 'undefined' && process.env?.VUE_APP_AI_SERVER_URL 
    ? process.env.VUE_APP_AI_SERVER_URL 
    : 'http://localhost:5000', // Flask AI后端默认端口
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

// 应用后端API配置 (Spring Boot)
export const APP_API_CONFIG = {
  baseUrl: typeof process !== 'undefined' && process.env?.VUE_APP_APP_SERVER_URL 
    ? process.env.VUE_APP_APP_SERVER_URL 
    : 'http://localhost:8080', // Spring Boot应用后端默认端口
  timeout: 30000,
  retries: 3,
  endpoints: {
    // 认证相关
    auth: {
      login: '/api/auth/login',
      register: '/api/auth/register'
    },
    // 数据上传相关
    data: {
      upload: '/api/data/upload',
      uploadBatch: '/api/data/upload/batch'
    },
    // 统计分析相关
    statistics: {
      experiments: {
        summary: '/api/statistics/experiments/overall',
        user: (userId: number) => `/api/statistics/experiments/user/${userId}`,
        type: (typeId: number) => `/api/statistics/experiments/type/${typeId}`,
        timeRange: '/api/statistics/experiments/time-range',
        detailed: (expId: number) => `/api/statistics/experiments/${expId}/detailed`
      },
      users: {
        count: '/api/statistics/users/count',
        list: '/api/statistics/users/all',
        byRole: (roleId: number) => `/api/statistics/users/by-role/${roleId}`,
        experiments: (userId: number) => `/api/statistics/users/${userId}/experiments`
      },
      comprehensive: {
        summary: '/api/statistics/comprehensive/summary',
        userActivity: '/api/statistics/comprehensive/user-activity',
        user: (userId: number) => `/api/statistics/comprehensive/user/${userId}`
      }
    }
  }
};

// 导入认证相关函数
import { getAuthHeaders } from './authService';

// 检查AI服务器连接状态
export async function checkAIServerConnection(): Promise<boolean> {
  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), AI_API_CONFIG.timeout);

    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.health}`, {
      signal: controller.signal
    });
    
    clearTimeout(timeoutId);
    return response.ok;
  } catch (error) {
    console.error('AI Server connection check failed:', error);
    return false;
  }
}

// 检查应用服务器连接状态
export async function checkAppServerConnection(): Promise<boolean> {
  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), APP_API_CONFIG.timeout);

    // 尝试访问一个公共端点来检查连接
    const response = await fetch(`${APP_API_CONFIG.baseUrl}/api/auth/login`, {
      method: 'OPTIONS', // 使用OPTIONS请求检查服务可用性
      signal: controller.signal
    });
    
    clearTimeout(timeoutId);
    // 如果响应状态是405（方法不允许）或其他非错误状态，说明服务是可达的
    return response.status !== 0 && response.status !== 404;
  } catch (error) {
    console.error('App Server connection check failed:', error);
    return false;
  }
}

// 检查两个服务器的连接状态
export async function checkServerConnection(): Promise<boolean> {
  try {
    // 检查AI服务器
    const aiConnected = await checkAIServerConnection();
    // 检查应用服务器
    const appConnected = await checkAppServerConnection();
    
    return aiConnected || appConnected; // 如果任一服务器连接成功，则认为整体连接成功
  } catch (error) {
    console.error('Server connection check failed:', error);
    return false;
  }
}

// 重试机制的滴定预测函数 (AI后端)
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
    
    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.titration}`, {
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
      if (response.status >= 500 && retryCount < AI_API_CONFIG.retries) {
        // 服务器错误，重试
        await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1))); // 指数退避
        return getTitrationPredictionWithRetry(imageData, options, retryCount + 1);
      }
      throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    if (retryCount < AI_API_CONFIG.retries) {
      console.log(`Retry ${retryCount + 1}/${AI_API_CONFIG.retries} for titration:`, (error as Error).message);
      await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
      return getTitrationPredictionWithRetry(imageData, options, retryCount + 1);
    }
    console.error(`Titration prediction request failed after ${AI_API_CONFIG.retries} retries:`, error);
    throw error;
  }
}

// 重试机制的浓度预测函数 (AI后端)
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
    
    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.concentration}`, {
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
      if (response.status >= 500 && retryCount < AI_API_CONFIG.retries) {
        // 服务器错误，重试
        await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1))); // 指数退避
        return getConcentrationPredictionWithRetry(imageData, options, retryCount + 1);
      }
      throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    if (retryCount < AI_API_CONFIG.retries) {
      console.log(`Retry ${retryCount + 1}/${AI_API_CONFIG.retries} for concentration:`, (error as Error).message);
      await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
      return getConcentrationPredictionWithRetry(imageData, options, retryCount + 1);
    }
    console.error(`Concentration prediction request failed after ${AI_API_CONFIG.retries} retries:`, error);
    throw error;
  }
}

// 重试机制的表征预测函数 (AI后端)
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
    
    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.characterization}`, {
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
      if (response.status >= 500 && retryCount < AI_API_CONFIG.retries) {
        // 服务器错误，重试
        await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1))); // 指数退避
        return getCharacterizationPredictionWithRetry(imageData, options, retryCount + 1);
      }
      throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    if (retryCount < AI_API_CONFIG.retries) {
      console.log(`Retry ${retryCount + 1}/${AI_API_CONFIG.retries} for characterization:`, (error as Error).message);
      await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1)));
      return getCharacterizationPredictionWithRetry(imageData, options, retryCount + 1);
    }
    console.error(`Characterization prediction request failed after ${AI_API_CONFIG.retries} retries:`, error);
    throw error;
  }
}

// 从AI服务器获取滴定预测结果
export async function getTitrationPrediction(imageData: string, options: Record<string, any> = {}) {
  return getTitrationPredictionWithRetry(imageData, options, 0);
}

// 从AI服务器获取浓度预测结果
export async function getConcentrationPrediction(imageData: string, options: Record<string, any> = {}) {
  return getConcentrationPredictionWithRetry(imageData, options, 0);
}

// 从AI服务器获取表征预测结果
export async function getCharacterizationPrediction(imageData: string, options: Record<string, any> = {}) {
  return getCharacterizationPredictionWithRetry(imageData, options, 0);
}

// 从AI服务器获取预测结果的通用函数（保留以兼容旧代码）
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

// 获取模型信息 (AI后端)
export async function getModelInfo(): Promise<any> {
  try {
    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.info}`);
    if (!response.ok) {
      throw new Error(`Failed to get model info: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error getting model info:', error);
    throw error;
  }
}

// 获取可用模型列表 (AI后端)
export async function getAvailableModels(): Promise<any> {
  try {
    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.models}`);
    if (!response.ok) {
      throw new Error(`Failed to get models: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error getting models:', error);
    throw error;
  }
}

// 切换模型 (AI后端)
export async function switchModel(modelName: string): Promise<any> {
  try {
    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.switchModel}`, {
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

// 通用预测函数（用于兼容旧版API）(AI后端)
export async function predictImage(imageData: string, returnImage: boolean = true): Promise<any> {
  try {
    const response = await fetch(`${AI_API_CONFIG.baseUrl}${AI_API_CONFIG.endpoints.predict}`, {
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
        aiBaseUrl: AI_API_CONFIG.baseUrl,
        appBaseUrl: APP_API_CONFIG.baseUrl,
        timeout: AI_API_CONFIG.timeout,
        retries: AI_API_CONFIG.retries
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

// 实验数据上传相关函数 (应用后端)

// 上传单个实验数据
export async function uploadExperimentData(data: any): Promise<any> {
  try {
    const response = await fetch(`${APP_API_CONFIG.baseUrl}${APP_API_CONFIG.endpoints.data.upload}`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      throw new Error(`Failed to upload experiment data: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error uploading experiment data:', error);
    throw error;
  }
}

// 批量上传实验数据
export async function batchUploadExperimentData(data: any[]): Promise<any> {
  try {
    const response = await fetch(`${APP_API_CONFIG.baseUrl}${APP_API_CONFIG.endpoints.data.uploadBatch}`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      throw new Error(`Failed to batch upload experiment data: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error in batch upload experiment data:', error);
    throw error;
  }
}

// 统计分析相关函数 (应用后端)

// 获取总体实验统计
export async function getOverallExperimentStats(): Promise<any> {
  try {
    const response = await fetch(`${APP_API_CONFIG.baseUrl}${APP_API_CONFIG.endpoints.statistics.experiments.summary}`, {
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      throw new Error(`Failed to get overall experiment stats: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error getting overall experiment stats:', error);
    throw error;
  }
}

// 获取用户实验统计
export async function getUserExperimentStats(userId: number): Promise<any[]> {
  try {
    const response = await fetch(`${APP_API_CONFIG.baseUrl}${APP_API_CONFIG.endpoints.statistics.experiments.user(userId)}`, {
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      throw new Error(`Failed to get user experiment stats: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error getting user experiment stats:', error);
    throw error;
  }
}

// 获取用户统计数据
export async function getUserStats(): Promise<any> {
  try {
    const response = await fetch(`${APP_API_CONFIG.baseUrl}${APP_API_CONFIG.endpoints.statistics.users.count}`, {
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      throw new Error(`Failed to get user count: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error getting user stats:', error);
    throw error;
  }
}

// 获取综合统计摘要
export async function getComprehensiveStats(): Promise<any> {
  try {
    const response = await fetch(`${APP_API_CONFIG.baseUrl}${APP_API_CONFIG.endpoints.statistics.comprehensive.summary}`, {
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      throw new Error(`Failed to get comprehensive stats: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error getting comprehensive stats:', error);
    throw error;
  }
}