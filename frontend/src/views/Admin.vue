<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CategoryManager from './admin/CategoryManager.vue'
import HomeManager from './admin/HomeManager.vue'
import {
  Box,
  Delete,
  Download,
  Edit,
  Goods,
  Picture,
  Plus,
  Refresh,
  Search,
  TrendCharts,
} from '@element-plus/icons-vue'

const API_BASE = '/api/products'
const CATEGORY_API_BASE = '/api/categories'
const HOME_API_BASE = '/api/admin/home'

const products = ref([])
const categoryList = ref([])
const loading = ref(false)
const categoryLoading = ref(false)
const homeLoading = ref(false)
const keyword = ref('')
const selectedCategory = ref('全部')
const selectedStatus = ref('全部')
const activeSection = ref('products')
const drawerVisible = ref(false)
const categoryDrawerVisible = ref(false)
const homeSectionDrawerVisible = ref(false)
const homeItemDrawerVisible = ref(false)
const editingId = ref(null)
const editingCategoryId = ref(null)
const editingHomeSectionCode = ref('')
const editingHomeItemId = ref(null)
const homeSections = ref([])
const activeHomeSectionCode = ref('hero')

const productForm = reactive({
  name: '',
  nameEn: '',
  category: '',
  price: 0,
  originalPrice: null,
  stock: 0,
  status: '销售中',
  image: '/products/coffee.jpg',
  rating: 4.8,
  soldCount: 0,
  images: [],
  variants: [],
  detailEntries: [],
  description: '',
  tags: '',
})

const categoryForm = reactive({
  name: '',
  sortOrder: 0,
  status: '启用',
})

const homeSectionForm = reactive({
  code: '',
  eyebrow: '',
  title: '',
  subtitle: '',
  body: '',
  imageUrl: '',
  linkText: '',
  linkUrl: '',
  sortOrder: 0,
  status: '启用',
})

const homeItemForm = reactive({
  sectionCode: '',
  itemType: 'category_card',
  title: '',
  subtitle: '',
  description: '',
  imageUrl: '',
  linkText: '',
  linkUrl: '',
  sortOrder: 0,
  status: '启用',
})

const enabledCategories = computed(() => categoryList.value.filter((item) => item.status === '启用'))
const categoryFilters = computed(() => ['全部', ...categoryList.value.map((item) => item.name)])
const currentHomeSection = computed(() =>
  homeSections.value.find((section) => section.code === activeHomeSectionCode.value) || homeSections.value[0],
)

const filteredProducts = computed(() => {
  const query = keyword.value.trim().toLowerCase()

  return products.value.filter((product) => {
    const matchesCategory = selectedCategory.value === '全部' || product.category === selectedCategory.value
    const matchesStatus = selectedStatus.value === '全部' || product.status === selectedStatus.value
    const matchesKeyword = !query ||
      product.name.toLowerCase().includes(query) ||
      product.nameEn.toLowerCase().includes(query) ||
      product.description.toLowerCase().includes(query)

    return matchesCategory && matchesStatus && matchesKeyword
  })
})

const activeProducts = computed(() => products.value.filter((product) => product.status === '销售中'))
const totalSales = computed(() => products.value.reduce((sum, product) => sum + product.soldCount, 0))
const lowStockCount = computed(() => products.value.filter((product) => product.stock < 35).length)
const totalRevenue = computed(() =>
  products.value.reduce((sum, product) => sum + product.price * product.soldCount, 0),
)

const stats = computed(() => [
  {
    label: '在售商品',
    value: activeProducts.value.length,
    suffix: '件',
    tone: 'primary',
    icon: Goods,
  },
  {
    label: '累计销量',
    value: totalSales.value.toLocaleString('zh-CN'),
    suffix: '单',
    tone: 'green',
    icon: TrendCharts,
  },
  {
    label: '预估 GMV',
    value: `¥${totalRevenue.value.toLocaleString('zh-CN')}`,
    suffix: '',
    tone: 'gold',
    icon: Box,
  },
  {
    label: '低库存预警',
    value: lowStockCount.value,
    suffix: '项',
    tone: 'red',
    icon: Refresh,
  },
])

function resetForm() {
  Object.assign(productForm, {
    name: '',
    nameEn: '',
    category: '',
    price: 0,
    originalPrice: null,
    stock: 0,
    status: '销售中',
    image: '/products/coffee.jpg',
    rating: 4.8,
    soldCount: 0,
    images: [],
    variants: [],
    detailEntries: [],
    description: '',
    tags: '',
  })
}

