# Warm Island Shop

一个温暖的小岛商店，前后端分离的电商项目。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + Vite + Tailwind CSS |
| 后端 | Spring Boot 3.2.5 + MyBatis-Plus |
| 数据库 | MySQL |

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
│   │   └── entity/       # 实体类
│   └── src/main/resources/
│       ├── application.yml
│       └── db/schema.sql # 数据库初始化脚本
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

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`。

## 功能

- [x] 商品列表展示
- [ ] 商品详情
- [ ] 购物车
- [ ] 订单管理

## License

MIT
