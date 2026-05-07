<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import productsData from '../data/products.json'

const route = useRoute()
const quantity = ref(1)
const selectedImageIndex = ref(0)

const product = computed(() =>
  productsData.find(item => String(item.id) === String(route.params.id))
)

const galleryImages = computed(() => {
  if (!product.value) return []

  const images = product.value.images?.length ? product.value.images : [product.value.image]
  const labels = ['整体', '局部', '材质', '氛围']
  const objectClasses = ['object-center scale-100', 'object-left-top scale-125', 'object-right scale-125', 'object-bottom scale-125']

  if (images.length === 1) {
    return labels.map((label, index) => ({
      src: images[0],
      label,
      objectClass: objectClasses[index],
    }))
  }

  return images.map((src, index) => ({
    src,
    label: labels[index] || `图片 ${index + 1}`,
    objectClass: 'object-center scale-100',
  }))
})

const selectedGalleryImage = computed(() => {
  return galleryImages.value[selectedImageIndex.value] || galleryImages.value[0]
})

const relatedProducts = computed(() => {
  if (!product.value) return []
  return productsData
    .filter(item => item.category === product.value.category && item.id !== product.value.id)
    .slice(0, 3)
})

watch(
  () => route.params.id,
  () => {
    quantity.value = 1
    selectedImageIndex.value = 0
  }
)

function productIcon(item) {
  const imageName = item.image.split('/').pop().replace(/\.(jpg|png|webp)$/, '')

  return imageName === 'coffee' ? '☕' :
    imageName === 'matcha' ? '🍵' :
    imageName === 'mousse' ? '🥭' :
    imageName === 'cookies' ? '🍪' :
    imageName === 'lemon-tea' ? '🍋' :
    imageName === 'cake' ? '🎂' :
    imageName === 'tote' ? '👜' :
    '🕯️'
}

function addToCart() {
  // TODO: Pinia store integration
}
</script>

