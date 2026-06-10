<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart.js'
import { useAuthStore } from '../stores/auth.js'
import { createOrder } from '../api/orders.js'

const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()

const shippingAddress = ref('')
const recipientName = ref('')
const recipientPhone = ref('')
const loading = ref(false)
const errorMsg = ref('')

const canSubmit = computed(() =>
  shippingAddress.value.trim() &&
  recipientName.value.trim() &&
  recipientPhone.value.trim() &&
  cartStore.items.length > 0
)

async function handleSubmit() {
  if (!canSubmit.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const items = cartStore.items.map(item => ({
      productId: item.productId,
      variantId: item.variantId || null,
      quantity: item.quantity,
    }))
    await createOrder({
      shippingAddress: shippingAddress.value,
      recipientName: recipientName.value,
      recipientPhone: recipientPhone.value,
      items,
    })
    cartStore.clearCart()
    router.push('/orders')
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '提交订单失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen pt-20 bg-bg-warm">
    <div class="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-3xl font-bold text-text-main mb-8">结算</h1>

      <div v-if="!cartStore.items.length" class="text-center py-20 text-text-subtle">
        购物车是空的，<router-link to="/shop" class="text-primary">去逛逛</router-link>
      </div>

      <form v-else @submit.prevent="handleSubmit" class="space-y-6">
        <!-- 收货信息 -->
        <div class="bg-white rounded-2xl p-6 border border-border-light">
          <h2 class="text-lg font-semibold text-text-main mb-4">收货信息</h2>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-text-subtle mb-1">收件人</label>
              <input v-model="recipientName" type="text" placeholder="请输入收件人姓名"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#E8734A]/30" />
            </div>
            <div>
              <label class="block text-sm font-medium text-text-subtle mb-1">联系电话</label>
              <input v-model="recipientPhone" type="tel" placeholder="请输入联系电话"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#E8734A]/30" />
            </div>
            <div>
              <label class="block text-sm font-medium text-text-subtle mb-1">收货地址</label>
              <textarea v-model="shippingAddress" rows="3" placeholder="请输入详细收货地址"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#E8734A]/30 resize-none" />
            </div>
          </div>
        </div>

        <!-- 商品清单 -->
        <div class="bg-white rounded-2xl p-6 border border-border-light">
          <h2 class="text-lg font-semibold text-text-main mb-4">商品清单</h2>
          <div class="space-y-3">
            <div v-for="item in cartStore.items" :key="item.productId + '-' + (item.variantId || '')"
              class="flex items-center gap-3">
              <img :src="item.image" class="w-12 h-12 rounded-lg object-cover" />
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-text-main truncate">{{ item.name }}</p>
                <p v-if="item.variantName" class="text-xs text-text-subtle">{{ item.variantName }}</p>
              </div>
              <span class="text-sm text-text-subtle">x{{ item.quantity }}</span>
              <span class="text-sm font-medium text-primary">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          <div class="mt-4 pt-4 border-t border-border-light flex justify-between">
            <span class="text-text-subtle">合计</span>
            <span class="text-xl font-bold text-primary">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 错误提示 -->
        <div v-if="errorMsg" class="p-3 bg-red-50 text-red-500 text-sm rounded-lg">{{ errorMsg }}</div>

        <!-- 提交 -->
        <button type="submit" :disabled="!canSubmit || loading"
          class="w-full py-3 bg-[#E8734A] text-white rounded-full font-medium hover:bg-[#d65f3a] transition disabled:opacity-50">
          {{ loading ? '提交中...' : `提交订单 ¥${cartStore.totalPrice.toFixed(2)}` }}
        </button>
      </form>
    </div>
  </div>
</template>
