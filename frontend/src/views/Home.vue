<script setup>
import { ref } from 'vue'
import ProductCard from '../components/ProductCard.vue'
import productsData from '../data/products.json'

const featuredProducts = ref(productsData.slice(0, 4))

const categories = [
  { name: '手冲与特调', image: '/products/coffee.jpg', desc: '咖啡 · 抹茶 · 果茶', note: '适合给午后留一点空白' },
  { name: '小炉烘焙', image: '/products/cookies.jpg', desc: '慕斯 · 蛋糕 · 曲奇', note: '小批量制作，甜度刚刚好' },
  { name: '温柔周边', image: '/products/candle.jpg', desc: '帆布袋 · 香薰 · 文创', note: '把小店气味带回日常' },
]
</script>

<template>
  <div class="min-h-screen bg-[#fffaf2] text-text-main">
    <section class="relative min-h-[auto] sm:min-h-[92vh] flex items-center overflow-hidden bg-[radial-gradient(circle_at_18%_24%,#ffe7d2_0,#ffe7d200_34%),linear-gradient(135deg,#fffaf2_0%,#fff3e2_48%,#f4e4d1_100%)]">
      <div class="absolute inset-x-0 top-0 h-24 bg-gradient-to-b from-white/70 to-transparent"></div>
      <div class="absolute top-24 right-8 w-64 h-64 bg-[#f3b17f]/20 rounded-full blur-3xl animate-float"></div>
      <div class="absolute bottom-16 left-6 w-80 h-80 bg-[#d8b894]/18 rounded-full blur-3xl animate-float" style="animation-delay: 1.5s;"></div>

      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 sm:pt-28 pb-14 sm:pb-20 relative z-10">
        <div class="grid lg:grid-cols-[1.02fr_0.98fr] gap-12 lg:gap-16 items-center">
          <div class="animate-fade-in-up">
            <span class="inline-flex items-center gap-2 px-3.5 sm:px-4 py-2 bg-white/70 text-[#b9683e] rounded-full text-xs sm:text-sm font-medium mb-5 sm:mb-6 border border-[#f3d7bf] shadow-sm">
              <span class="w-1.5 h-1.5 rounded-full bg-primary"></span>
              暖屿手作 · 治愈系小店
            </span>
            <h1 class="text-[2.55rem] sm:text-5xl lg:text-7xl font-bold text-[#3f3329] leading-[1.1] tracking-normal mb-5 sm:mb-6">
              把慢下来的<br>
              <span class="text-primary">温柔日常</span><br class="hidden sm:block">
              做成礼物
            </h1>
            <p class="text-base sm:text-lg text-[#756252] mb-7 sm:mb-9 max-w-xl leading-7 sm:leading-8">
              我们在一间小小手作室里调配饮品、烘烤甜点、整理香气与布料。每一件暖屿好物，都想陪你把平凡的一天过得更柔软一点。
            </p>
            <div class="grid grid-cols-1 sm:flex sm:flex-wrap gap-3 sm:gap-4">
              <router-link to="/shop" class="text-center px-7 sm:px-8 py-3.5 bg-primary text-white rounded-full font-medium hover:bg-primary-dark transition-all duration-300 shadow-lg shadow-primary/25 hover:shadow-xl hover:shadow-primary/30 hover:-translate-y-1">
                逛逛手作好物
              </router-link>
              <router-link to="/about" class="text-center px-7 sm:px-8 py-3.5 bg-white/75 border border-[#edcfb6] text-[#6d5542] rounded-full font-medium hover:text-primary hover:border-primary/50 hover:shadow-lg hover:shadow-primary/15 hover:-translate-y-1 transition-all duration-300">
                听听小店故事
              </router-link>
            </div>

            <div class="grid grid-cols-3 gap-2.5 sm:gap-5 mt-9 sm:mt-12 max-w-xl">
              <div class="rounded-2xl sm:rounded-3xl bg-white/62 border border-[#f0d8c5] px-3 sm:px-4 py-4 sm:py-5 shadow-sm">
                <div class="text-xl sm:text-3xl font-bold text-[#4a372b]">8+</div>
                <div class="text-[11px] sm:text-sm text-[#8a7463] mt-1">手作选品</div>
              </div>
              <div class="rounded-2xl sm:rounded-3xl bg-white/62 border border-[#f0d8c5] px-3 sm:px-4 py-4 sm:py-5 shadow-sm">
                <div class="text-xl sm:text-3xl font-bold text-[#4a372b]">4.8</div>
                <div class="text-[11px] sm:text-sm text-[#8a7463] mt-1">治愈评分</div>
              </div>
              <div class="rounded-2xl sm:rounded-3xl bg-white/62 border border-[#f0d8c5] px-3 sm:px-4 py-4 sm:py-5 shadow-sm">
                <div class="text-xl sm:text-3xl font-bold text-[#4a372b]">5K+</div>
                <div class="text-[11px] sm:text-sm text-[#8a7463] mt-1">温暖陪伴</div>
              </div>
            </div>
          </div>

          <div class="relative animate-fade-in hidden md:block">
            <div class="w-full aspect-[0.92] max-w-lg mx-auto relative">
              <div class="absolute inset-4 rounded-[3rem] bg-white/70 border border-white shadow-2xl shadow-[#d8a06d]/20 rotate-2"></div>
              <div class="absolute inset-0 rounded-[3rem] bg-[#fff7eb] border border-[#efd4bd] shadow-xl shadow-[#d8a06d]/15 overflow-hidden">
                <div class="absolute inset-x-0 top-0 h-32 bg-gradient-to-b from-white/80 to-transparent"></div>
                <div class="absolute left-10 right-10 top-10 rounded-[2rem] bg-[#f6dfc7] border border-[#e9c7aa] p-8 shadow-inner">
                  <div class="flex items-center justify-between text-[#8b6246] text-sm mb-8">
                    <span>handmade atelier</span>
                    <span>since warm days</span>
                  </div>
                  <div class="relative aspect-[1.24] rounded-[1.7rem] overflow-hidden border border-white/70 shadow-lg shadow-[#9d6b45]/15">
                    <img src="/products/cookies.jpg" alt="暖屿曲奇礼盒" class="w-full h-full object-cover">
                    <div class="absolute inset-0 bg-gradient-to-t from-[#3f2c1f]/18 via-transparent to-white/10"></div>
                  </div>
                  <p class="mt-8 text-center text-[#7f6655] leading-7">
                    今日出炉：黄油曲奇、海盐焦糖蛋糕、棉布小袋与一盏温柔香气。
                  </p>
                </div>
                <div class="absolute bottom-9 left-9 right-9 grid grid-cols-3 gap-3">
                  <div class="h-24 rounded-[1.75rem] bg-white border border-[#f0d8c5] shadow-sm overflow-hidden animate-float">
                    <img src="/products/coffee.jpg" alt="暖屿咖啡" class="w-full h-full object-cover">
                  </div>
                  <div class="h-24 rounded-[1.75rem] bg-white border border-[#f0d8c5] shadow-sm overflow-hidden animate-float" style="animation-delay: 1s;">
                    <img src="/products/cake.jpg" alt="海盐焦糖蛋糕" class="w-full h-full object-cover">
                  </div>
                  <div class="h-24 rounded-[1.75rem] bg-white border border-[#f0d8c5] shadow-sm overflow-hidden animate-float" style="animation-delay: 2s;">
                    <img src="/products/candle.jpg" alt="海岛香薰蜡烛" class="w-full h-full object-cover">
                  </div>
                </div>
              </div>
              <div class="absolute -right-4 top-24 w-20 h-20 bg-white rounded-[1.75rem] border border-[#efd4bd] shadow-xl shadow-[#d8a06d]/15 overflow-hidden animate-float">
                <img src="/products/tote.jpg" alt="暖屿帆布袋" class="w-full h-full object-cover">
              </div>
              <div class="absolute -left-4 bottom-28 w-16 h-16 bg-white rounded-[1.5rem] border border-[#efd4bd] shadow-xl shadow-[#d8a06d]/15 overflow-hidden animate-float" style="animation-delay: 1.2s;">
                <img src="/products/mousse.jpg" alt="椰香芒果慕斯" class="w-full h-full object-cover">
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="hidden sm:block absolute bottom-8 left-1/2 -translate-x-1/2 animate-bounce">
        <svg class="w-6 h-6 text-[#8a7463]/50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
        </svg>
      </div>
    </section>

    <section class="py-14 sm:py-24 bg-[#fffaf2]">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-8 sm:mb-12 animate-fade-in-up">
          <span class="text-sm font-medium text-primary">慢慢挑选</span>
          <h2 class="text-2xl sm:text-4xl font-bold text-[#3f3329] mt-3 mb-4">暖屿今日手作</h2>
          <p class="text-[#7a6758] max-w-md mx-auto leading-7">从饮品香气到烘焙甜点，再到能带回家的小物，每一类都保留一点手作温度。</p>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 sm:gap-6 stagger-children">
          <div v-for="cat in categories" :key="cat.name" class="group cursor-pointer">
            <router-link to="/shop" class="block h-full p-4 sm:p-5 rounded-[1.6rem] sm:rounded-[2rem] bg-white/78 border border-[#efd9c6] shadow-sm shadow-[#d8a06d]/10 transition-all duration-300 hover:-translate-y-2 hover:shadow-xl hover:shadow-[#d8a06d]/18 hover:border-primary/35">
              <div class="aspect-[1.55] sm:aspect-[1.28] rounded-[1.25rem] sm:rounded-[1.6rem] bg-[#fff0de] border border-[#f1d3bb] overflow-hidden mb-4 sm:mb-6">
                <img :src="cat.image" :alt="cat.name" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500">
              </div>
              <h3 class="text-xl font-semibold text-[#49382d] mb-2">{{ cat.name }}</h3>
              <p class="text-sm text-primary mb-4">{{ cat.desc }}</p>
              <p class="text-sm text-[#856f5f] leading-6">{{ cat.note }}</p>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <section class="py-14 sm:py-24 bg-[#f7eadc]">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-5 mb-8 sm:mb-12 animate-fade-in-up">
          <div>
            <span class="text-sm font-medium text-primary">被反复带走的温柔</span>
            <h2 class="text-2xl sm:text-4xl font-bold text-[#3f3329] mt-3 mb-3">人气手作好物</h2>
            <p class="text-[#7a6758]">暖屿最受欢迎的几件小东西，适合自留，也适合送人。</p>
          </div>
          <router-link to="/shop" class="inline-flex items-center justify-center gap-2 self-stretch sm:self-auto px-5 py-3 bg-primary text-white rounded-full font-medium shadow-lg shadow-primary/20 hover:bg-primary-dark hover:shadow-xl hover:shadow-primary/25 hover:-translate-y-1 transition-all duration-300">
            查看全部
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </router-link>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 stagger-children">
          <ProductCard v-for="product in featuredProducts" :key="product.id" :product="product" />
        </div>
      </div>
    </section>

    <section class="py-14 sm:py-24 bg-[#fffaf2]">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid md:grid-cols-2 gap-10 sm:gap-16 items-center">
          <div class="relative animate-slide-left">
            <div class="absolute -inset-2 sm:-inset-4 rounded-[2rem] sm:rounded-[2.5rem] bg-[#f0d2b7]/30 rotate-3"></div>
            <div class="relative aspect-[1.2] sm:aspect-[1.04] rounded-[2rem] sm:rounded-[2.5rem] bg-white border border-[#efd9c6] shadow-xl shadow-[#d8a06d]/12 flex items-center justify-center overflow-hidden">
              <img src="/products/tote.jpg" alt="暖屿手作包装与帆布袋" class="absolute inset-0 w-full h-full object-cover">
              <div class="absolute inset-0 bg-gradient-to-t from-[#3f2c1f]/38 via-[#3f2c1f]/6 to-white/10"></div>
              <div class="relative text-center px-8 mt-auto mb-10">
                <p class="text-white italic leading-7 drop-shadow-sm">小批量制作，慢一点打包，认真写下每一张祝福卡。</p>
              </div>
            </div>
          </div>

          <div class="animate-slide-right">
            <span class="text-sm font-medium text-primary">关于暖屿</span>
            <h2 class="text-2xl sm:text-4xl font-bold text-[#3f3329] mt-3 mb-5 sm:mb-6">一间把温柔做进日常的小店</h2>
            <p class="text-[#756252] mb-4 leading-7 sm:leading-8">
              暖屿手作从一张木桌开始。我们喜欢奶油色的纸袋、刚出炉的黄油香、手写标签和拆开包裹时那一点点被照顾到的心情。
            </p>
            <p class="text-[#756252] mb-7 sm:mb-8 leading-7 sm:leading-8">
              从一杯特调咖啡，到一块手作甜点；从一个帆布袋，到一支香薰蜡烛，每一件商品都用柔和颜色、自然材质和小批量制作，留住慢生活里的安静片刻。
            </p>
            <router-link to="/about" class="inline-flex w-full sm:w-auto items-center justify-center gap-2 px-6 py-3 bg-primary text-white rounded-full font-medium shadow-lg shadow-primary/20 hover:bg-primary-dark hover:shadow-xl hover:shadow-primary/25 hover:-translate-y-1 transition-all duration-300">
              了解我们的故事
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <section class="py-14 sm:py-24 bg-[#f4dfc9]">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div class="rounded-[2rem] sm:rounded-[2.5rem] bg-gradient-to-br from-primary to-primary-dark text-white px-5 sm:px-12 py-10 sm:py-16 shadow-2xl shadow-primary/20">
          <h2 class="text-2xl sm:text-4xl font-bold mb-4">给今天留一份暖意</h2>
          <p class="text-white/86 mb-8 text-base sm:text-lg leading-8">挑一件喜欢的手作好物，让奶油色、木质香和一点点甜，陪你慢慢度过这一天。</p>
          <router-link to="/shop" class="inline-block px-10 py-4 bg-white text-primary rounded-full font-semibold hover:bg-[#fff7ed] transition-all duration-300 shadow-lg hover:shadow-xl hover:-translate-y-1">
            去逛逛
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>
