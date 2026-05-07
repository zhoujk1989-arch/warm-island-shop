<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Download, Edit, Plus } from '@element-plus/icons-vue'
import { downloadImage, HOME_API_BASE, requestJson, uploadImageFile } from './adminUtils'

const loading = ref(false)
const sections = ref([])
const activeCode = ref('hero')
const sectionDrawerVisible = ref(false)
const itemDrawerVisible = ref(false)
const editingSectionCode = ref('')
const editingItemId = ref(null)

const sectionForm = reactive({
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

const itemForm = reactive({
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

const currentSection = computed(() =>
  sections.value.find((section) => section.code === activeCode.value) || sections.value[0],
)

async function fetchHomeContent() {
  loading.value = true

  try {
    sections.value = await requestJson(HOME_API_BASE)

    if (!sections.value.some((section) => section.code === activeCode.value)) {
      activeCode.value = sections.value[0]?.code || 'hero'
    }
  } catch (error) {
    ElMessage.error(`读取首页内容失败：${error.message}`)
  } finally {
    loading.value = false
  }
}

function fillSectionForm(section) {
  Object.assign(sectionForm, {
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

function openEditSection(section) {
  editingSectionCode.value = section.code
  fillSectionForm(section)
  sectionDrawerVisible.value = true
}

async function saveSection() {
  try {
    await requestJson(`${HOME_API_BASE}/sections/${editingSectionCode.value}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...sectionForm,
        sortOrder: Number(sectionForm.sortOrder) || 0,
      }),
    })
    ElMessage.success('首页内容位已保存')
    sectionDrawerVisible.value = false
    await fetchHomeContent()
  } catch (error) {
    ElMessage.error(`保存首页内容位失败：${error.message}`)
  }
}

async function toggleSectionStatus(section) {
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

function resetItemForm(sectionCode) {
  Object.assign(itemForm, {
    sectionCode,
    itemType: sectionCode === 'category_cards' ? 'category_card' : 'button',
    title: '',
    subtitle: '',
    description: '',
    imageUrl: '',
    linkText: '',
    linkUrl: '',
    sortOrder: (currentSection.value?.items?.length || 0) + 1,
    status: '启用',
  })
}

function openCreateItem(sectionCode) {
  editingItemId.value = null
  resetItemForm(sectionCode)
  itemDrawerVisible.value = true
}

function openEditItem(item) {
  editingItemId.value = item.id
  Object.assign(itemForm, {
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
  itemDrawerVisible.value = true
}

async function saveItem() {
  if (!itemForm.title.trim()) {
    ElMessage.warning('请填写内容项标题')
    return
  }

  const payload = {
    ...itemForm,
    sortOrder: Number(itemForm.sortOrder) || 0,
  }

  try {
    if (editingItemId.value) {
      await requestJson(`${HOME_API_BASE}/items/${editingItemId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload),
      })
      ElMessage.success('首页内容项已更新')
    } else {
      await requestJson(`${HOME_API_BASE}/sections/${itemForm.sectionCode}/items`, {
        method: 'POST',
        body: JSON.stringify(payload),
      })
      ElMessage.success('首页内容项已新增')
    }

    itemDrawerVisible.value = false
    await fetchHomeContent()
  } catch (error) {
    ElMessage.error(`保存首页内容项失败：${error.message}`)
  }
}

async function toggleItemStatus(item) {
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

async function removeItem(item) {
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

async function uploadSectionImage({ file, onSuccess, onError }) {
  try {
    sectionForm.imageUrl = await uploadImageFile(file)
    ElMessage.success('首页图片已上传')
    onSuccess({ code: 200 })
  } catch (error) {
    ElMessage.error(`图片上传失败：${error.message}`)
    onError(error)
  }
}

async function uploadItemImage({ file, onSuccess, onError }) {
  try {
    itemForm.imageUrl = await uploadImageFile(file)
    ElMessage.success('首页内容项图片已上传')
    onSuccess({ code: 200 })
  } catch (error) {
    ElMessage.error(`图片上传失败：${error.message}`)
    onError(error)
  }
}

onMounted(fetchHomeContent)

const ImageUploadBox = {
  props: {
    imageUrl: { type: String, default: '' },
    uploadTitle: { type: String, required: true },
    uploadTip: { type: String, required: true },
  },
  emits: ['upload', 'download', 'remove'],
  template: `
    <div class="image-manager">
      <el-upload drag action="" accept="image/*" :show-file-list="false" :http-request="(payload) => $emit('upload', payload)">
        <div class="upload-copy">
          <strong>{{ uploadTitle }}</strong>
          <span>{{ uploadTip }}</span>
        </div>
      </el-upload>
      <div v-if="imageUrl" class="image-row">
        <img :src="imageUrl" alt="当前图片">
        <div class="image-row-main">
          <div class="image-row-title"><span>当前图片</span></div>
        </div>
        <div class="image-actions">
          <el-button size="small" @click="$emit('download')">下载</el-button>
          <el-button size="small" type="danger" @click="$emit('remove')">删除</el-button>
        </div>
      </div>
    </div>
  `,
}
</script>

<template>
  <section class="panel">
    <div class="panel-header">
      <div>
        <h2>首页装修</h2>
        <p>维护首页固定内容位、推广文案、按钮和图片，前台会从数据库实时读取。</p>
      </div>
      <el-button v-if="currentSection" type="primary" :icon="Plus" @click="openCreateItem(currentSection.code)">
        新增首页内容
      </el-button>
    </div>

    <div class="home-layout" v-loading="loading">
      <aside class="home-section-list">
        <button
          v-for="section in sections"
          :key="section.code"
          :class="{ active: activeCode === section.code }"
          @click="activeCode = section.code"
        >
          <strong>{{ section.title || section.code }}</strong>
          <span>{{ section.code }} · {{ section.status }}</span>
        </button>
      </aside>

      <div v-if="currentSection" class="home-editor">
        <div class="home-section-card">
          <div>
            <span class="home-code">{{ currentSection.code }}</span>
            <h3>{{ currentSection.title }}</h3>
            <p>{{ currentSection.body }}</p>
          </div>
          <div class="home-section-actions">
            <el-tag :type="currentSection.status === '启用' ? 'success' : 'info'">
              {{ currentSection.status }}
            </el-tag>
            <el-button size="small" :icon="Edit" @click="openEditSection(currentSection)">编辑内容位</el-button>
            <el-button size="small" @click="toggleSectionStatus(currentSection)">
              {{ currentSection.status === '启用' ? '停用' : '启用' }}
            </el-button>
          </div>
        </div>

        <div class="table-wrap">
          <el-table :data="currentSection.items || []" row-key="id">
            <el-table-column label="内容项" min-width="240">
              <template #default="{ row }">
                <div class="content-cell">
                  <img v-if="row.imageUrl" :src="row.imageUrl" :alt="row.title" />
                  <div v-else class="image-placeholder">无图</div>
                  <div>
                    <strong>{{ row.title }}</strong>
                    <span>{{ row.subtitle || row.description || row.linkUrl || '-' }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="itemType" label="类型" width="130" />
            <el-table-column prop="sortOrder" label="排序" width="90" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '启用' ? 'success' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="250">
              <template #default="{ row }">
                <el-button size="small" :icon="Edit" @click="openEditItem(row)">编辑</el-button>
                <el-button size="small" @click="toggleItemStatus(row)">
                  {{ row.status === '启用' ? '停用' : '启用' }}
                </el-button>
                <el-button size="small" type="danger" :icon="Delete" @click="removeItem(row)" />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <el-drawer v-model="sectionDrawerVisible" title="编辑首页内容位" size="min(520px, 100%)">
      <el-form label-position="top">
        <el-form-item label="内容位编码">
          <el-input v-model="sectionForm.code" disabled />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="眉标">
            <el-input v-model="sectionForm.eyebrow" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="sectionForm.status">
              <el-option label="启用" value="启用" />
              <el-option label="停用" value="停用" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="标题">
          <el-input v-model="sectionForm.title" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="图片">
          <ImageUploadBox
            :image-url="sectionForm.imageUrl"
            upload-title="上传内容位图片"
            upload-tip="上传后自动替换当前图片。"
            @upload="uploadSectionImage"
            @download="downloadImage(sectionForm.imageUrl)"
            @remove="sectionForm.imageUrl = ''"
          />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="sectionForm.body" type="textarea" :rows="5" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="按钮文案">
            <el-input v-model="sectionForm.linkText" />
          </el-form-item>
          <el-form-item label="按钮链接">
            <el-input v-model="sectionForm.linkUrl" placeholder="/shop" />
          </el-form-item>
        </div>
        <el-form-item label="排序">
          <el-input-number v-model="sectionForm.sortOrder" :min="0" controls-position="right" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="sectionDrawerVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSection">保存</el-button>
      </template>
    </el-drawer>

    <el-drawer
      v-model="itemDrawerVisible"
      :title="editingItemId ? '编辑首页内容项' : '新增首页内容项'"
      size="min(520px, 100%)"
    >
      <el-form label-position="top">
        <div class="form-grid">
          <el-form-item label="内容位">
            <el-input v-model="itemForm.sectionCode" disabled />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="itemForm.itemType">
              <el-option label="按钮" value="button" />
              <el-option label="主视觉图片" value="hero_image" />
              <el-option label="小图" value="small_image" />
              <el-option label="浮动图" value="float_image" />
              <el-option label="分类卡片" value="category_card" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="标题">
          <el-input v-model="itemForm.title" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="itemForm.subtitle" />
        </el-form-item>
        <el-form-item label="图片">
          <ImageUploadBox
            :image-url="itemForm.imageUrl"
            upload-title="上传内容项图片"
            upload-tip="按钮类内容可以不上传图片。"
            @upload="uploadItemImage"
            @download="downloadImage(itemForm.imageUrl)"
            @remove="itemForm.imageUrl = ''"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="itemForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="链接文案">
            <el-input v-model="itemForm.linkText" />
          </el-form-item>
          <el-form-item label="链接地址">
            <el-input v-model="itemForm.linkUrl" placeholder="/shop" />
          </el-form-item>
        </div>
        <div class="form-grid">
          <el-form-item label="排序">
            <el-input-number v-model="itemForm.sortOrder" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="itemForm.status">
              <el-option label="启用" value="启用" />
              <el-option label="停用" value="停用" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="itemDrawerVisible = false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </template>
    </el-drawer>
  </section>
</template>

<style scoped>
.panel {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.06);
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

.table-wrap {
  overflow-x: auto;
}

.content-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.content-cell img,
.image-placeholder {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
}

.image-placeholder {
  display: grid;
  place-items: center;
  background: #f3f4f6;
  color: #9ca3af;
  font-size: 12px;
}

.content-cell strong,
.content-cell span {
  display: block;
}

.content-cell strong {
  color: #111827;
}

.content-cell span {
  color: #6b7280;
  font-size: 12px;
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

.image-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

@media (max-width: 1080px) {
  .home-layout {
    grid-template-columns: 1fr;
  }

  .home-section-actions,
  .home-section-card,
  .panel-header {
    display: grid;
  }
}

@media (max-width: 720px) {
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