function normalizeDetailEntries(entries = []) {
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

function normalizeVariants(variants = []) {
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

function normalizeProduct(product) {
  const tags = Array.isArray(product.tags)
    ? product.tags
    : String(product.tags || '').split(/[、,，]/).map((tag) => tag.trim()).filter(Boolean)

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

async function fetchProducts() {
  loading.value = true

  try {
    const page = await requestJson(`${API_BASE}?pageNum=1&pageSize=200`)
    products.value = (page.records || []).map(normalizeProduct)
  } catch (error) {
    ElMessage.error(`读取数据库商品失败：${error.message}`)
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  categoryLoading.value = true

  try {
    categoryList.value = await requestJson(CATEGORY_API_BASE)
  } catch (error) {
    ElMessage.error(`读取分类失败：${error.message}`)
  } finally {
    categoryLoading.value = false
  }
}

async function fetchHomeContent() {
  homeLoading.value = true

  try {
    homeSections.value = await requestJson(HOME_API_BASE)

    if (!homeSections.value.some((section) => section.code === activeHomeSectionCode.value)) {
      activeHomeSectionCode.value = homeSections.value[0]?.code || 'hero'
    }
  } catch (error) {
    ElMessage.error(`读取首页内容失败：${error.message}`)
  } finally {
    homeLoading.value = false
  }
}

function openCreateDrawer() {
  editingId.value = null
  resetForm()
  productForm.category = enabledCategories.value[0]?.name || ''
  addVariant()
  drawerVisible.value = true
}

function openEditDrawer(product) {
  editingId.value = product.id
  Object.assign(productForm, {
    name: product.name,
    nameEn: product.nameEn,
    category: product.category,
    price: product.price,
    originalPrice: product.originalPrice,
    stock: product.stock,
    status: product.status,
    image: product.image,
    rating: product.rating,
    soldCount: product.soldCount,
    images: [...product.images],
    variants: normalizeVariants(product.variants || []),
    detailEntries: normalizeDetailEntries(product.detailEntries || []),
    description: product.description,
    tags: product.tags.join('、'),
  })
  drawerVisible.value = true
}

async function downloadImage(image) {
  try {
    const response = await fetch(image)

    if (!response.ok) {
      throw new Error('图片下载失败')
    }

    const blob = await response.blob()
    const objectUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    const filename = decodeURIComponent(image.split('/').pop() || 'product-image.jpg')

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

async function uploadImageFile(file) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await fetch(`${API_BASE}/upload`, {
    method: 'POST',
    body: formData,
  })
  const result = await response.json()

  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '上传失败')
  }

  return result.data
}

async function uploadHomeSectionImage({ file, onSuccess, onError }) {
  try {
    homeSectionForm.imageUrl = await uploadImageFile(file)
    ElMessage.success('首页图片已上传')
    onSuccess({ code: 200 })
  } catch (error) {
    ElMessage.error(`图片上传失败：${error.message}`)
    onError(error)
  }
}

async function uploadHomeItemImage({ file, onSuccess, onError }) {
  try {
    homeItemForm.imageUrl = await uploadImageFile(file)
    ElMessage.success('首页内容项图片已上传')
    onSuccess({ code: 200 })
  } catch (error) {
    ElMessage.error(`图片上传失败：${error.message}`)
    onError(error)
  }
}

const detailSectionLabels = {
  detail: '图文详情',
  spec: '规格参数',
  service: '服务说明',
  notice: '购买须知',
}

const detailLayoutLabels = {
  text: '纯文本',
  image_text: '图文左右',
  image: '通栏图片',
  highlight: '重点说明',
}

function detailEntriesBySection(sectionType) {
  return productForm.detailEntries
    .filter((entry) => entry.sectionType === sectionType)
    .sort((a, b) => a.sortOrder - b.sortOrder)
}

function addDetailEntry(sectionType) {
  productForm.detailEntries.push({
    sectionType,
    layoutType: sectionType === 'detail' ? 'image_text' : sectionType === 'service' ? 'highlight' : 'text',
    title: '',
    content: '',
    imageUrl: '',
    sortOrder: productForm.detailEntries.length + 1,
    status: '启用',
  })
}

function removeDetailEntry(entry) {
  const index = productForm.detailEntries.indexOf(entry)

  if (index >= 0) {
    productForm.detailEntries.splice(index, 1)
  }
}

function moveDetailEntry(entry, direction) {
  const entries = detailEntriesBySection(entry.sectionType)
  const index = entries.indexOf(entry)
  const next = entries[index + direction]

  if (!next) {
    return
  }

  const currentSort = entry.sortOrder
  entry.sortOrder = next.sortOrder
  next.sortOrder = currentSort
}

async function uploadDetailEntryImage(entry, file) {
  entry.imageUrl = await uploadImageFile(file)
  ElMessage.success('详情图片已上传')
}

function addVariant() {
  productForm.variants.push({
    name: '',
    skuCode: '',
    price: Number(productForm.price) || 0,
    originalPrice: productForm.originalPrice ? Number(productForm.originalPrice) : null,
    stock: Number(productForm.stock) || 0,
    imageUrl: '',
    sortOrder: productForm.variants.length + 1,
    status: '启用',
  })
}

function removeVariant(variant) {
  const index = productForm.variants.indexOf(variant)

  if (index >= 0) {
    productForm.variants.splice(index, 1)
  }
}

function moveVariant(index, direction) {
  const nextIndex = index + direction

  if (nextIndex < 0 || nextIndex >= productForm.variants.length) {
    return
  }

  const [variant] = productForm.variants.splice(index, 1)
  productForm.variants.splice(nextIndex, 0, variant)
  productForm.variants.forEach((item, itemIndex) => {
    item.sortOrder = itemIndex + 1
  })
}

async function uploadVariantImage(variant, file) {
  variant.imageUrl = await uploadImageFile(file)
  ElMessage.success('款式图片已上传')
}

async function saveProduct() {
  if (!productForm.name.trim() || !productForm.category.trim()) {
    ElMessage.warning('请填写商品名称和分类')
    return
  }

  const variants = normalizeVariants(productForm.variants)
  const images = variants.map((variant) => variant.imageUrl).filter(Boolean)

  if (images.length === 0) {
    ElMessage.warning('请至少维护一个带图片的商品款式')
    return
  }

  const payload = {
    name: productForm.name.trim(),
    nameEn: productForm.nameEn.trim(),
    category: productForm.category.trim(),
    price: Number(productForm.price) || 0,
    originalPrice: productForm.originalPrice ? Number(productForm.originalPrice) : null,
    image: images[0],
    images,
    variants,
    detailEntries: normalizeDetailEntries(productForm.detailEntries),
    rating: Number(productForm.rating) || 4.8,
    soldCount: Number(productForm.soldCount) || 0,
    stock: Number(productForm.stock) || 0,
    status: productForm.status,
    description: productForm.description.trim(),
    tags: productForm.tags.split(/[、,，]/).map((tag) => tag.trim()).filter(Boolean).join(','),
  }

  try {
    if (editingId.value) {
      await requestJson(`${API_BASE}/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload),
      })
      ElMessage.success('商品已更新到数据库')
    } else {
      await requestJson(API_BASE, {
        method: 'POST',
        body: JSON.stringify(payload),
      })
      ElMessage.success('商品已新增到数据库')
    }

    drawerVisible.value = false
    await fetchProducts()
  } catch (error) {
    ElMessage.error(`保存失败：${error.message}`)
  }
}

function resetCategoryForm() {
  Object.assign(categoryForm, {
    name: '',
    sortOrder: categoryList.value.length + 1,
    status: '启用',
  })
}

function openCreateCategoryDrawer() {
  editingCategoryId.value = null
  resetCategoryForm()
  categoryDrawerVisible.value = true
}

function openEditCategoryDrawer(category) {
  editingCategoryId.value = category.id
  Object.assign(categoryForm, {
    name: category.name,
    sortOrder: category.sortOrder,
    status: category.status,
  })
  categoryDrawerVisible.value = true
}

async function saveCategory() {
  if (!categoryForm.name.trim()) {
    ElMessage.warning('请填写分类名称')
    return
  }

  const payload = {
    name: categoryForm.name.trim(),
    sortOrder: Number(categoryForm.sortOrder) || 0,
    status: categoryForm.status,
  }

  try {
    if (editingCategoryId.value) {
      await requestJson(`${CATEGORY_API_BASE}/${editingCategoryId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload),
      })
      ElMessage.success('分类已更新')
    } else {
      await requestJson(CATEGORY_API_BASE, {
        method: 'POST',
        body: JSON.stringify(payload),
      })
      ElMessage.success('分类已新增')
    }

    categoryDrawerVisible.value = false
    await Promise.all([fetchCategories(), fetchProducts()])
  } catch (error) {
    ElMessage.error(`保存分类失败：${error.message}`)
  }
}

async function toggleCategoryStatus(category) {
  const nextStatus = category.status === '启用' ? '停用' : '启用'

  try {
    await requestJson(`${CATEGORY_API_BASE}/${category.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...category,
        status: nextStatus,
      }),
    })
    ElMessage.success(`分类已${nextStatus}`)
    await fetchCategories()
  } catch (error) {
    ElMessage.error(`状态更新失败：${error.message}`)
  }
}

async function removeCategory(category) {
  try {
    await ElMessageBox.confirm(`确认删除分类「${category.name}」吗？`, '删除分类', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await requestJson(`${CATEGORY_API_BASE}/${category.id}`, { method: 'DELETE' })
    ElMessage.success('分类已删除')
    await fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`删除分类失败：${error.message}`)
    }
  }
}

function resetHomeSectionForm(section) {
  Object.assign(homeSectionForm, {
    code: section?.code || '',
    eyebrow: section?.eyebrow || '',
    title: section?.title || '',
    subtitle: section?.subtitle || '',
    body: section?.body || '',
    imageUrl: section?.imageUrl || '',
    linkText: section?.linkText || '',
    linkUrl: section?.linkUrl || '',
    sortOrder: Number(section?.sortOrder) || 0,
    status: section?.status || '启用',
  })
}

function openEditHomeSection(section) {
  editingHomeSectionCode.value = section.code
  resetHomeSectionForm(section)
  homeSectionDrawerVisible.value = true
}

async function saveHomeSection() {
  if (!editingHomeSectionCode.value) {
    return
  }

  try {
    await requestJson(`${HOME_API_BASE}/sections/${editingHomeSectionCode.value}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...homeSectionForm,
        sortOrder: Number(homeSectionForm.sortOrder) || 0,
      }),
    })
    ElMessage.success('首页内容位已保存')
    homeSectionDrawerVisible.value = false
    await fetchHomeContent()
  } catch (error) {
    ElMessage.error(`保存首页内容位失败：${error.message}`)
  }
}

