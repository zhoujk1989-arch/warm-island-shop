<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus } from '@element-plus/icons-vue'
import { CATEGORY_API_BASE, requestJson } from './adminUtils'

const categoryList = ref([])
const loading = ref(false)
const drawerVisible = ref(false)
const editingId = ref(null)
const form = ref({
  name: '',
  sortOrder: 0,
  status: '启用',
})

async function fetchCategories() {
  loading.value = true

  try {
    categoryList.value = await requestJson(CATEGORY_API_BASE)
  } catch (error) {
    ElMessage.error(`读取分类失败：${error.message}`)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.value = {
    name: '',
    sortOrder: categoryList.value.length + 1,
    status: '启用',
  }
  drawerVisible.value = true
}

function openEdit(category) {
  editingId.value = category.id
  form.value = {
    name: category.name,
    sortOrder: category.sortOrder,
    status: category.status,
  }
  drawerVisible.value = true
}

async function saveCategory() {
  if (!form.value.name.trim()) {
    ElMessage.warning('请填写分类名称')
    return
  }

  const payload = {
    name: form.value.name.trim(),
    sortOrder: Number(form.value.sortOrder) || 0,
    status: form.value.status,
  }

  try {
    if (editingId.value) {
      await requestJson(`${CATEGORY_API_BASE}/${editingId.value}`, {
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

    drawerVisible.value = false
    await fetchCategories()
  } catch (error) {
    ElMessage.error(`保存分类失败：${error.message}`)
  }
}

async function toggleStatus(category) {
  try {
    await requestJson(`${CATEGORY_API_BASE}/${category.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...category,
        status: category.status === '启用' ? '停用' : '启用',
      }),
    })
    ElMessage.success('分类状态已更新')
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

onMounted(fetchCategories)
</script>

<template>
  <section class="panel">
    <div class="panel-header">
      <div>
        <h2>分类维护</h2>
        <p>维护商品分类，商品编辑时只能从启用分类中选择。</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openCreate">新增分类</el-button>
    </div>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="categoryList" row-key="id">
        <el-table-column prop="name" label="分类名称" min-width="180" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新日期" min-width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" @click="toggleStatus(row)">
              {{ row.status === '启用' ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="removeCategory(row)" />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer
      v-model="drawerVisible"
      :title="editingId ? '编辑分类' : '新增分类'"
      size="min(420px, 100%)"
    >
      <el-form label-position="top">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="例如：饮品" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="启用" value="启用" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
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

.table-wrap {
  overflow-x: auto;
}

@media (max-width: 720px) {
  .panel-header {
    display: grid;
  }
}
</style>
