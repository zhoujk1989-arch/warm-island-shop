<script setup>
import { useRouter } from 'vue-router'

defineProps({
  product: { type: Object, required: true },
})

const router = useRouter()

function openDetail(product) {
  router.push(`/products/${product.id}`)
}

function addToCart(product) {
  // TODO: Pinia store integration
}

function productIcon(product) {
  const imageName = product.image.split('/').pop().replace(/\.(jpg|png|webp)$/, '')

  return imageName === 'coffee' ? '☕' :
    imageName === 'matcha' ? '🍵' :
    imageName === 'mousse' ? '🥭' :
    imageName === 'cookies' ? '🍪' :
    imageName === 'lemon-tea' ? '🍋' :
    imageName === 'cake' ? '🎂' :
    imageName === 'tote' ? '👜' :
    '🕯️'
}
</script>

<template>
  <div
    class="group bg-[#fffdf8] rounded-[1.6rem] sm:rounded-[2rem] overflow-hidden border border-[#efd9c6] shadow-sm shadow-[#d8a06d]/10 hover:shadow-xl hover:shadow-[#d8a06d]/20 transition-all duration-300 hover:-translate-y-1.5 cursor-pointer"
    role="link"
    tabindex="0"
    @click="openDetail(product)"
    @keydown.enter="openDetail(product)"
    @keydown.space.prevent="openDetail(product)"
  >
    <!-- Image -->
    <div class="relative aspect-square bg-[radial-gradient(circle_at_35%_25%,#fff8ed_0,#fff8ed00_38%),linear-gradient(135deg,#f9ead8_0%,#fff5e7_55%,#f0d6bd_100%)] overflow-hidden">
      <img :src="product.image" :alt="product.name" class="absolute inset-0 w-full h-full object-cover group-hover:scale-105 transition-transform duration-500">
      <div class="absolute inset-0 bg-gradient-to-t from-[#3f2c1f]/8 via-transparent to-white/10"></div>
      <div class="absolute inset-4 sm:inset-5 rounded-[1.25rem] sm:rounded-[1.6rem] border border-white/55 pointer-events-none"></div>
      <div class="hidden absolute inset-0 items-center justify-center text-6xl">
        {{ productIcon(product) }}
      </div>

      <!-- Tags -->
      <div class="absolute top-2.5 sm:top-3 left-2.5 sm:left-3 z-10 flex flex-wrap gap-1.5 max-w-[calc(100%-1.25rem)] sm:max-w-[calc(100%-1.5rem)]">
        <span v-for="tag in product.tags" :key="tag"
          class="px-2.5 sm:px-3 py-1 sm:py-1.5 text-[11px] sm:text-xs font-bold rounded-full backdrop-blur-md border shadow-[0_6px_18px_rgba(63,44,31,0.22)]"
          :class="tag === '热销' || tag === '招牌' ? 'bg-[#c9552d] text-white border-white/70' :
                  tag === '新品' ? 'bg-[#d9783f] text-white border-white/70' :
                  tag === '礼盒' ? 'bg-[#8f5f3f] text-white border-white/70' :
                  'bg-white/94 text-[#5f4939] border-white/80'">
          {{ tag }}
        </span>
      </div>

      <!-- Quick add button -->
      <button @click.stop="addToCart(product)"
        class="absolute bottom-3 sm:bottom-4 right-3 sm:right-4 w-10 sm:w-11 h-10 sm:h-11 bg-primary text-white rounded-full shadow-lg shadow-primary/25 flex items-center justify-center opacity-100 sm:opacity-0 sm:group-hover:opacity-100 translate-y-0 sm:translate-y-2 sm:group-hover:translate-y-0 transition-all duration-300 hover:bg-primary-dark hover:shadow-xl hover:shadow-primary/30 hover:-translate-y-0.5">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
      </button>
    </div>

    <!-- Info -->
    <div class="p-4 sm:p-5">
      <h3 class="font-semibold text-[#46362b] mb-1 group-hover:text-primary transition-colors">{{ product.name }}</h3>
      <p class="text-xs text-[#9b8270] mb-3 truncate">{{ product.nameEn }}</p>
      <div class="flex items-center gap-1 mb-3">
        <svg v-for="i in 5" :key="i" class="w-3.5 h-3.5" :class="i <= Math.floor(product.rating) ? 'text-[#efad4d]' : 'text-[#eadbca]'" fill="currentColor" viewBox="0 0 20 20">
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
        <span class="text-xs text-[#8d7666] ml-1 truncate">{{ product.rating }} ({{ product.soldCount }}已售)</span>
      </div>
      <div class="flex items-center justify-between">
        <div class="flex items-baseline gap-2">
          <span class="text-xl font-bold text-primary">&yen;{{ product.price }}</span>
          <span v-if="product.originalPrice" class="text-sm text-[#a99584] line-through">&yen;{{ product.originalPrice }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
