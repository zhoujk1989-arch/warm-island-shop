# Warm Island Shop - API 文档

## 基础信息

- 基础路径：`/api`
- 认证方式：Bearer Token (JWT)
- 响应格式：
  ```json
  { "code": 200, "message": "success", "data": ... }
  ```

---

## 认证接口

### POST `/api/auth/login`
登录

**请求体：**
```json
{ "username": "admin", "password": "admin123" }
```

**响应：**
```json
{ "code": 200, "data": { "token": "eyJ...", "username": "admin", "role": "ADMIN" } }
```

### POST `/api/auth/register`
注册（同登录，自动返回 token）

---

## 商品接口

### GET `/api/products`
分页查询商品

**Query 参数：**
- `pageNum` (默认1)、`pageSize` (默认10)
- `category` (可选)、`keyword` (可选，搜索)

**响应：** `{ "code": 200, "data": { "records": [...], "total": 100 } }`

### GET `/api/products/hot?limit=4`
获取热门商品（按销量排序）

### GET `/api/products/{id}`
获取商品详情

### POST `/api/products`
新增商品（需 ADMIN 权限）

### PUT `/api/products/{id}`
更新商品（需 ADMIN 权限）

### DELETE `/api/products/{id}`
删除商品（需 ADMIN 权限）

### POST `/api/products/upload`
上传商品图片（需 ADMIN 权限）
- 请求：multipart/form-data，字段名 `file`
- 响应：图片 URL 字符串

---

## 分类接口

### GET `/api/product-categories`
获取全部分类列表

---

## 首页内容接口

### GET `/api/home/sections`
获取全部分区（首页装修）

### GET `/api/home/sections/{code}/items`
获取指定分区的子项

---

## 订单接口（需认证）

### POST `/api/orders`
创建订单

**请求体：**
```json
{
  "shippingAddress": "广东省深圳市...",
  "recipientName": "张三",
  "recipientPhone": "13800138000",
  "items": [
    { "productId": 1, "variantId": null, "quantity": 2 }
  ]
}
```

### GET `/api/orders`
获取当前登录用户的订单列表

### GET `/api/orders/{id}`
获取订单详情

### PATCH `/api/orders/{id}/status`
更新订单状态（需 ADMIN 权限）
- 请求体：`{ "status": "PAID" }`
- 状态枚举：`PENDING` / `PAID` / `SHIPPED` / `COMPLETED` / `CANCELLED`

---

## 错误码

| code | 说明 |
|------|------|
| 200  | 成功 |
| 400  | 参数错误 |
| 401  | 未登录 / 认证失败 |
| 403  | 权限不足 |
| 404  | 资源不存在 |
| 500  | 服务器错误 |