async function toggleHomeSectionStatus(section) {
  try {
    await requestJson(`${HOME_API_BASE}/sections/${section.code}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...section,
        status: section.status === '启用' ? '停用' : '启用',
      }),
    })
    ElMessage.success('首页内容位状态已更新')
    await fetchHomeContent()
  } catch (error) {
    ElMessage.error(`状态更新失败：${error.message}`)
  }
}

function resetHomeItemForm(sectionCode) {
  Object.assign(homeItemForm, {
    sectionCode,
    itemType: sectionCode === 'category_cards' ? 'category_card' : 'button',
    title: '',
    subtitle: '',
    description: '',
    imageUrl: '',
    linkText: '',
    linkUrl: '',
    sortOrder: (currentHomeSection.value?.items?.length || 0) + 1,
    status: '启用',
  })
}

function openCreateHomeItem(sectionCode) {
  editingHomeItemId.value = null
  resetHomeItemForm(sectionCode)
  homeItemDrawerVisible.value = true
}

function openEditHomeItem(item) {
  editingHomeItemId.value = item.id
  Object.assign(homeItemForm, {
    sectionCode: item.sectionCode,
    itemType: item.itemType || 'button',
    title: item.title || '',
    subtitle: item.subtitle || '',
    description: item.description || '',
    imageUrl: item.imageUrl || '',
    linkText: item.linkText || '',
    linkUrl: item.linkUrl || '',
    sortOrder: Number(item.sortOrder) || 0,
    status: item.status || '启用',
  })
  homeItemDrawerVisible.value = true
}

async function saveHomeItem() {
  if (!homeItemForm.title.trim()) {
    ElMessage.warning('请填写内容项标题')
    return
  }

  const payload = {
    ...homeItemForm,
    sortOrder: Number(homeItemForm.sortOrder) || 0,
  }

  try {
    if (editingHomeItemId.value) {
      await requestJson(`${HOME_API_BASE}/items/${editingHomeItemId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload),
      })
      ElMessage.success('首页内容项已更新')
    } else {
      await requestJson(`${HOME_API_BASE}/sections/${homeItemForm.sectionCode}/items`, {
        method: 'POST',
        body: JSON.stringify(payload),
      })
      ElMessage.success('首页内容项已新增')
    }

    homeItemDrawerVisible.value = false
    await fetchHomeContent()
  } catch (error) {
    ElMessage.error(`保存首页内容项失败：${error.message}`)
  }
}

