<script setup>
import { useCartStore } from '../stores/cart.js'
import { useRouter } from 'vue-router'

const cartStore = useCartStore()
const router = useRouter()

function removeItem(index) {
  cartStore.removeItem(index)
}

function updateQuantity(index, delta) {
  const newQty = cartStore.items[index].quantity + delta
  if (newQty <= 0) {
    cartStore.removeItem(index)
  } else {
    cartStore.updateQuantity(index, newQty)
  }
}

function clearCart() {
  cartStore.clearCart()
}

function goCheckout() {
  if (!cartStore.items.length) return
  router.push('/checkout')
}
</script>

<template>
  <div class="min-h-screen pt-20 bg-bg-warm">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-3xl font-bold text-text-main mb-2">购物车</h1>
      <p class="text-text-subtle mb-8">{{ cartStore.itemCount }} 件商品</p>

      <!-- Empty cart -->
      <div v-if="!cartStore.items.length" class="text-center py-20 bg-white rounded-2xl">
        <div class="text-6xl mb-4">🛒</div>
        <h3 class="text-xl font-semibold text-text-main mb-2">购物车是空的</h3>
        <p class="text-text-subtle mb-6">快去挑选心仪的商品吧</p>
        <router-link to="/shop" class="inline-block px-8 py-3 bg-primary text-white rounded-full hover:bg-primary-dark transition-colors">
          去逛逛
        </router-link>
      </div>

      <!-- Cart items -->
      <div v-else>
        <div class="space-y-4 mb-8">
          <div v-for="(item, index) in cartStore.items" :key="index"
            class="bg-white rounded-2xl p-4 sm:p-6 border border-border-light flex gap-4 sm:gap-6">
            <!-- Product image -->
            <div class="w-20 h-20 sm:w-24 sm:h-24 rounded-xl bg-gradient-to-br from-bg-warm to-orange-50 flex items-center justify-center text-4xl shrink-0">
              {{ item.image ? '' : '🛍️' }}
              <img v-if="item.image" :src="item.image" class="w-full h-full object-cover rounded-xl" />
            </div>

            <!-- Info -->
            <div class="flex-1 min-w-0">
              <h3 class="font-semibold text-text-main">{{ item.name }}</h3>
              <p v-if="item.variantName" class="text-sm text-text-subtle mt-1">{{ item.variantName }}</p>
              <p class="text-sm text-text-subtle mt-1">&yen;{{ item.price }}</p>
              <div class="flex items-center gap-3 mt-3">
                <button @click="updateQuantity(index, -1)" class="w-8 h-8 rounded-full border border-border-light flex items-center justify-center hover:border-primary transition-colors">
                  -
                </button>
                <span class="w-8 text-center font-medium">{{ item.quantity }}</span>
                <button @click="updateQuantity(index, 1)" class="w-8 h-8 rounded-full border border-border-light flex items-center justify-center hover:border-primary transition-colors">
                  +
                </button>
              </div>
            </div>

            <!-- Price & Remove -->
            <div class="flex flex-col items-end justify-between">
              <button @click="removeItem(index)" class="text-text-subtle hover:text-red-500 transition-colors">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
              <span class="text-lg font-bold text-primary">&yen;{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>

        <!-- Summary -->
        <div class="bg-white rounded-2xl p-6 border border-border-light">
          <div class="flex items-center justify-between mb-4">
            <span class="text-text-subtle">商品合计</span>
            <span class="text-2xl font-bold text-primary">&yen;{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
          <p class="text-xs text-text-subtle mb-6">运费将在结算时计算</p>
          <div class="flex gap-3">
            <button @click="clearCart" class="px-6 py-3 border-2 border-border-light text-text-subtle rounded-full hover:border-red-300 hover:text-red-500 transition-colors">
              清空购物车
            </button>
            <button @click="goCheckout" class="flex-1 py-3 bg-primary text-white rounded-full font-medium hover:bg-primary-dark transition-colors shadow-lg shadow-primary/25">
              去结算
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
