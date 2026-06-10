---
name: warm-island-shop-dev-tasks
overview: 对项目进行全面代码审查，按优先级输出后续开发任务清单，涵盖功能完善、代码优化、测试健壮性、安全规范、部署文档五大类。
design:
  architecture:
    framework: vue
  styleKeywords:
    - 温暖治愈
    - 奶油色系
    - 圆角卡片
    - 柔和阴影
  fontSystem:
    fontFamily: PingFang SC
    heading:
      size: 24px
      weight: 700
    subheading:
      size: 18px
      weight: 600
    body:
      size: 14px
      weight: 400
  colorSystem:
    primary:
      - "#E8734A"
      - "#F4A261"
    background:
      - "#FEF9F0"
      - "#FFFFFF"
    text:
      - "#1F2937"
      - "#6B7280"
    functional:
      - "#10B981"
      - "#EF4444"
      - "#F59E0B"
todos:
  - id: add-jwt-auth
    content: 集成 Spring Security + JWT 认证，保护 Admin API，新增 AuthController 和用户实体
    status: completed
  - id: fix-security-issues
    content: 修复 CORS 配置（指定允许域名）、SQL注入风险（白名单校验）、文件上传安全加固
    status: completed
    dependencies:
      - add-jwt-auth
  - id: add-user-system
    content: 实现用户注册登录功能，前端新增登录页和个人中心页，集成 Pinia 用户状态管理
    status: completed
    dependencies:
      - add-jwt-auth
  - id: implement-cart
    content: 使用 Pinia 实现真实购物车状态管理，修改 Cart.vue 接入 store，实现增减删清空功能
    status: completed
    dependencies:
      - add-user-system
  - id: implement-order-system
    content: 实现订单系统：新增 Order/OrderItem 实体、Mapper、Service、Controller，前端新增结算页和订单列表页
    status: completed
    dependencies:
      - implement-cart
  - id: refactor-admin-vue
    content: 拆分 Admin.vue 为子组件（ProductManager/CategoryManager/HomeManager），降低单文件复杂度
    status: completed
  - id: refactor-product-service
    content: 重构 ProductServiceImpl，抽取 ImageService/DetailEntryService/VariantService，优化 replaceXxx 为增量更新
    status: completed
    dependencies:
      - refactor-admin-vue
  - id: add-global-exception-handler
    content: 新增 GlobalExceptionHandler（@ControllerAdvice），统一错误响应格式，添加 Bean Validation 参数校验
    status: completed
  - id: add-tests
    content: 补充后端 JUnit 5 + Mockito 单元测试，优先覆盖 Service 层和 Controller 层
    status: completed
    dependencies:
      - add-global-exception-handler
  - id: improve-frontend-search
    content: 修改 Shop.vue 搜索功能，改为调用后端 /api/products?keyword= 搜索 API
    status: completed
  - id: add-api-docs-and-swagger
    content: 集成 SpringDoc OpenAPI，自动生成 API 文档，补充 README 功能清单更新
    status: completed
---

## 用户需求

全面审查 warm-island-shop 项目代码库，按优先级输出后续开发任务清单，涵盖以下五大类：

## 审查范围与发现

### 1. 功能完善（核心功能缺失）

- **用户系统完全缺失**：无注册、登录、JWT认证、个人中心
- **购物车功能未实现**：Cart.vue 仅为静态展示，TODO注释表明 Pinia store 未集成
- **订单系统完全缺失**：无 Order/OrderItem 实体、Controller、Service，无下单流程
- **支付功能缺失**：无任何支付集成（支付宝/微信支付）
- **前端搜索未使用后端API**：Shop.vue 仅在本地已加载数据中过滤，未调用后端搜索接口
- **分类数据硬编码**：Footer组件中有硬编码分类数据，未从后端动态获取

### 2. 代码优化（重构与性能）

- **Admin.vue 过重**：单文件 1883 行，包含商品管理、分类管理、首页装修，应拆分为子组件
- **ProductServiceImpl 过重**：340行，承担过多职责（图片处理、详情处理、款式处理），违反单一职责原则
- **replaceXxx 方法效率低下**：先删除再全量插入，应使用 upsert 或对比增量更新
- **N+1 查询问题**：fillProductImages/fillProductDetailEntries/fillProductVariants 产生额外查询
- **DatabaseInitializer 不适合生产环境**：启动时自动建表+种子数据，应使用 Flyway/Liquibase
- **重复代码**：adminUtils.js 与 HomeManager.vue 中有重复的工具函数