async function toggleHomeItemStatus(item) {
  try {
    await requestJson(`${HOME_API_BASE}/items/${item.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...item,
        status: item.status === '启用' ? '停用' : '启用',
      }),
    })
    ElMessage.success('首页内容项状态已更新')
    await fetchHomeContent()
  } catch (error) {
    ElMessage.error(`状态更新失败：${error.message}`)
  }
}

async function removeHomeItem(item) {
  try {
    await ElMessageBox.confirm(`确认删除首页内容项「${item.title}」吗？`, '删除首页内容项', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await requestJson(`${HOME_API_BASE}/items/${item.id}`, { method: 'DELETE' })
    ElMessage.success('首页内容项已删除')
    await fetchHomeContent()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`删除首页内容项失败：${error.message}`)
    }
  }
}

async function toggleStatus(product) {
  const nextStatus = product.status === '销售中' ? '已下架' : '销售中'

  try {
    await requestJson(`${API_BASE}/${product.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...product,
        tags: product.tags.join(','),
        images: product.images,
        variants: product.variants || [],
        detailEntries: product.detailEntries || [],
        image: product.images[0] || product.image,
        status: nextStatus,
      }),
    })
    ElMessage.success(`已${nextStatus === '销售中' ? '上架' : '下架'} ${product.name}`)
    await fetchProducts()
  } catch (error) {
    ElMessage.error(`状态更新失败：${error.message}`)
  }
}

async function removeProduct(product) {
  try {
    await ElMessageBox.confirm(`确认删除「${product.name}」吗？`, '删除商品', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await requestJson(`${API_BASE}/${product.id}`, { method: 'DELETE' })
    ElMessage.success('商品已从数据库删除')
    await fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`删除失败：${error.message}`)
    }
  }
}

function refreshProducts() {
  fetchProducts()
  fetchCategories()
  fetchHomeContent()
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
  fetchHomeContent()
})
</script>

