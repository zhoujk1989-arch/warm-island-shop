<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { fetchOrders } from '../api/orders.js'

const router = useRouter()
const authStore = useAuthStore()
const orders = ref([])
const loading = ref(false)
const errorMsg = ref('')

async function loadOrders() {
  loading.value = true
  errorMsg.value = ''
  try {
    orders.value = await fetchOrders()
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadOrders()
})

const statusText = (status) => {
  return {
    'PENDING': '待支付',
    'PAID': '已支付',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
  }[status] || status
}
</script>

<template>
  <div class="min-h-screen pt-20 bg-bg-warm">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-3xl font-bold text-text-main mb-8">我的订单</h1>

      <div v-if="loading" class="text-center py-20 text-text-subtle">加载中...</div>

      <div v-else-if="errorMsg" class="text-center py-20 text-red-500">{{ errorMsg }}</div>

      <div v-else-if="!orders.length" class="text-center py-20 bg-white rounded-2xl">
        <p class="text-text-subtle mb-4">暂无订单</p>
        <router-link to="/shop" class="inline-block px-8 py-3 bg-primary text-white rounded-full hover:bg-primary-dark transition-colors">
          去逛逛
        </router-link>
      </div>

      <div v-else class="space-y-4">
        <div v-for="order in orders" :key="order.id"
          class="bg-white rounded-2xl p-6 border border-border-light">
          <div class="flex items-center justify-between mb-3">
            <span class="text-sm text-text-subtle">订单号：{{ order.id }}</span>
            <span class="text-sm font-medium text-primary">{{ statusText(order.status) }}</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-text-subtle text-sm">下单时间：{{ order.createTime?.replace('T', ' ')?.slice(0, 19) }}</span>
            <span class="text-lg font-bold text-primary">¥{{ Number(order.totalAmount).toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
