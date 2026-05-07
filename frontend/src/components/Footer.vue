<template>
  <footer class="bg-text-main text-white/80">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- Brand -->
        <div>
          <div class="flex items-center gap-2 mb-4">
            <div class="w-8 h-8 rounded-full bg-primary flex items-center justify-center">
              <span class="text-white text-sm font-bold">暖</span>
            </div>
            <span class="text-lg font-semibold text-white">暖岛</span>
          </div>
          <p class="text-sm text-white/60">把海岛的温暖，装进每一个日常。</p>
        </div>

        <!-- Links -->
        <div>
          <h4 class="text-white font-medium mb-4">导航</h4>
          <ul class="space-y-2 text-sm">
            <li><router-link to="/" class="hover:text-primary transition-colors">首页</router-link></li>
            <li><router-link to="/shop" class="hover:text-primary transition-colors">商品</router-link></li>
            <li><router-link to="/about" class="hover:text-primary transition-colors">关于暖岛</router-link></li>
          </ul>
        </div>

        <div>
          <h4 class="text-white font-medium mb-4">分类</h4>
          <ul class="space-y-2 text-sm">
            <li><span class="hover:text-primary transition-colors cursor-pointer">饮品</span></li>
            <li><span class="hover:text-primary transition-colors cursor-pointer">甜点</span></li>
            <li><span class="hover:text-primary transition-colors cursor-pointer">周边</span></li>
          </ul>
        </div>

        <div>
          <h4 class="text-white font-medium mb-4">联系我们</h4>
          <ul class="space-y-2 text-sm text-white/60">
            <li>hello@warmisland.com</li>
            <li>+86 123 4567 8900</li>
            <li class="flex items-center gap-3 mt-4">
              <!-- WeChat -->
              <svg class="w-5 h-5 cursor-pointer hover:text-primary transition-colors" fill="currentColor" viewBox="0 0 24 24">
                <path d="M8.5 13.5a1 1 0 110-2 1 1 0 010 2zm5 0a1 1 0 110-2 1 1 0 010 2zM9.6 3C4.8 3 .5 6.5.5 10.5c0 2.3 1.4 4.3 3.6 5.6L3.5 18l2.5-1.4c.7.2 1.5.3 2.2.3.3 0 .6 0 .9-.1a5.7 5.7 0 01-.4-2c0-3.6 3.5-6.5 7.8-6.5.4 0 .7 0 1.1.1C16.3 4.8 13.2 3 9.6 3zm7.4 7c-3.5 0-6.5 2.3-6.5 5.2s3 5.2 6.5 5.2c.7 0 1.4-.1 2-.3L21 21l-.6-2c1.7-1.1 2.6-2.7 2.6-4.5 0-3.1-3.2-5.5-6-5.5z"/>
              </svg>
              <!-- Weibo -->
              <svg class="w-5 h-5 cursor-pointer hover:text-primary transition-colors" fill="currentColor" viewBox="0 0 24 24">
                <path d="M10.1 18.3c-3.5.4-6.6-1.2-6.8-3.6-.3-2.4 2.4-4.7 5.9-5.1 3.5-.4 6.6 1.2 6.8 3.6.3 2.4-2.4 4.7-5.9 5.1zm6.9-9.3c-.3-.9-1.2-1.4-2.1-1.1l-.1-.1c.6-1.3.1-2.8-1.2-3.4-.3-.1-.5-.3-.6-.6 0-.3.2-.6.4-.7 1.5-.8 2-2.6 1.3-4.1-.1-.2-.1-.5 0-.7.1-.2.3-.3.6-.3 2.4.5 4.1 2.7 4 5.2-.3.6-.5 1.3-.8 1.9l-.4.7c-.2.3-.5.6-.8.7-.1 0-.2 0-.3-.1z"/>
              </svg>
            </li>
          </ul>
        </div>
      </div>

      <div class="border-t border-white/10 mt-8 pt-8 text-center text-sm text-white/40">
        <p>&copy; 2026 暖岛 Warm Island. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>