<template>
  <div class="admin-page min-h-screen bg-[#f6f7fb] pt-14 sm:pt-16">
    <div class="admin-shell">
      <aside class="admin-sidebar">
        <div class="admin-brand">
          <span class="brand-mark">暖</span>
          <div>
            <p>暖屿后台</p>
            <span>Warm Island Admin</span>
          </div>
        </div>

        <nav class="admin-nav">
          <button :class="{ active: activeSection === 'products' }" @click="activeSection = 'products'">
            <el-icon><Goods /></el-icon>
            商品管理
          </button>
          <button :class="{ active: activeSection === 'categories' }" @click="activeSection = 'categories'">
            <el-icon><Box /></el-icon>
            分类维护
          </button>
          <button :class="{ active: activeSection === 'home' }" @click="activeSection = 'home'">
            <el-icon><Picture /></el-icon>
            首页装修
          </button>
          <button>
            <el-icon><TrendCharts /></el-icon>
            库存预警
          </button>
        </nav>
      </aside>

      <main class="admin-content">
        <section class="admin-toolbar">
          <div>
            <p class="section-kicker">商品运营</p>
            <h1>后台管理中心</h1>
          </div>
          <div class="toolbar-actions">
            <el-button :icon="Refresh" @click="refreshProducts">刷新数据库数据</el-button>
            <el-button
              v-if="activeSection === 'products'"
              type="primary"
              :icon="Plus"
              @click="openCreateDrawer"
            >
              新增商品
            </el-button>
          </div>
        </section>

        <section v-if="activeSection === 'products'" class="stats-grid">
          <div v-for="item in stats" :key="item.label" class="stat-card" :class="`stat-${item.tone}`">
            <div class="stat-icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div>
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}<small>{{ item.suffix }}</small></strong>
            </div>
          </div>
        </section>

        <section v-if="activeSection === 'products'" class="panel">
          <div class="panel-header">
            <div>
              <h2>商品列表</h2>
              <p>管理商品资料、价格、库存和上下架状态。</p>
            </div>
            <div class="filters">
              <el-input
                v-model="keyword"
                :prefix-icon="Search"
                clearable
                placeholder="搜索商品"
              />
              <el-select v-model="selectedCategory" placeholder="分类">
                <el-option v-for="category in categoryFilters" :key="category" :label="category" :value="category" />
              </el-select>
              <el-select v-model="selectedStatus" placeholder="状态">
                <el-option label="全部" value="全部" />
                <el-option label="销售中" value="销售中" />
                <el-option label="已下架" value="已下架" />
              </el-select>
            </div>
          </div>

          <div class="table-wrap">
            <el-table v-loading="loading" :data="filteredProducts" row-key="id">
              <el-table-column label="商品" min-width="260">
                <template #default="{ row }">
                  <div class="product-cell">
                    <img :src="row.image" :alt="row.name" />
                    <div>
                      <strong>{{ row.name }}</strong>
                      <span>{{ row.nameEn }}</span>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="category" label="分类" width="110" />
              <el-table-column label="售价" width="110">
                <template #default="{ row }">
                  <strong class="price">¥{{ row.price }}</strong>
                </template>
              </el-table-column>
              <el-table-column label="库存" width="110">
                <template #default="{ row }">
                  <el-tag :type="row.stock < 35 ? 'danger' : 'success'" effect="light">{{ row.stock }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="销量" width="120">
                <template #default="{ row }">
                  {{ row.soldCount.toLocaleString('zh-CN') }}
                </template>
              </el-table-column>
              <el-table-column label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="row.status === '销售中' ? 'success' : 'info'">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="updatedAt" label="更新日期" width="130" />
              <el-table-column label="操作" fixed="right" width="220">
                <template #default="{ row }">
                  <el-button size="small" :icon="Edit" @click="openEditDrawer(row)">编辑</el-button>
                  <el-button size="small" @click="toggleStatus(row)">
                    {{ row.status === '销售中' ? '下架' : '上架' }}
                  </el-button>
                  <el-button size="small" type="danger" :icon="Delete" @click="removeProduct(row)" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </section>

        <CategoryManager v-else-if="activeSection === 'categories'" />

        <HomeManager v-else-if="activeSection === 'home'" />
      </main>
    </div>

    <el-drawer
      v-model="drawerVisible"
      :title="editingId ? '编辑商品' : '新增商品'"
      size="min(920px, 100%)"
    >
      <el-form label-position="top">
        <el-form-item label="商品名称">
          <el-input v-model="productForm.name" placeholder="例如：暖屿咖啡" />
        </el-form-item>
        <el-form-item label="英文名称">
          <el-input v-model="productForm.nameEn" placeholder="Warm Island Coffee" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="分类">
            <el-select v-model="productForm.category" placeholder="选择分类" filterable>
              <el-option
                v-for="category in enabledCategories"
                :key="category.id"
                :label="category.name"
                :value="category.name"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="productForm.status">
              <el-option label="销售中" value="销售中" />
              <el-option label="已下架" value="已下架" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-grid">
          <el-form-item label="售价">
            <el-input-number v-model="productForm.price" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="原价">
            <el-input-number v-model="productForm.originalPrice" :min="0" controls-position="right" />
          </el-form-item>
        </div>
        <el-form-item label="库存">
          <el-input-number v-model="productForm.stock" :min="0" controls-position="right" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="销量">
            <el-input-number v-model="productForm.soldCount" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="评分">
            <el-input-number v-model="productForm.rating" :min="0" :max="5" :step="0.1" controls-position="right" />
          </el-form-item>
        </div>
        <el-form-item label="标签">
          <el-input v-model="productForm.tags" placeholder="多个标签用顿号或逗号分隔" />
        </el-form-item>
        <el-form-item label="列表摘要">
          <el-input v-model="productForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="款式维护">
          <div class="variant-editor">
            <div class="variant-editor-head">
                <div>
                  <strong>同一商品不同款式</strong>
                  <span>维护颜色、规格、套装等款式；商品展示图和详情页图片会按款式图片排序生成。</span>
                </div>
              <el-button size="small" :icon="Plus" @click="addVariant">新增款式</el-button>
            </div>

            <div v-if="productForm.variants.length" class="variant-list">
              <div v-for="(variant, index) in productForm.variants" :key="`${variant.name}-${index}`" class="variant-card">
                <div class="variant-media">
                  <img v-if="variant.imageUrl" :src="variant.imageUrl" alt="款式图片">
                  <span v-else>款式图</span>
                </div>
                <div class="variant-fields">
                  <div class="variant-main-grid">
                    <el-input v-model="variant.name" placeholder="款式名称，例如：奶油白 / 250ml" />
                    <el-input v-model="variant.skuCode" placeholder="款式编码，例如：CUP-WHITE-250" />
                  </div>
                  <div class="variant-price-grid">
                    <label>
                      <span>售价</span>
                      <el-input-number v-model="variant.price" :min="0" controls-position="right" />
                    </label>
                    <label>
                      <span>原价</span>
                      <el-input-number v-model="variant.originalPrice" :min="0" controls-position="right" />
                    </label>
                    <label>
                      <span>库存</span>
                      <el-input-number v-model="variant.stock" :min="0" controls-position="right" />
                    </label>
                    <label>
                      <span>状态</span>
                    <el-select v-model="variant.status" placeholder="状态">
                      <el-option label="启用" value="启用" />
                      <el-option label="停用" value="停用" />
                    </el-select>
                    </label>
                  </div>
                  <div class="variant-actions">
                    <el-upload
                      action=""
                      accept="image/*"
                      :show-file-list="false"
                      :http-request="({ file, onSuccess, onError }) => uploadVariantImage(variant, file).then(() => onSuccess({ code: 200 })).catch(onError)"
                    >
                      <el-button size="small" type="primary">上传款式图</el-button>
                    </el-upload>
                    <el-button size="small" :disabled="index === 0" @click="moveVariant(index, -1)">上移</el-button>
                    <el-button size="small" :disabled="index === productForm.variants.length - 1" @click="moveVariant(index, 1)">下移</el-button>
                    <el-button v-if="variant.imageUrl" size="small" :icon="Download" @click="downloadImage(variant.imageUrl)">下载</el-button>
                    <el-button size="small" type="danger" :icon="Delete" @click="removeVariant(variant)">删除</el-button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="detail-empty">暂未维护款式，前台将按商品默认价格和库存展示。</div>
          </div>
        </el-form-item>
        <el-form-item label="详情内容维护">
          <div class="detail-editor">
            <div
              v-for="sectionType in ['detail', 'spec', 'service', 'notice']"
              :key="sectionType"
              class="detail-section"
            >
              <div class="detail-section-head">
                <div>
                  <strong>{{ detailSectionLabels[sectionType] }}</strong>
                  <span>
                    {{ sectionType === 'detail' ? '独立于列表摘要，用于商品详情页图文排版。' : '在商品详情页对应模块展示。' }}
                  </span>
                </div>
                <el-button size="small" :icon="Plus" @click="addDetailEntry(sectionType)">新增</el-button>
              </div>

              <div v-if="detailEntriesBySection(sectionType).length" class="detail-entry-list">
                <div
                  v-for="entry in detailEntriesBySection(sectionType)"
                  :key="`${entry.sectionType}-${entry.sortOrder}-${entry.title}`"
                  class="detail-entry"
                >
                  <div class="detail-entry-top">
                    <el-select v-model="entry.layoutType" size="small" class="detail-layout-select">
                      <el-option
                        v-for="(label, value) in detailLayoutLabels"
                        :key="value"
                        :label="label"
                        :value="value"
                      />
                    </el-select>
                    <el-select v-model="entry.status" size="small" class="detail-status-select">
                      <el-option label="启用" value="启用" />
                      <el-option label="停用" value="停用" />
                    </el-select>
                    <el-input-number v-model="entry.sortOrder" size="small" :min="0" controls-position="right" />
                  </div>

                  <el-input v-model="entry.title" placeholder="标题 / 参数名" />
                  <el-input v-model="entry.content" type="textarea" :rows="sectionType === 'detail' ? 4 : 2" placeholder="内容 / 参数值" />

                  <div v-if="sectionType === 'detail'" class="detail-image-box">
                    <el-upload
                      drag
                      action=""
                      accept="image/*"
                      :show-file-list="false"
                      :http-request="({ file, onSuccess, onError }) => uploadDetailEntryImage(entry, file).then(() => onSuccess({ code: 200 })).catch(onError)"
                    >
                      <div class="upload-copy">
                        <strong>上传详情图片</strong>
                        <span>用于图文左右或通栏图片排版。</span>
                      </div>
                    </el-upload>
                    <div v-if="entry.imageUrl" class="image-row">
                      <img :src="entry.imageUrl" alt="详情图片">
                      <div class="image-row-main">
                        <div class="image-row-title">
                          <span>详情图</span>
                        </div>
                      </div>
                      <div class="image-actions">
                        <el-button size="small" :icon="Download" @click="downloadImage(entry.imageUrl)">下载</el-button>
                        <el-button size="small" type="danger" :icon="Delete" @click="entry.imageUrl = ''" />
                      </div>
                    </div>
                  </div>

                  <div class="detail-entry-actions">
                    <el-button size="small" @click="moveDetailEntry(entry, -1)">上移</el-button>
                    <el-button size="small" @click="moveDetailEntry(entry, 1)">下移</el-button>
                    <el-button size="small" type="danger" :icon="Delete" @click="removeDetailEntry(entry)">删除</el-button>
                  </div>
                </div>
              </div>
              <div v-else class="detail-empty">暂未维护{{ detailSectionLabels[sectionType] }}</div>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-drawer>

  </div>
</template>

<style scoped>
.admin-page {
  color: #1f2937;
}

.admin-shell {
  display: grid;
  grid-template-columns: 248px minmax(0, 1fr);
  min-height: calc(100vh - 4rem);
}

.admin-sidebar {
  background: #172033;
  color: #f9fafb;
  padding: 24px 18px;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
}

.brand-mark {
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: 8px;
  background: #e8734a;
  font-weight: 700;
}

.admin-brand p {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
}

.admin-brand span:not(.brand-mark) {
  color: #aeb7c7;
  font-size: 12px;
}

.admin-nav {
  display: grid;
  gap: 8px;
}

.admin-nav button {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #c7d0df;
  cursor: pointer;
  font: inherit;
  padding: 12px 14px;
  text-align: left;
}

.admin-nav button.active,
.admin-nav button:hover {
  background: rgba(232, 115, 74, 0.16);
  color: #ffffff;
}

.admin-content {
  min-width: 0;
  padding: 28px;
}

.admin-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.section-kicker {
  margin: 0 0 4px;
  color: #e8734a;
  font-size: 13px;
  font-weight: 700;
}

.admin-toolbar h1 {
  margin: 0;
  color: #111827;
  font-size: clamp(24px, 3vw, 34px);
  font-weight: 800;
  letter-spacing: 0;
}

.toolbar-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.stat-card,
.panel {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.06);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 112px;
  padding: 18px;
}

