<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { login, register } from '../api/auth.js'

const router = useRouter()
const authStore = useAuthStore()

const isLogin = ref(true)
const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

const toggleMode = () => {
  isLogin.value = !isLogin.value
  errorMsg.value = ''
}

const handleSubmit = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    if (isLogin.value) {
      await authStore.login(username.value, password.value)
    } else {
      await authStore.register(username.value, password.value)
    }
    router.push('/')
  } catch (e) {
    const data = e.response?.data
    errorMsg.value = data?.message || '操作失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-[#FEF9F0] px-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-lg p-8 md:p-10">
      <!-- Logo / Title -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-[#E8734A]">暖屿 Warm Island</h1>
        <p class="text-gray-400 mt-1 text-sm">
          {{ isLogin ? '登录你的账号' : '注册新账号' }}
        </p>
      </div>

      <!-- Error -->
      <div v-if="errorMsg" class="mb-4 p-3 bg-red-50 text-red-500 text-sm rounded-lg">
        {{ errorMsg }}
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="space-y-5">
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">用户名</label>
          <input
            v-model="username"
            type="text"
            placeholder="请输入用户名"
            class="w-full px-4 py-2.5 border border-gray-200 rounded-lg
                   focus:outline-none focus:ring-2 focus:ring-[#E8734A]/30
                   focus:border-[#E8734A] transition"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">密码</label>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            class="w-full px-4 py-2.5 border border-gray-200 rounded-lg
                   focus:outline-none focus:ring-2 focus:ring-[#E8734A]/30
                   focus:border-[#E8734A] transition"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full py-2.5 bg-[#E8734A] text-white rounded-lg font-medium
                 hover:bg-[#d65f3a] transition disabled:opacity-50"
        >
          {{ loading ? '处理中...' : (isLogin ? '登录' : '注册') }}
        </button>
      </form>

      <!-- Toggle -->
      <div class="mt-6 text-center text-sm text-gray-400">
        <span v-if="isLogin">还没有账号？</span>
        <span v-else>已有账号？</span>
        <button
          @click="toggleMode"
          class="ml-1 text-[#E8734A] font-medium hover:underline"
        >
          {{ isLogin ? '去注册' : '去登录' }}
        </button>
      </div>
    </div>
  </div>
</template>
