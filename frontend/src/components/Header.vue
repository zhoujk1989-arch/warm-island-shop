<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { useCartStore } from '../stores/cart.js'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const cartCount = computed(() => cartStore.itemCount)
const mobileMenuOpen = ref(false)

function navigateTo(path) {
  router.push(path)
  mobileMenuOpen.value = false
}

function handleLogout() {
  authStore.logout()
  mobileMenuOpen.value = false
}
</script>

<template>
  <header class="fixed top-0 left-0 right-0 z-50 bg-white/88 backdrop-blur-md border-b border-[#efd9c6]">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-14 sm:h-16">
        <!-- Logo -->
        <div class="flex items-center gap-2 cursor-pointer" @click="navigateTo('/')">
          <div class="w-8 h-8 rounded-full bg-primary flex items-center justify-center shadow-sm shadow-primary/20">
            <span class="text-white text-sm font-bold">暖</span>
          </div>
          <span class="text-lg font-semibold text-text-main">暖屿</span>
        </div>

        <!-- Desktop Nav -->
        <nav class="hidden md:flex items-center gap-8">
          <router-link to="/" class="nav-link text-text-main hover:text-primary transition-colors">首页</router-link>
          <router-link to="/shop" class="nav-link text-text-subtle hover:text-primary transition-colors">商品</router-link>
          <router-link to="/about" class="nav-link text-text-subtle hover:text-primary transition-colors">关于</router-link>
        </nav>

        <!-- Auth & Cart -->
        <div class="flex items-center gap-4">
          <!-- Login / User -->
          <div v-if="authStore.isLoggedIn" class="hidden sm:flex items-center gap-2">
            <span class="text-sm text-text-subtle">{{ authStore.username }}</span>
            <button @click="handleLogout" class="text-sm text-text-subtle hover:text-primary transition-colors">登出</button>
          </div>
          <router-link v-else to="/login" class="hidden sm:block text-sm text-text-subtle hover:text-primary transition-colors">登录</router-link>

          <!-- Cart -->
          <button @click="navigateTo('/cart')" class="relative p-2 hover:bg-[#fff1df] rounded-full transition-colors">
            <svg class="w-5 h-5 text-text-main" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 100 4 2 2 0 000-4z" />
            </svg>
            <span v-if="cartCount > 0" class="absolute -top-1 -right-1 w-5 h-5 bg-primary text-white text-xs rounded-full flex items-center justify-center">
              {{ cartCount }}
            </span>
          </button>

          <!-- Mobile menu button -->
          <button @click="mobileMenuOpen = !mobileMenuOpen" class="md:hidden p-2 hover:bg-[#fff1df] rounded-full">
            <svg v-if="!mobileMenuOpen" class="w-5 h-5 text-text-main" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
            <svg v-else class="w-5 h-5 text-text-main" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Mobile Nav -->
      <transition name="slide">
        <nav v-if="mobileMenuOpen" class="md:hidden py-3 border-t border-[#efd9c6]">
          <router-link to="/" @click="mobileMenuOpen = false" class="block rounded-2xl px-4 py-3 text-text-subtle hover:text-primary hover:bg-[#fff1df]">首页</router-link>
          <router-link to="/shop" @click="mobileMenuOpen = false" class="block rounded-2xl px-4 py-3 text-text-subtle hover:text-primary hover:bg-[#fff1df]">商品</router-link>
          <router-link to="/about" @click="mobileMenuOpen = false" class="block rounded-2xl px-4 py-3 text-text-subtle hover:text-primary hover:bg-[#fff1df]">关于</router-link>
          <router-link v-if="!authStore.isLoggedIn" to="/login" @click="mobileMenuOpen = false" class="block rounded-2xl px-4 py-3 text-text-subtle hover:text-primary hover:bg-[#fff1df]">登录</router-link>
          <button v-else @click="handleLogout" class="block w-full text-left rounded-2xl px-4 py-3 text-text-subtle hover:text-primary hover:bg-[#fff1df]">登出</button>
        </nav>
      </transition>
    </div>
  </header>
</template>

<style scoped>
.nav-link.router-link-active {
  color: #e8734a;
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.2s ease;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  max-height: 0;
}

.slide-enter-to,
.slide-leave-from {
  opacity: 1;
  max-height: 200px;
}
</style>