### 3. 测试与健壮性

- **完全无测试代码**：pom.xml 已引入 spring-boot-starter-test 但未编写任何测试
- **无全局异常处理**：Controller 层无 @ControllerAdvice，异常直接抛出堆栈给前端
- **参数校验缺失**：pom.xml 已引入 spring-boot-starter-validation 但未使用，Entity 无 Bean Validation 注解
- **前端错误处理不完善**：API 请求失败时错误提示不够友好，未分类处理错误类型
- **文件上传缺少二次校验**：Controller 未对文件类型、大小进行严格校验

### 4. 安全与规范

- **无认证授权机制**：所有 Admin API 无任何保护，任何人可调用
- **CORS 配置过于宽松**：`addAllowedOriginPattern("*")` 配合 `setAllowCredentials(true)` 存在CSRF风险
- **SQL注入风险**：DatabaseInitializer.addColumnIfMissing() 中直接拼接 columnName/columnDefinition
- **敏感信息明文配置**：application.yml 中数据库密码明文，start.sh 中硬编码默认密码
- **文件上传目录暴露为静态资源**：上传文件可通过URL直接访问，存在安全风险
- **XSS 防护不足**：后端未对用户输入进行HTML标签过滤
- **代码规范问题**：状态值使用中文魔法字符串（'销售中'、'启用'），应定义为枚举或常量

### 5. 部署与文档

- **README 未更新**：功能清单中"商品详情"、"购物车"、"订单管理"仍为未选中状态
- **无 API 文档**：未集成 Swagger/OpenAPI
- **无数据库 Schema 文档**：仅靠 DatabaseInitializer 中的建表语句
- **前端无环境配置**：无 .env.development / .env.production，API基础路径硬编码
- **缺少 Docker 部署支持**：无 Dockerfile / docker-compose.yml
- **缺少 CI/CD 配置**：无 GitHub Actions 等自动化配置
- **日志配置缺失**：无 logback-spring.xml，应用内部日志格式未配置
- **dist/ 被提交到代码仓库**：应被 .gitignore 排除

## 执行方向

按优先级排序输出任务清单，每项任务包含：具体执行方向、预期目标、涉及文件。

## 技术栈确认

- **前端**：Vue 3 + Vite + Tailwind CSS + Element Plus
- **后端**：Spring Boot 3.2.5 + MyBatis-Plus 3.5.6 + MySQL 8
- **Java**：17

## 实施方案

### 阶段一：安全与认证（最高优先级）

**目标**：为 Admin API 添加认证保护，修复安全漏洞

1. **集成 Spring Security + JWT**

- 新增 `entity/User.java`（id, username, passwordHash, role, createTime）
- 新增 `dto/LoginRequest.java`、`dto/LoginResponse.java`
- 新增 `config/SecurityConfig.java`（Spring Security 配置，JWT 过滤器）
- 新增 `security/JwtTokenProvider.java`（JWT 生成与验证）
- 新增 `service/UserService.java` + 实现类（用户认证逻辑）
- 新增 `controller/AuthController.java`（/api/auth/login, /api/auth/register）
- 修改 `CorsConfig.java`：生产环境指定允许域名，不使用通配符 `*`

2. **修复 SQL 注入风险**

- 修改 `DatabaseInitializer.addColumnIfMissing()`：对 columnDefinition 进行白名单校验，或使用预定义枚举

3. **文件上传安全加固**

- 修改 `ProductController.upload()`：增加文件类型白名单校验、文件大小二次校验
- 考虑将上传文件转存到 OSS 或使用独立文件服务

### 阶段二：核心电商功能（高优先级）

**目标**：实现用户系统、购物车、订单系统

1. **用户系统**

- 新增 `entity/User.java`
- 新增 `mapper/UserMapper.java`
- 新增 `service/UserService.java` + 实现类
- 新增 `controller/AuthController.java`（注册、登录、个人中心）
- 前端新增 `views/Login.vue`、`views/UserProfile.vue`
- 前端新增 `stores/auth.js`（Pinia 用户状态管理）
- 修改 `router/index.js`：添加路由守卫

2. **购物车功能**

- 前端新增 `stores/cart.js`（Pinia store，替代 Cart.vue 中的 ref）
- 修改 `Cart.vue`：接入 Pinia store，实现真实购物车逻辑
- 后端可选：新增 `entity/CartItem.java` + Mapper/Service（服务端购物车持久化）

3. **订单系统**

