<script setup>
const creatorGroups = [
  { title: '手工制作者', desc: '让饰品、布艺、香薰、家居小物拥有被认真看见的展示空间。', image: '/products/candle.jpg' },
  { title: '宝妈创作者', desc: '把碎片时间里的手艺、审美和耐心，转化为可持续的价值。', image: '/products/cookies.jpg' },
  { title: '独立创作者', desc: '获得更稳定的销售渠道，慢慢经营自己的作品和个人品牌。', image: '/products/tote.jpg' },
]

const missionItems = [
  '让手工制作者被看见。',
  '让宝妈的时间和手艺变得有价值。',
  '让独立创作者拥有更稳定的销售渠道。',
  '让每一件有温度的作品，遇见真正喜欢它的人。',
]

const supportItems = [
  { title: '展示', desc: '用更温柔清晰的页面，讲好作品与创作者背后的故事。' },
  { title: '推广', desc: '帮助好作品触达更合适的人，而不是淹没在冷冰冰的货架里。' },
  { title: '销售', desc: '把选购、下单和复购路径做得顺畅，让创作更接近收入。' },
  { title: '运营', desc: '沉淀商品、内容、客户与反馈，让创作者更专注于创作。' },
]
</script>

<template>
  <div class="min-h-screen pt-14 sm:pt-20 bg-[#fffaf2] text-text-main">
    <section class="relative overflow-hidden bg-[radial-gradient(circle_at_18%_24%,#ffe7d2_0,#ffe7d200_34%),linear-gradient(135deg,#fffaf2_0%,#fff3e2_48%,#f4e4d1_100%)]">
      <div class="absolute top-16 right-8 w-72 h-72 rounded-full bg-primary/12 blur-3xl"></div>
      <div class="absolute bottom-0 left-4 w-80 h-80 rounded-full bg-[#d8b894]/20 blur-3xl"></div>

      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14 sm:py-24">
        <div class="grid lg:grid-cols-[0.92fr_1.08fr] gap-9 sm:gap-12 lg:gap-16 items-center">
          <div>
            <span class="inline-flex items-center gap-2 px-3.5 sm:px-4 py-2 bg-white/72 text-primary rounded-full text-xs sm:text-sm font-medium mb-5 sm:mb-6 border border-[#f3d7bf] shadow-sm">
              <span class="w-1.5 h-1.5 rounded-full bg-primary"></span>
              关于暖岛
            </span>
            <h1 class="text-[2.45rem] sm:text-5xl lg:text-6xl font-bold text-[#3f3329] leading-tight mb-5 sm:mb-6">
              让有温度的手艺，<br>
              被更多人看见
            </h1>
            <p class="text-base sm:text-lg text-[#756252] leading-7 sm:leading-8 max-w-2xl">
              暖岛是一个专注于手工商品与治愈系生活方式的平台。我们希望把更多优秀的手工制作者、宝妈、独立创作者聚集在一起，帮助她们展示作品、销售商品、打造个人品牌。
            </p>
          </div>

          <div class="relative">
            <div class="absolute -inset-2 sm:-inset-4 rounded-[2rem] sm:rounded-[3rem] bg-white/50 rotate-2"></div>
            <div class="relative grid grid-cols-2 gap-3 sm:gap-4">
              <div class="space-y-3 sm:space-y-4 pt-6 sm:pt-10">
                <div class="aspect-[0.92] sm:aspect-[0.86] rounded-[1.5rem] sm:rounded-[2rem] overflow-hidden border border-white shadow-xl shadow-[#d8a06d]/18">
                  <img src="/products/tote.jpg" alt="暖岛手作帆布袋" class="w-full h-full object-cover">
                </div>
                <div class="rounded-[1.5rem] sm:rounded-[2rem] bg-white/82 border border-[#efd9c6] p-4 sm:p-5 shadow-sm shadow-[#d8a06d]/10">
                  <p class="text-xs sm:text-sm text-[#7f6655] leading-5 sm:leading-6">每一件作品，都来自真实创作者对生活的热爱与用心。</p>
                </div>
              </div>
              <div class="space-y-3 sm:space-y-4">
                <div class="rounded-[1.5rem] sm:rounded-[2rem] bg-white/82 border border-[#efd9c6] p-4 sm:p-5 shadow-sm shadow-[#d8a06d]/10">
                  <p class="text-xs sm:text-sm font-semibold text-primary mb-2">Warm Handmade Community</p>
                  <p class="text-xs sm:text-sm text-[#7f6655] leading-5 sm:leading-6">连接创作者与消费者的温暖桥梁。</p>
                </div>
                <div class="aspect-[0.98] sm:aspect-[0.92] rounded-[1.5rem] sm:rounded-[2rem] overflow-hidden border border-white shadow-xl shadow-[#d8a06d]/18">
                  <img src="/products/candle.jpg" alt="暖岛手作香薰蜡烛" class="w-full h-full object-cover">
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-14 sm:py-24">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid lg:grid-cols-[0.8fr_1.2fr] gap-8 sm:gap-10 lg:gap-16 items-start">
          <div>
            <span class="text-sm font-medium text-primary">平台初心</span>
            <h2 class="text-2xl sm:text-4xl font-bold text-[#3f3329] mt-3 mb-5 sm:mb-6">不只是卖商品，更是在托举创作者</h2>
            <p class="text-[#756252] leading-7 sm:leading-8">
              在这里，手工饰品、布艺编织、香薰蜡烛、手作甜点、原创周边、家居小物，不只是商品。它们代表着创作者对生活的热爱、审美、耐心和一双愿意认真创造的手。
            </p>
          </div>
          <div class="grid sm:grid-cols-3 gap-4 sm:gap-5">
            <article v-for="group in creatorGroups" :key="group.title" class="group rounded-[1.6rem] sm:rounded-[2rem] bg-white/78 border border-[#efd9c6] p-4 sm:p-5 shadow-sm shadow-[#d8a06d]/10 hover:-translate-y-1 hover:shadow-xl hover:shadow-[#d8a06d]/16 transition-all duration-300">
              <div class="aspect-[1.45] sm:aspect-[1.12] rounded-[1.25rem] sm:rounded-[1.5rem] overflow-hidden mb-4 sm:mb-5 border border-[#f0ddca]">
                <img :src="group.image" :alt="group.title" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500">
              </div>
              <h3 class="text-lg font-semibold text-[#49382d] mb-3">{{ group.title }}</h3>
              <p class="text-sm text-[#7f6655] leading-6">{{ group.desc }}</p>
            </article>
          </div>
        </div>
      </div>
    </section>

    <section class="py-14 sm:py-24 bg-[#f7eadc]">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid lg:grid-cols-2 gap-9 sm:gap-10 lg:gap-16 items-center">
          <div class="relative">
            <div class="absolute -inset-2 sm:-inset-4 rounded-[2rem] sm:rounded-[2.5rem] bg-[#f0d2b7]/30 -rotate-2"></div>
            <div class="relative aspect-[1.05] sm:aspect-[1.08] rounded-[2rem] sm:rounded-[2.5rem] overflow-hidden border border-white shadow-xl shadow-[#d8a06d]/14">
              <img src="/products/cookies.jpg" alt="暖岛手作甜点与礼盒" class="w-full h-full object-cover">
              <div class="absolute inset-0 bg-gradient-to-t from-[#3f2c1f]/38 via-transparent to-white/10"></div>
              <div class="absolute left-4 sm:left-6 right-4 sm:right-6 bottom-4 sm:bottom-6 rounded-[1.4rem] sm:rounded-[1.75rem] bg-white/86 backdrop-blur-sm border border-white/70 p-4 sm:p-5">
                <p class="text-sm sm:text-base text-[#6f5644] leading-6 sm:leading-7">我们帮助创作者解决展示、推广、销售和运营问题，让她们可以更专注于创作。</p>
              </div>
            </div>
          </div>

          <div>
            <span class="text-sm font-medium text-primary">我们提供的支持</span>
            <h2 class="text-2xl sm:text-4xl font-bold text-[#3f3329] mt-3 mb-6 sm:mb-8">让好作品从被喜欢，到被持续购买</h2>
            <div class="grid sm:grid-cols-2 gap-3 sm:gap-4">
              <div v-for="item in supportItems" :key="item.title" class="rounded-[1.5rem] sm:rounded-[1.75rem] bg-white/76 border border-[#efd9c6] p-4 sm:p-5 shadow-sm shadow-[#d8a06d]/10">
                <div class="w-10 h-10 rounded-2xl bg-primary text-white flex items-center justify-center font-semibold mb-4">{{ item.title.slice(0, 1) }}</div>
                <h3 class="font-semibold text-[#49382d] mb-2">{{ item.title }}</h3>
                <p class="text-sm text-[#7f6655] leading-6">{{ item.desc }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-14 sm:py-24 bg-[#fffaf2]">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-8 sm:mb-12">
          <span class="text-sm font-medium text-primary">我们想做的事</span>
          <h2 class="text-2xl sm:text-4xl font-bold text-[#3f3329] mt-3">让每一份用心，都有抵达之处</h2>
        </div>
        <div class="grid sm:grid-cols-2 gap-3 sm:gap-4">
          <div v-for="item in missionItems" :key="item" class="rounded-[1.5rem] sm:rounded-[1.75rem] bg-white/78 border border-[#efd9c6] p-5 sm:p-6 shadow-sm shadow-[#d8a06d]/10 flex gap-4 items-start">
            <span class="mt-1 w-3 h-3 rounded-full bg-primary shrink-0"></span>
            <p class="text-[#5f4939] leading-7 font-medium">{{ item }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="py-14 sm:py-24 bg-[#f4dfc9]">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div class="rounded-[2rem] sm:rounded-[2.5rem] bg-gradient-to-br from-primary to-primary-dark text-white px-5 sm:px-12 py-10 sm:py-16 shadow-2xl shadow-primary/20">
          <p class="text-white/82 mb-4">平台理念</p>
          <h2 class="text-2xl sm:text-4xl font-bold mb-5">用双手创造温度，用平台传递价值</h2>
          <p class="text-white/86 leading-8 mb-8">
            暖岛不仅是一个手工商品平台，更是一个帮助普通创作者成长、销售和被认可的温暖社区。
          </p>
          <router-link to="/shop" class="inline-block px-10 py-4 bg-white text-primary rounded-full font-semibold hover:bg-[#fff7ed] transition-all duration-300 shadow-lg hover:shadow-xl hover:-translate-y-1">
            看看手作好物
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>
