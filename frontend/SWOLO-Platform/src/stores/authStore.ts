import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { login as authServiceLogin, register as authServiceRegister, logout as authServiceLogout, isAuthenticated as authServiceIsAuthenticated, getCurrentUser } from '../services/authService';

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref<any>(null);
  const isLoading = ref(false);
  const error = ref<string | null>(null);
  const isAuthenticated = ref(authServiceIsAuthenticated());

  // 计算属性
  const currentUser = computed(() => user.value);
  const isLoggedIn = computed(() => isAuthenticated.value);

  // 登录 - 仅需要员工ID
  const login = async (credentials: { employeeId: string }) => {
    isLoading.value = true;
    error.value = null;
    
    try {
      // 注意：后端现在只需要员工ID即可登录，不需要密码
      const userData = await authServiceLogin(credentials);
      user.value = userData;
      isAuthenticated.value = true;
      return userData;
    } catch (err: any) {
      error.value = err.message || '登录失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  // 注册
  const register = async (userData: { name: string, employeeId: string, password: string, roleId?: number }) => {
    isLoading.value = true;
    error.value = null;
    
    try {
      const response = await authServiceRegister(userData);
      return response;
    } catch (err: any) {
      error.value = err.message || '注册失败';
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  // 登出
  const logout = () => {
    authServiceLogout();
    user.value = null;
    isAuthenticated.value = false;
    error.value = null;
  };

  // 检查认证状态
  const checkAuthStatus = () => {
    const isAuth = authServiceIsAuthenticated();
    isAuthenticated.value = isAuth;
    
    if (isAuth) {
      user.value = getCurrentUser();
    } else {
      user.value = null;
    }
    
    return isAuth;
  };

  return {
    // 状态
    user,
    isLoading,
    error,
    isAuthenticated,
    
    // 计算属性
    currentUser,
    isLoggedIn,
    
    // 方法
    login,
    register,
    logout,
    checkAuthStatus
  };
});