.stat-icon {
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: 8px;
}

.stat-card span {
  display: block;
  color: #6b7280;
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 6px;
  color: #111827;
  font-size: 26px;
  line-height: 1;
}

.stat-card small {
  margin-left: 4px;
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
}

.stat-primary .stat-icon {
  background: #fff1df;
  color: #e8734a;
}

.stat-green .stat-icon {
  background: #e9f7ef;
  color: #24985a;
}

.stat-gold .stat-icon {
  background: #fff6dc;
  color: #b7791f;
}

.stat-red .stat-icon {
  background: #fff0f0;
  color: #d13f3f;
}

.panel {
  padding: 18px;
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.panel-header h2 {
  margin: 0 0 4px;
  color: #111827;
  font-size: 20px;
}

.panel-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.filters {
  display: grid;
  grid-template-columns: minmax(190px, 260px) 140px 120px;
  gap: 10px;
}

.table-wrap {
  overflow-x: auto;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-cell img {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
}

.image-placeholder {
  display: grid;
  width: 48px;
  height: 48px;
  place-items: center;
  border-radius: 8px;
  background: #f3f4f6;
  color: #9ca3af;
  font-size: 12px;
}

.product-cell strong,
.product-cell span {
  display: block;
}

.product-cell strong {
  color: #111827;
}

.product-cell span {
  color: #6b7280;
  font-size: 12px;
}

.price {
  color: #e8734a;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.image-manager {
  display: grid;
  gap: 12px;
  width: 100%;
}

.upload-copy {
  display: grid;
  gap: 6px;
  color: #4b5563;
}

.upload-copy strong {
  color: #111827;
}

.upload-copy span {
  font-size: 13px;
}

.image-list {
  display: grid;
  gap: 10px;
}

.image-row {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.image-row img {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  object-fit: cover;
  background: #fff1df;
}

.image-row-main {
  display: grid;
  gap: 8px;
  min-width: 0;
}

.image-row-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #374151;
  font-size: 13px;
  font-weight: 700;
}

.image-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-editor {
  display: grid;
  gap: 14px;
  width: 100%;
}

.detail-section {
  display: grid;
  gap: 12px;
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.detail-section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.detail-section-head strong,
.detail-section-head span {
  display: block;
}

.detail-section-head strong {
  color: #111827;
}

.detail-section-head span {
  margin-top: 3px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.5;
}

.detail-entry-list {
  display: grid;
  gap: 12px;
}

.detail-entry {
  display: grid;
  gap: 10px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
}

.detail-entry-top {
  display: grid;
  grid-template-columns: minmax(120px, 1fr) 96px 120px;
  gap: 8px;
}

.detail-image-box {
  display: grid;
  gap: 10px;
}

.detail-entry-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.detail-empty {
  border: 1px dashed #d1d5db;
  border-radius: 8px;
  color: #9ca3af;
  padding: 16px;
  text-align: center;
}

.variant-editor {
  display: grid;
  gap: 12px;
  width: 100%;
}

.variant-editor-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.variant-editor-head strong,
.variant-editor-head span {
  display: block;
}

.variant-editor-head strong {
  color: #111827;
}

.variant-editor-head span {
  margin-top: 3px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.5;
}

.variant-list {
  display: grid;
  gap: 12px;
}

.variant-card {
  display: grid;
  grid-template-columns: 150px minmax(0, 1fr);
  gap: 18px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
}

.variant-media {
  display: grid;
  width: 150px;
  height: 150px;
  place-items: center;
  overflow: hidden;
  border-radius: 8px;
  background: #fff1df;
  color: #9ca3af;
  font-size: 12px;
}

.variant-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.variant-fields {
  display: grid;
  align-content: start;
  gap: 14px;
  min-width: 0;
}

.variant-main-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.variant-price-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(120px, 1fr));
  gap: 12px;
}

.variant-price-grid label {
  display: grid;
  gap: 6px;
  min-width: 0;
}

.variant-price-grid label > span {
  color: #6b7280;
  font-size: 12px;
  font-weight: 700;
}

.variant-price-grid :deep(.el-input-number),
.variant-price-grid :deep(.el-select) {
  width: 100%;
}

.variant-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 8px;
}

