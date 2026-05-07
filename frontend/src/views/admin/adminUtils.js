import { ElMessage } from 'element-plus'

export const PRODUCT_API_BASE = '/api/products'
export const CATEGORY_API_BASE = '/api/categories'
export const HOME_API_BASE = '/api/admin/home'

export async function requestJson(url, options = {}) {
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })
  const result = await response.json()

  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '请求失败')
  }

  return result.data
}

export function toTagArray(tags) {
  return Array.isArray(tags)
    ? tags
    : String(tags || '').split(/[、,，]/).map((tag) => tag.trim()).filter(Boolean)
}

export function normalizeDetailEntries(entries = []) {
  return entries
    .map((entry, index) => ({
      sectionType: entry.sectionType || 'detail',
      layoutType: entry.layoutType || 'text',
      title: entry.title || '',
      content: entry.content || '',
      imageUrl: entry.imageUrl || '',
      sortOrder: Number(entry.sortOrder) || index + 1,
      status: entry.status || '启用',
    }))
    .sort((a, b) => a.sortOrder - b.sortOrder)
}

export function normalizeVariants(variants = []) {
  return variants
    .map((variant, index) => ({
      name: variant.name || '',
      skuCode: variant.skuCode || '',
      price: variant.price === null || variant.price === undefined ? null : Number(variant.price),
      originalPrice: variant.originalPrice === null || variant.originalPrice === undefined ? null : Number(variant.originalPrice),
      stock: Number(variant.stock) || 0,
      imageUrl: variant.imageUrl || '',
      sortOrder: Number(variant.sortOrder) || index + 1,
      status: variant.status || '启用',
    }))
    .sort((a, b) => a.sortOrder - b.sortOrder)
}

export function normalizeProduct(product) {
  const tags = toTagArray(product.tags)

  return {
    ...product,
    price: Number(product.price) || 0,
    originalPrice: product.originalPrice === null || product.originalPrice === undefined
      ? null
      : Number(product.originalPrice),
    image: product.image || '/products/coffee.jpg',
    images: product.images?.length ? product.images : [product.image || '/products/coffee.jpg'],
    variants: normalizeVariants(product.variants || []),
    detailEntries: normalizeDetailEntries(product.detailEntries || []),
    description: product.description || '',
    tags,
    rating: Number(product.rating) || 4.8,
    soldCount: Number(product.soldCount) || 0,
    stock: Number(product.stock) || 0,
    status: product.status || '销售中',
    updatedAt: product.updateTime ? product.updateTime.slice(0, 10) : '-',
  }
}

export async function uploadImageFile(file) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await fetch(`${PRODUCT_API_BASE}/upload`, {
    method: 'POST',
    body: formData,
  })
  const result = await response.json()

  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '上传失败')
  }

  return result.data
}

export async function downloadImage(image) {
  try {
    const response = await fetch(image)

    if (!response.ok) {
      throw new Error('图片下载失败')
    }

    const blob = await response.blob()
    const objectUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    const filename = decodeURIComponent(image.split('/').pop() || 'image.jpg')

    link.href = objectUrl
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(objectUrl)
  } catch (error) {
    ElMessage.error(`下载失败：${error.message}`)
  }
}
