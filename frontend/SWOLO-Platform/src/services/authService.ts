// 认证服务 - 与后端API交互
const BACKEND_API_CONFIG = {
  baseUrl: typeof process !== 'undefined' && process.env?.VUE_APP_APP_SERVER_URL
    ? process.env.VUE_APP_APP_SERVER_URL
    : 'http://localhost:8080', // Spring Boot应用后端地址
  timeout: 30000,
  retries: 3
};

// 登录请求 - 现在只需要员工ID
export interface LoginRequest {
  employeeId: string;
}

// 注册请求
export interface RegisterRequest {
  name: string;
  employeeId: string;
  password: string;
  roleId?: number;
}

// JWT响应
export interface JwtResponse {
  token: string;
  userId: number;
  employeeId: string;
  name: string;
  roles: string[];
}

// 检查用户是否已登录
export function isAuthenticated(): boolean {
  const token = localStorage.getItem('authToken');
  return token !== null && token !== undefined && token.trim() !== '' && isValidJwtToken(token);
}

// 检查JWT令牌格式是否有效
function isValidJwtToken(token: string): boolean {
  if (!token) return false;
  // JWT令牌应该有恰好两个点，分成三部分
  const parts = token.split('.');
  return parts.length === 3 && parts.every(part => part.length > 0);
}

// 获取当前用户信息
export function getCurrentUser(): JwtResponse | null {
  const token = localStorage.getItem('authToken');
  if (!token || !isValidJwtToken(token)) return null;

  try {
    // 解码JWT token以获取用户信息（仅解码头部和载荷部分，不验证签名）
    const base64Url = token.split('.')[1]; // 载荷部分
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
}

// 登录 - 仅使用员工ID
export async function login(credentials: LoginRequest): Promise<JwtResponse> {
  try {
    const response = await fetch(`${BACKEND_API_CONFIG.baseUrl}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials) // 仅发送员工ID
    });
    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(`Login failed: ${response.status} - ${errorData}`);
    }
    const data: JwtResponse = await response.json();
    // 存储token到localStorage
    localStorage.setItem('authToken', data.token);
    return data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}

// 注册
export async function register(userData: RegisterRequest): Promise<any> {
  try {
    const response = await fetch(`${BACKEND_API_CONFIG.baseUrl}/api/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData)
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(`Registration failed: ${response.status} - ${errorData}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Registration error:', error);
    throw error;
  }
}

// 登出
export function logout(): void {
  localStorage.removeItem('authToken');
}

// 获取认证头
export function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem('authToken');
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  // 只有在令牌存在且格式有效时才添加认证头
  if (token && isValidJwtToken(token)) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  return headers;
}
