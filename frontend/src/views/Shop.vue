<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import ProductCard from '../components/ProductCard.vue'
import { fetchVisibleProducts } from '../api/products'

const products = ref([])
const loading = ref(false)
const loadError = ref('')
const selectedCategory = ref('全部')
const searchQuery = ref('')
let searchTimer = null

const categories = computed(() => {
  const cats = ['全部', ...new Set(products.value.map(p => p.category))]
  return cats
})

async function loadProducts() {
  loading.value = true
  loadError.value = ''

  try {
    const page = await fetchVisibleProducts({
      pageSize: 200,
      category: selectedCategory.value !== '全部' ? selectedCategory.value : null,
      keyword: searchQuery.value.trim() || null,
    })
    products.value = page.records
  } catch (error) {
    loadError.value = error.message
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    loadProducts()
  }, 400)
}

function handleCategoryChange() {
  loadProducts()
}

onMounted(loadProducts)
</script>

<template>
  <div class="min-h-screen pt-14 sm:pt-20 bg-bg-warm">
    <!-- Header -->
    <div class="bg-white border-b border-border-light">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
        <h1 class="text-2xl sm:text-3xl font-bold text-text-main mb-2">暖屿商店</h1>
        <p class="text-sm sm:text-base text-text-subtle mb-5 sm:mb-6">探索暖屿风味的精选好物</p>

        <!-- Search -->
        <div class="relative max-w-md">
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-subtle/50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input v-model="searchQuery" @input="handleSearch" type="text" placeholder="搜索商品..."
            class="w-full pl-10 pr-4 py-3 sm:py-2.5 rounded-2xl sm:rounded-xl border border-border-light bg-bg-warm focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/10 transition-all" />
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
      <!-- Category filter -->
      <div class="flex gap-2 mb-6 sm:mb-8 overflow-x-auto pb-2 -mx-4 px-4 sm:mx-0 sm:px-0 sm:flex-wrap">
        <button v-for="cat in categories" :key="cat" @click="selectedCategory = cat; handleCategoryChange()"
          class="shrink-0 px-5 py-2.5 sm:py-2 rounded-full text-sm font-medium transition-all duration-300"
          :class="selectedCategory === cat
            ? 'bg-primary text-white shadow-md shadow-primary/25'
            : 'bg-white text-text-subtle border border-border-light hover:border-primary hover:text-primary'">
          {{ cat }}
        </button>
      </div>

      <div v-if="loading" class="text-center py-20 text-text-subtle">
        正在读取商品数据...
      </div>

      <div v-else-if="loadError" class="text-center py-20">
        <h3 class="text-xl font-semibold text-text-main mb-2">商品加载失败</h3>
        <p class="text-text-subtle mb-6">{{ loadError }}</p>
        <button @click="loadProducts" class="px-6 py-3 bg-primary text-white rounded-full hover:bg-primary-dark transition-colors">
          重新加载
        </button>
      </div>

      <!-- Product grid -->
      <div v-else-if="products.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
        <ProductCard v-for="product in products" :key="product.id" :product="product" />
      </div>

      <!-- Empty state -->
      <div v-else class="text-center py-20">
        <div class="text-6xl mb-4">🔍</div>
        <h3 class="text-xl font-semibold text-text-main mb-2">没有找到商品</h3>
        <p class="text-text-subtle">试试其他关键词或分类吧</p>
      </div>
    </div>
  </div>
</template>
