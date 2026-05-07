async function requestHome(url) {
  const response = await fetch(url)
  const result = await response.json()

  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '首页内容读取失败')
  }

  return result.data || []
}

export async function fetchHomeConfig() {
  return requestHome('/api/home')
}

export async function fetchAdminHomeConfig() {
  return requestHome('/api/admin/home')
}