- 新增 `entity/Order.java`（id, userId, totalAmount, status, shippingAddress, createTime）
- 新增 `entity/OrderItem.java`（id, orderId, productId, variantId, quantity, price）
- 新增 `mapper/OrderMapper.java`、`OrderItemMapper.java`
- 新增 `service/OrderService.java` + 实现类（创建订单、查询订单、更新状态）
- 新增 `controller/OrderController.java`（/api/orders）
- 前端新增 `views/Checkout.vue`、`views/OrderList.vue`、`views/OrderDetail.vue`
- 修改 `Cart.vue`："去结算"按钮跳转结算页

### 阶段三：代码质量提升（中优先级）

**目标**：重构过重类、提升代码可维护性

1. **拆分 Admin.vue**

- 将商品管理拆分为 `components/admin/ProductManager.vue`
- 将分类管理已部分拆分为 `components/admin/CategoryManager.vue`（需进一步完善）
- 将首页装修已拆分为 `components/admin/HomeManager.vue`
- 创建 `components/admin/AdminLayout.vue`（后台布局组件）

2. **重构 ProductServiceImpl**

- 抽取图片处理逻辑到 `ImageService.java`
- 抽取详情项处理逻辑到 `DetailEntryService.java`
- 抽取款式处理逻辑到 `VariantService.java`
- 修改 `replaceXxx` 方法：使用对比增量更新，减少不必要的删除插入

3. **添加全局异常处理**

- 新增 `handler/GlobalExceptionHandler.java`（`@ControllerAdvice`）
- 统一错误响应格式（使用已有的 `Result` 类）
- 处理常见异常：MethodArgumentNotValidException、BusinessException 等

4. **添加参数校验**

- 在 `entity/Product.java` 等实体类上添加 Bean Validation 注解（`@NotNull`、`@DecimalMin`、`@Size`）
- 在 Controller 方法参数上添加 `@Valid` 或 `@Validated`

### 阶段四：测试与健壮性（中优先级）

**目标**：补充测试代码，提升系统健壮性

1. **后端单元测试**

- 使用 JUnit 5 + Mockito 为 Service 层编写测试
- 测试文件位置：`backend/src/test/java/com/warmisland/service/`
- 优先测试：ProductServiceImpl、HomeContentController 相关逻辑

2. **后端集成测试**

- 使用 `@SpringBootTest` + `@AutoConfigureMockMvc`
- 测试关键 API 接口

3. **前端组件测试**

- 配置 Vitest
- 为关键组件编写测试

4. **前端错误处理优化**

- 创建 `src/utils/errorHandler.js`：统一错误处理工具函数
- 分类处理网络错误、服务器错误、业务错误

### 阶段五：部署与文档（低优先级）

**目标**：完善部署配置和项目文档

1. **API 文档**

- 集成 SpringDoc OpenAPI（springdoc-openapi-starter-webmvc-ui）
- 访问 `/swagger-ui.html` 查看 API 文档

2. **数据库版本管理**

- 引入 Flyway 或 Liquibase
- 将 `DatabaseInitializer` 中的建表逻辑迁移到版本化 SQL 脚本

3. **Docker 部署支持**

- 后端新增 `Dockerfile`
- 新增 `docker-compose.yml`（定义 MySQL、后端应用、前端应用）

4. **CI/CD 配置**

- 新增 `.github/workflows/ci.yml`：自动化测试、构建

5. **日志配置**

- 新增 `backend/src/main/resources/logback-spring.xml`

6. **更新 README.md**

- 更新功能清单（将已完成项设为 `[x]`）
- 补充 API 文档链接
- 补充环境变量配置说明

## 设计风格

项目已采用治愈系、温暖的设计风格。后续新增页面（登录、个人中心、订单、结算）应保持一致的温暖治愈风格。

## 新增页面设计方向

- **登录/注册页**：温暖色调（奶油色、橙色），简洁表单，与首页风格一致
- **个人中心页**：订单列表、用户信息编辑，使用卡片式布局
- **结算页**：清晰的价格明细、地址表单，温暖但不喧宾夺主
- **订单详情页**：订单状态时间线、商品明细、操作按钮

## 现有页面优化

- Cart.vue：接入真实 Pinia store 后，优化空购物车状态和交互反馈
- Shop.vue：修复搜索功能，改为调用后端搜索API

## Agent Extensions

### SubAgent

- **code-explorer**
- Purpose: 在实施方案中需要深入探索代码库时调用（如重构时了解依赖关系）
- Expected outcome: 获取准确的代码结构信息，指导重构和新增功能