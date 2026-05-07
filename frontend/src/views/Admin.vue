<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Box,
  Delete,
  Download,
  Edit,
  Goods,
  Plus,
  Refresh,
  Search,
  TrendCharts,
} from '@element-plus/icons-vue'

const API_BASE = '/api/products'

const products = ref([])
const loading = ref(false)
const keyword = ref('')
const selectedCategory = ref('全部')
const selectedStatus = ref('全部')
const drawerVisible = ref(false)
const editingId = ref(null)

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
  description: '',
  tags: '',
})

const categories = computed(() => ['全部', ...new Set(products.value.map((item) => item.category))])

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
    description: '',
    tags: '',
  })
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

function openCreateDrawer() {
  editingId.value = null
  resetForm()
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
    description: product.description,
    tags: product.tags.join('、'),
  })
  drawerVisible.value = true
}

function syncPrimaryImage() {
  productForm.image = productForm.images[0] || productForm.image || '/products/coffee.jpg'
}

function removeImage(index) {
  productForm.images.splice(index, 1)
  syncPrimaryImage()
}

function moveImage(index, direction) {
  const nextIndex = index + direction

  if (nextIndex < 0 || nextIndex >= productForm.images.length) {
    return
  }

  const [image] = productForm.images.splice(index, 1)
  productForm.images.splice(nextIndex, 0, image)
  syncPrimaryImage()
}

async function uploadProductImage({ file, onSuccess, onError }) {
  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await fetch(`${API_BASE}/upload`, {
      method: 'POST',
      body: formData,
    })
    const result = await response.json()

    if (!response.ok || result.code !== 200) {
      throw new Error(result.message || '上传失败')
    }

    productForm.images.push(result.data)
    syncPrimaryImage()
    ElMessage.success('图片已上传')
    onSuccess(result)
  } catch (error) {
    ElMessage.error(`图片上传失败：${error.message}`)
    onError(error)
  }
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

async function saveProduct() {
  if (!productForm.name.trim() || !productForm.category.trim()) {
    ElMessage.warning('请填写商品名称和分类')
    return
  }

  const images = productForm.images.map((image) => image.trim()).filter(Boolean)

  if (images.length === 0) {
    ElMessage.warning('请至少上传一张商品图片')
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

async function toggleStatus(product) {
  const nextStatus = product.status === '销售中' ? '已下架' : '销售中'

  try {
    await requestJson(`${API_BASE}/${product.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...product,
        tags: product.tags.join(','),
        images: product.images,
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
}

onMounted(fetchProducts)
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
          <button class="active">
            <el-icon><Goods /></el-icon>
            商品管理
          </button>
          <button>
            <el-icon><TrendCharts /></el-icon>
            经营概览
          </button>
          <button>
            <el-icon><Box /></el-icon>
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
            <el-button type="primary" :icon="Plus" @click="openCreateDrawer">新增商品</el-button>
          </div>
        </section>

        <section class="stats-grid">
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

        <section class="panel">
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
                <el-option v-for="category in categories" :key="category" :label="category" :value="category" />
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
      </main>
    </div>

    <el-drawer
      v-model="drawerVisible"
      :title="editingId ? '编辑商品' : '新增商品'"
      size="min(520px, 100%)"
    >
      <el-form label-position="top">
        <el-form-item label="商品名称">
          <el-input v-model="productForm.name" placeholder="例如：暖屿咖啡" />
        </el-form-item>
        <el-form-item label="英文名称">
          <el-input v-model="productForm.nameEn" placeholder="Warm Island Coffee" />
        </el-form-item>
        <el-form-item label="商品图片">
          <div class="image-manager">
            <el-upload
              drag
              action=""
              accept="image/*"
              :show-file-list="false"
              :http-request="uploadProductImage"
            >
              <div class="upload-copy">
                <strong>上传商品图片</strong>
                <span>支持多张图片，保存时按下方顺序展示。</span>
              </div>
            </el-upload>

            <div class="image-list">
              <div v-for="(image, index) in productForm.images" :key="`${image}-${index}`" class="image-row">
                <img :src="image" :alt="`商品图片 ${index + 1}`">
                <div class="image-row-main">
                  <div class="image-row-title">
                    <span>第 {{ index + 1 }} 张</span>
                    <el-tag v-if="index === 0" size="small" type="success">主图</el-tag>
                  </div>
                </div>
                <div class="image-actions">
                  <el-button size="small" :disabled="index === 0" @click="moveImage(index, -1)">上移</el-button>
                  <el-button size="small" :disabled="index === productForm.images.length - 1" @click="moveImage(index, 1)">下移</el-button>
                  <el-button size="small" :icon="Download" @click="downloadImage(image)">下载</el-button>
                  <el-button size="small" type="danger" :icon="Delete" @click="removeImage(index)" />
                </div>
              </div>
            </div>
          </div>
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="分类">
            <el-input v-model="productForm.category" placeholder="饮品 / 甜点 / 周边" />
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
        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="4" />
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
