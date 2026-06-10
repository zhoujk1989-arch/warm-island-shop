# Warm Island Shop

一个温暖的小岛商店，前后端分离的电商项目。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + Vite + Tailwind CSS + Element Plus |
| 后端 | Spring Boot 3.2.5 + MyBatis-Plus + Spring Security |
| 数据库 | MySQL 8 |
| 认证 | JWT (JJWT 0.11.5) |

## 项目结构

```
├── frontend/          # Vue 3 前端应用
│   ├── src/
│   ├── public/        # 静态资源（商品图片等）
│   └── package.json
├── backend/           # Spring Boot 后端 API
│   ├── src/main/java/com/warmisland/
│   │   ├── controller/   # 接口层
│   │   ├── service/      # 业务逻辑
│   │   ├── mapper/       # 数据访问
│   │   ├── entity/       # 实体类
│   │   ├── security/     # JWT 认证
│   │   ├── config/       # 配置类
│   │   └── handler/      # 全局异常处理
│   └── src/main/resources/
│       ├── application.yml
│       └── db/schema.sql  # 数据库初始化脚本
└── README.md
```

## 快速开始

### 前置要求

- Node.js >= 18
- JDK >= 17
- MySQL >= 8.0

### 后端启动

```bash
cd backend
# 配置 application.yml 中的数据库连接信息
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

默认管理员账号：`admin` / `admin123`

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`。

## 功能

- [x] 用户注册 / 登录（JWT 认证）
- [x] 商品列表展示（分页、分类筛选、关键词搜索）
- [x] 商品详情
- [x] 购物车（Pinia 状态管理）
- [x] 结算页面
- [x] 订单系统（创建、查询、状态更新）
- [x] 后台管理（商品、分类、首页装修）
- [ ] 支付集成
- [ ] 个人中心

## API 文档

详见 [backend/API.md](backend/API.md)

访问 `http://localhost:8080/swagger-ui.html` 查看 Swagger 文档（需先集成 SpringDoc）。

## 环境变量

### 后端 (`application.yml` 或环境变量)

| 变量 | 说明 | 默认值 |
|---|---|---|
| `DB_HOST` | 数据库主机 | `127.0.0.1` |
| `DB_PORT` | 数据库端口 | `3306` |
| `DB_NAME` | 数据库名 | `warm_island` |
| `DB_USER` | 数据库用户 | `root` |
| `DB_PASSWORD` | 数据库密码 | `root` |
| `JWT_SECRET` | JWT 签名密钥 | `warm-island-secret-key-...` |
| `JWT_EXPIRATION` | JWT 过期时间(ms) | `86400000` (24h) |
| `PORT` | 后端端口 | `8080` |

## License

MIT
