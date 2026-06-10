import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: ref([]),
  }),
  getters: {
    itemCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    totalPrice: (state) => state.items.reduce((sum, item) => sum + item.price * item.quantity, 0),
  },
  actions: {
    addItem(product, variantId = null, quantity = 1) {
      const existing = this.items.find(
        (item) => item.productId === product.id && item.variantId === (variantId || null)
      )
      if (existing) {
        existing.quantity += quantity
      } else {
        this.items.push({
          productId: product.id,
          name: product.name,
          image: product.image,
          price: product.price,
          variantId,
          variantName: variantId ? this.findVariantName(product, variantId) : null,
          quantity,
        })
      }
    },
    removeItem(index) {
      this.items.splice(index, 1)
    },
    updateQuantity(index, quantity) {
      if (quantity <= 0) {
        this.removeItem(index)
      } else {
        this.items[index].quantity = quantity
      }
    },
    clearCart() {
      this.items = []
    },
    findVariantName(product, variantId) {
      const variant = product.variants?.find((v) => v.id === variantId)
      return variant ? variant.name : null
    },
  },
})