<template>
  <div class="min-h-screen pt-14 sm:pt-20 bg-[#fffaf2]">
    <div v-if="product" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-14">
      <div class="mb-5 sm:mb-8">
        <router-link to="/shop" class="inline-flex items-center gap-2 text-sm font-medium text-[#8a7463] hover:text-primary transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          返回手作商店
        </router-link>
      </div>

      <div class="grid lg:grid-cols-[1.04fr_0.96fr] gap-8 sm:gap-10 lg:gap-16 items-start">
        <section>
          <div class="relative aspect-square rounded-[2rem] sm:rounded-[2.5rem] overflow-hidden bg-[radial-gradient(circle_at_32%_18%,#fff8ed_0,#fff8ed00_36%),linear-gradient(135deg,#f8e7d4_0%,#fff5e7_54%,#eccfb6_100%)] border border-[#efd9c6] shadow-xl sm:shadow-2xl shadow-[#d8a06d]/16">
            <div class="absolute inset-4 sm:inset-6 rounded-[1.5rem] sm:rounded-[2rem] border border-white/70 z-10 pointer-events-none"></div>
            <div class="absolute top-4 sm:top-6 left-4 sm:left-6 z-20 flex flex-wrap gap-2">
              <span v-for="tag in product.tags" :key="tag" class="px-3 py-1.5 rounded-full bg-[#c9552d] border border-white/70 text-xs font-bold text-white shadow-[0_6px_18px_rgba(63,44,31,0.22)]">
                {{ tag }}
              </span>
            </div>
            <div class="absolute inset-0 overflow-hidden">
              <img
                :src="selectedGalleryImage.src"
                :alt="`${product.name}${selectedGalleryImage.label}`"
                class="w-full h-full object-cover transition-transform duration-500"
                :class="selectedGalleryImage.objectClass"
              >
              <div class="absolute inset-0 bg-gradient-to-t from-[#3f2c1f]/10 via-transparent to-white/10"></div>
            </div>
          </div>

          <div class="grid grid-cols-4 gap-2.5 sm:gap-4 mt-4 sm:mt-5">
            <button v-for="(image, index) in galleryImages" :key="`${image.src}-${index}`" @click="selectedImageIndex = index"
              class="relative aspect-square rounded-[1.2rem] sm:rounded-[1.5rem] bg-white border overflow-hidden transition-all duration-300 hover:-translate-y-1 hover:shadow-lg hover:shadow-[#d8a06d]/16"
              :class="selectedImageIndex === index ? 'border-primary shadow-lg shadow-primary/18' : 'border-[#efd9c6] shadow-sm shadow-[#d8a06d]/8'">
              <img :src="image.src" :alt="`${product.name}${image.label}`" class="w-full h-full object-cover" :class="image.objectClass">
              <span class="absolute left-1.5 sm:left-2 bottom-1.5 sm:bottom-2 px-1.5 sm:px-2 py-0.5 sm:py-1 rounded-full bg-white/86 text-[10px] sm:text-[11px] font-medium text-[#7f6655]">{{ image.label }}</span>
            </button>
          </div>
        </section>

        <section class="lg:pt-4">
          <div class="flex flex-wrap items-center gap-2 mb-4 sm:mb-5">
            <span class="px-3.5 sm:px-4 py-2 rounded-full bg-[#fff1df] border border-[#efd9c6] text-xs sm:text-sm font-medium text-primary">{{ product.category }}</span>
            <span class="px-3.5 sm:px-4 py-2 rounded-full bg-white/78 border border-[#efd9c6] text-xs sm:text-sm text-[#8a7463]">小批量手作</span>
          </div>

          <h1 class="text-3xl sm:text-5xl font-bold text-[#3f3329] leading-tight mb-2 sm:mb-3">{{ product.name }}</h1>
          <p class="text-sm sm:text-base text-[#9b8270] mb-6">{{ product.nameEn }}</p>

          <div class="flex flex-wrap items-center gap-2 mb-5 sm:mb-7">
            <div class="flex items-center gap-1">
              <svg v-for="i in 5" :key="i" class="w-4 h-4" :class="i <= Math.floor(product.rating) ? 'text-[#efad4d]' : 'text-[#eadbca]'" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
            </div>
            <span class="text-sm text-[#8d7666]">{{ product.rating }} 分 · {{ product.soldCount }} 人带走过</span>
          </div>

          <p class="text-[#756252] leading-7 sm:leading-8 mb-6 sm:mb-8 text-base sm:text-lg">{{ product.description }}</p>

          <div class="rounded-[1.75rem] sm:rounded-[2rem] bg-white/76 border border-[#efd9c6] shadow-sm shadow-[#d8a06d]/10 p-4 sm:p-6 mb-6 sm:mb-8">
            <div class="flex items-end gap-3 mb-5">
              <span class="text-4xl font-bold text-primary">&yen;{{ product.price }}</span>
              <span v-if="product.originalPrice" class="text-lg text-[#a99584] line-through mb-1">&yen;{{ product.originalPrice }}</span>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-3 text-sm text-[#7f6655]">
              <div class="rounded-2xl bg-[#fff8ef] border border-[#f0ddca] p-4">
                <div class="font-semibold text-[#4a372b] mb-1">手作温度</div>
                <div>小批量制作，保持新鲜</div>
              </div>
              <div class="rounded-2xl bg-[#fff8ef] border border-[#f0ddca] p-4">
                <div class="font-semibold text-[#4a372b] mb-1">包装心意</div>
                <div>奶油色纸袋与祝福卡</div>
              </div>
              <div class="rounded-2xl bg-[#fff8ef] border border-[#f0ddca] p-4">
                <div class="font-semibold text-[#4a372b] mb-1">适合场景</div>
                <div>自留、下午茶、送礼</div>
              </div>
            </div>
          </div>

          <div class="flex flex-col sm:flex-row gap-3 sm:gap-4">
            <div class="inline-flex items-center justify-between w-full sm:w-36 rounded-full bg-white border border-[#efd9c6] overflow-hidden">
              <button @click="quantity = Math.max(1, quantity - 1)" class="w-12 sm:w-11 h-12 flex items-center justify-center text-[#8a7463] hover:text-primary hover:bg-[#fff1df] transition-colors">-</button>
              <span class="font-semibold text-[#4a372b]">{{ quantity }}</span>
              <button @click="quantity += 1" class="w-12 sm:w-11 h-12 flex items-center justify-center text-[#8a7463] hover:text-primary hover:bg-[#fff1df] transition-colors">+</button>
            </div>
            <button @click="addToCart" class="flex-1 px-8 py-3.5 bg-primary text-white rounded-full font-medium shadow-lg shadow-primary/25 hover:bg-primary-dark hover:shadow-xl hover:shadow-primary/30 hover:-translate-y-1 transition-all duration-300">
              加入购物车
            </button>
            <router-link to="/cart" class="flex-1 text-center px-8 py-3.5 bg-white border border-[#edcfb6] text-[#6d5542] rounded-full font-medium hover:text-primary hover:border-primary/50 hover:shadow-lg hover:shadow-primary/15 hover:-translate-y-1 transition-all duration-300">
              去结算
            </router-link>
          </div>
        </section>
      </div>

      <section v-if="relatedProducts.length" class="mt-12 sm:mt-20">
        <div class="flex items-end justify-between gap-4 mb-5 sm:mb-6">
          <div>
            <span class="text-sm font-medium text-primary">同类推荐</span>
            <h2 class="text-2xl sm:text-3xl font-bold text-[#3f3329] mt-2">也许你还会喜欢</h2>
          </div>
          <router-link to="/shop" class="text-sm font-medium text-[#8a7463] hover:text-primary transition-colors">查看全部</router-link>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
          <router-link v-for="item in relatedProducts" :key="item.id" :to="`/products/${item.id}`" class="group rounded-[1.6rem] sm:rounded-[2rem] bg-white/78 border border-[#efd9c6] p-4 sm:p-5 shadow-sm shadow-[#d8a06d]/10 hover:-translate-y-1 hover:shadow-xl hover:shadow-[#d8a06d]/18 transition-all duration-300">
            <div class="aspect-[1.5] sm:aspect-[1.3] rounded-[1.25rem] sm:rounded-[1.5rem] bg-[#fff1df] border border-[#f0ddca] mb-4 overflow-hidden group-hover:scale-[1.02] transition-transform">
              <img :src="item.image" :alt="item.name" class="w-full h-full object-cover">
            </div>
            <div class="flex items-center justify-between gap-3">
              <div>
                <h3 class="font-semibold text-[#46362b] group-hover:text-primary transition-colors">{{ item.name }}</h3>
                <p class="text-sm text-[#9b8270] mt-1">{{ item.category }}</p>
              </div>
              <span class="font-bold text-primary">&yen;{{ item.price }}</span>
            </div>
          </router-link>
        </div>
      </section>
    </div>

    <div v-else class="max-w-3xl mx-auto px-5 sm:px-6 lg:px-8 py-24 text-center">
      <div class="text-7xl mb-6">🧺</div>
      <h1 class="text-3xl font-bold text-[#3f3329] mb-3">没有找到这件手作好物</h1>
      <p class="text-[#756252] mb-8">它可能已经下架，或者链接地址不正确。</p>
      <router-link to="/shop" class="inline-block px-8 py-3.5 bg-primary text-white rounded-full font-medium shadow-lg shadow-primary/25 hover:bg-primary-dark hover:-translate-y-1 transition-all duration-300">
        返回商店
      </router-link>
    </div>
  </div>
</template>
