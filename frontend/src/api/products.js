const API_BASE = '/api/products'

function toTagArray(tags) {
  if (Array.isArray(tags)) {
    return tags
  }

  return String(tags || '')
    .split(/[、,，]/)
    .map((tag) => tag.trim())
    .filter(Boolean)
}

export function normalizeProduct(product) {
  return {
    ...product,
    price: Number(product.price) || 0,
    originalPrice: product.originalPrice === null || product.originalPrice === undefined
      ? null
      : Number(product.originalPrice),
    image: product.image || '/products/coffee.jpg',
    images: product.images?.length ? product.images : [product.image || '/products/coffee.jpg'],
    description: product.description || '',
    tags: toTagArray(product.tags),
    rating: Number(product.rating) || 0,
    soldCount: Number(product.soldCount) || 0,
    stock: Number(product.stock) || 0,
    status: product.status || '销售中',
  }
}

async function requestJson(url, options = {}) {
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

export async function fetchProducts({ pageNum = 1, pageSize = 200, category, keyword } = {}) {
  const params = new URLSearchParams({
    pageNum: String(pageNum),
    pageSize: String(pageSize),
  })

  if (category && category !== '全部') {
    params.set('category', category)
  }

  if (keyword?.trim()) {
    params.set('keyword', keyword.trim())
  }

  const page = await requestJson(`${API_BASE}?${params.toString()}`)
  const records = (page.records || []).map(normalizeProduct)

  return {
    ...page,
    records,
  }
}

export async function fetchVisibleProducts(options = {}) {
  const page = await fetchProducts(options)

  return {
    ...page,
    records: page.records.filter((product) => product.status === '销售中'),
  }
}

export async function fetchHotProducts(limit = 4) {
  const records = await requestJson(`${API_BASE}/hot?limit=${Math.max(limit * 3, limit)}`)
  return records
    .map(normalizeProduct)
    .filter((product) => product.status === '销售中')
    .slice(0, limit)
}

export async function fetchProductById(id) {
  return normalizeProduct(await requestJson(`${API_BASE}/${id}`))
}
