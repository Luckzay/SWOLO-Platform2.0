<template>
  <div class="auth-modal">
    <div class="auth-card">
      <div class="card-header">
        <h2>用户注册</h2>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="name">姓名</label>
          <input 
            id="name" 
            v-model="name" 
            type="text" 
            placeholder="请输入姓名"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="regEmployeeId">员工号</label>
          <input 
            id="regEmployeeId" 
            v-model="employeeId" 
            type="text" 
            placeholder="请输入员工号"
            required
          />
        </div>
        
        <div class="form-actions">
          <button type="submit" :disabled="isLoading">
            <span v-if="isLoading">注册中...</span>
            <span v-else>注册</span>
          </button>
          <p class="switch-auth">
            已有账号？ 
            <a href="#" @click.prevent="$emit('switch-to-login')">立即登录</a>
          </p>
        </div>
        
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '../stores/authStore';

interface Emits {
  'switch-to-login': () => void;
  'close': () => void;
}

const emit = defineEmits<Emits>();

const authStore = useAuthStore();
const name = ref('');
const employeeId = ref('');
const isLoading = ref(false);
const error = ref('');

const handleRegister = async () => {
  if (!name.value.trim()) {
    error.value = '请输入姓名';
    return;
  }
  
  if (!employeeId.value.trim()) {
    error.value = '请输入员工号';
    return;
  }

  isLoading.value = true;
  error.value = '';

  try {
    // 注册时不再需要密码，使用默认密码
    await authStore.register({
      name: name.value,
      employeeId: employeeId.value,
      password: 'default123' // 后端API可能仍需要此字段
    });
    
    alert('注册成功！');
    emit('switch-to-login');
  } catch (err: any) {
    error.value = err.message || '注册失败，请重试';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.auth-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.auth-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 2rem;
  width: 100%;
  max-width: 400px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.card-header h2 {
  color: var(--secondary-color);
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.2rem;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: var(--transition);
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.8rem 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: 1rem;
  transition: var(--transition);
}

.form-group input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 102, 204, 0.3);
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
}

.form-actions button {
  width: 100%;
  padding: 0.8rem;
  border: none;
  border-radius: 8px;
  background: var(--primary-gradient);
  color: white;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  position: relative;
  overflow: hidden;
}

.form-actions button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: var(--transition);
}

.form-actions button:hover::before {
  left: 100%;
}

.form-actions button:hover {
  background: var(--primary-gradient);
  box-shadow: var(--glow);
  transform: translateY(-2px);
}

.form-actions button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.switch-auth {
  text-align: center;
  margin-top: 1rem;
  color: var(--text-secondary);
}

.switch-auth a {
  color: var(--secondary-color);
  text-decoration: none;
  font-weight: 500;
}

.switch-auth a:hover {
  color: var(--accent-color);
  text-decoration: underline;
}

.error-message {
  background: var(--bg-error, rgba(255, 51, 102, 0.1));
  color: var(--text-error, #ff6666);
  padding: 0.8rem;
  border-radius: 8px;
  border: 1px solid var(--border-error, #ff3366);
  margin-top: 1rem;
  text-align: center;
}
</style>