.home-layout {
  display: grid;
  grid-template-columns: 230px minmax(0, 1fr);
  gap: 16px;
  min-height: 420px;
}

.home-section-list {
  display: grid;
  align-content: start;
  gap: 8px;
}

.home-section-list button {
  display: grid;
  gap: 4px;
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  color: #374151;
  cursor: pointer;
  padding: 12px;
  text-align: left;
}

.home-section-list button.active,
.home-section-list button:hover {
  border-color: #e8734a;
  background: #fff7ed;
}

.home-section-list strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.home-section-list span {
  color: #6b7280;
  font-size: 12px;
}

.home-editor {
  display: grid;
  align-content: start;
  gap: 14px;
  min-width: 0;
}

.home-section-card {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
  padding: 16px;
}

.home-code {
  color: #e8734a;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.home-section-card h3 {
  margin: 4px 0 8px;
  color: #111827;
  font-size: 18px;
}

.home-section-card p {
  display: -webkit-box;
  margin: 0;
  overflow: hidden;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.7;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.home-section-actions {
  display: flex;
  flex-shrink: 0;
  align-items: flex-start;
  gap: 8px;
}

:deep(.el-button--primary) {
  --el-button-bg-color: #e8734a;
  --el-button-border-color: #e8734a;
  --el-button-hover-bg-color: #c45a32;
  --el-button-hover-border-color: #c45a32;
}

@media (max-width: 1080px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    display: none;
  }

  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .panel-header {
    display: grid;
  }

  .home-layout {
    grid-template-columns: 1fr;
  }

  .home-section-actions,
  .home-section-card {
    display: grid;
  }
}

@media (max-width: 720px) {
  .admin-content {
    padding: 18px 14px;
  }

  .admin-toolbar {
    align-items: flex-start;
    flex-direction: column;
  }

  .toolbar-actions,
  .toolbar-actions .el-button {
    width: 100%;
  }

  .stats-grid,
  .filters,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .image-row {
    grid-template-columns: 56px minmax(0, 1fr);
  }

  .detail-entry-top {
    grid-template-columns: 1fr;
  }

  .variant-card,
  .variant-main-grid,
  .variant-price-grid {
    grid-template-columns: 1fr;
  }

  .variant-media {
    width: 100%;
    height: auto;
    aspect-ratio: 1;
  }

  .variant-editor-head {
    display: grid;
  }

  .image-row img {
    width: 56px;
    height: 56px;
  }

  .image-actions {
    grid-column: 1 / -1;
    justify-content: flex-end;
  }
}
</style>
