-- Warm Island Shop Database Schema

CREATE DATABASE IF NOT EXISTS `warm_island` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `warm_island`;

CREATE TABLE IF NOT EXISTS `product_categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` VARCHAR(20) NOT NULL DEFAULT '启用' COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_name` (`name`),
    INDEX `idx_category_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `name_en` VARCHAR(200) DEFAULT NULL COMMENT '英文名称',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '原价',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '图片路径',
    `category` VARCHAR(50) NOT NULL COMMENT '分类',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签，逗号分隔',
    `rating` DOUBLE DEFAULT 5.0 COMMENT '评分',
    `sold_count` INT DEFAULT 0 COMMENT '销量',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `status` VARCHAR(20) NOT NULL DEFAULT '销售中' COMMENT '商品状态',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_sold_count` (`sold_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

CREATE TABLE IF NOT EXISTS `product_images` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片地址',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_product_sort` (`product_id`, `sort_order`),
    CONSTRAINT `fk_product_images_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

CREATE TABLE IF NOT EXISTS `product_detail_entries` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品详情项ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `section_type` VARCHAR(30) NOT NULL COMMENT '区块类型：detail/spec/service/notice',
    `layout_type` VARCHAR(30) NOT NULL DEFAULT 'text' COMMENT '排版类型：text/image_text/image/highlight',
    `title` VARCHAR(200) DEFAULT NULL COMMENT '标题或参数名',
    `content` TEXT DEFAULT NULL COMMENT '内容或参数值',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片地址',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` VARCHAR(20) NOT NULL DEFAULT '启用' COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_product_detail_section_sort` (`product_id`, `section_type`, `sort_order`),
    CONSTRAINT `fk_product_detail_entries_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品详情装修项表';

CREATE TABLE IF NOT EXISTS `product_variants` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '款式ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '款式名称',
    `sku_code` VARCHAR(100) DEFAULT NULL COMMENT '款式编码',
    `price` DECIMAL(10, 2) DEFAULT NULL COMMENT '款式售价',
    `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '款式原价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '款式库存',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '款式图片',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` VARCHAR(20) NOT NULL DEFAULT '启用' COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_product_variant_sort` (`product_id`, `sort_order`),
    INDEX `idx_product_variant_status` (`status`),
    CONSTRAINT `fk_product_variants_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品款式表';

CREATE TABLE IF NOT EXISTS `home_sections` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '内容位ID',
    `code` VARCHAR(50) NOT NULL COMMENT '内容位编码',
    `eyebrow` VARCHAR(100) DEFAULT NULL COMMENT '眉标',
    `title` VARCHAR(200) DEFAULT NULL COMMENT '标题',
    `subtitle` VARCHAR(300) DEFAULT NULL COMMENT '副标题',
    `body` TEXT DEFAULT NULL COMMENT '正文',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片地址',
    `link_text` VARCHAR(100) DEFAULT NULL COMMENT '按钮文案',
    `link_url` VARCHAR(300) DEFAULT NULL COMMENT '按钮链接',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` VARCHAR(20) NOT NULL DEFAULT '启用' COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_home_section_code` (`code`),
    INDEX `idx_home_section_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页内容位表';

CREATE TABLE IF NOT EXISTS `home_section_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '内容项ID',
    `section_code` VARCHAR(50) NOT NULL COMMENT '内容位编码',
    `item_type` VARCHAR(50) NOT NULL COMMENT '内容项类型',
    `title` VARCHAR(200) DEFAULT NULL COMMENT '标题',
    `subtitle` VARCHAR(300) DEFAULT NULL COMMENT '副标题',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片地址',
    `link_text` VARCHAR(100) DEFAULT NULL COMMENT '链接文案',
    `link_url` VARCHAR(300) DEFAULT NULL COMMENT '链接地址',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` VARCHAR(20) NOT NULL DEFAULT '启用' COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_home_item_section_sort` (`section_code`, `sort_order`),
    INDEX `idx_home_item_status` (`status`),
    CONSTRAINT `fk_home_items_section`
        FOREIGN KEY (`section_code`) REFERENCES `home_sections` (`code`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页内容项表';

-- Seed data
INSERT INTO `products` (`name`, `name_en`, `price`, `original_price`, `image`, `category`, `description`, `tags`, `rating`, `sold_count`, `stock`, `status`) VALUES
('暖屿咖啡', 'Warm Island Coffee', 38.00, 48.00, '/products/coffee.jpg', '饮品', '精选阿拉比卡豆，暖屿特调配方，每一杯都是温暖的岛屿时光', '热销,招牌', 4.9, 1280, 86, '销售中'),
('海岛抹茶拿铁', 'Island Matcha Latte', 42.00, NULL, '/products/matcha.jpg', '饮品', '宇治抹茶搭配鲜奶，带来海岛风情的清新体验', '新品', 4.7, 560, 42, '销售中'),
('椰香芒果慕斯', 'Coconut Mango Mousse', 35.00, NULL, '/products/mousse.jpg', '甜点', '热带芒果与椰奶的完美融合，入口即化的绵密口感', '人气', 4.8, 920, 28, '销售中'),
('暖屿曲奇礼盒', 'Warm Island Cookie Box', 68.00, 88.00, '/products/cookies.jpg', '零食', '手工烘焙黄油曲奇，海岛风味的甜蜜馈赠', '礼盒,热销', 4.9, 2100, 64, '销售中'),
('海岛冰摇柠檬茶', 'Island Lemon Shaken Tea', 28.00, NULL, '/products/lemon-tea.jpg', '饮品', '新鲜柠檬搭配茉莉花茶，冰摇出夏日海岛的清爽', '清爽', 4.6, 780, 95, '销售中'),
('海盐焦糖蛋糕', 'Sea Salt Caramel Cake', 45.00, NULL, '/products/cake.jpg', '甜点', '海盐与焦糖的碰撞，每一口都是惊喜的味道层次', '招牌', 4.8, 1050, 33, '销售中'),
('暖屿帆布袋', 'Warm Island Tote Bag', 58.00, NULL, '/products/tote.jpg', '周边', '原创海岛插画设计，纯棉帆布材质，实用又好看', '周边', 4.7, 650, 58, '已下架'),
('海岛香薰蜡烛', 'Island Scented Candle', 78.00, 98.00, '/products/candle.jpg', '周边', '热带花香与木质调的融合，点燃就是海岛度假的感觉', '新品,礼盒', 4.9, 430, 24, '销售中');

INSERT INTO `product_categories` (`name`, `sort_order`, `status`)
SELECT category, ROW_NUMBER() OVER (ORDER BY category), '启用'
FROM (
    SELECT DISTINCT category
    FROM `products`
    WHERE deleted = 0
      AND category IS NOT NULL
      AND category <> ''
) source_categories
WHERE NOT EXISTS (
    SELECT 1 FROM `product_categories` pc WHERE pc.`name` = source_categories.category
);

INSERT INTO `product_images` (`product_id`, `image_url`, `sort_order`)
SELECT p.`id`, p.`image`, 0
FROM `products` p
WHERE p.`image` IS NOT NULL
  AND p.`image` <> ''
  AND NOT EXISTS (
      SELECT 1 FROM `product_images` pi WHERE pi.`product_id` = p.`id`
  );

INSERT INTO `product_detail_entries`
(`product_id`, `section_type`, `layout_type`, `title`, `content`, `image_url`, `sort_order`, `status`)
SELECT p.`id`, 'detail', 'image_text', '商品亮点', p.`description`, p.`image`, 1, '启用'
FROM `products` p
WHERE p.`deleted` = 0
  AND p.`description` IS NOT NULL
  AND p.`description` <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM `product_detail_entries` pde
      WHERE pde.`product_id` = p.`id`
        AND pde.`section_type` = 'detail'
  );

INSERT INTO `product_detail_entries`
(`product_id`, `section_type`, `layout_type`, `title`, `content`, `image_url`, `sort_order`, `status`)
SELECT p.`id`, detail_defaults.`section_type`, detail_defaults.`layout_type`, detail_defaults.`title`, detail_defaults.`content`, NULL, detail_defaults.`sort_order`, '启用'
FROM `products` p
JOIN (
    SELECT 'spec' section_type, 'text' layout_type, '商品分类' title, '__CATEGORY__' content, 10 sort_order
    UNION ALL SELECT 'spec', 'text', '商品编号', '__SKU__', 20
    UNION ALL SELECT 'spec', 'text', '库存状态', '__STOCK__', 30
    UNION ALL SELECT 'service', 'highlight', '实物拍摄', '商品图片与详情页信息按当前在售商品维护，图片按后台排序展示。', 40
    UNION ALL SELECT 'service', 'highlight', '用心包装', '默认使用暖屿包装物料，适合自留、下午茶和日常送礼。', 50
    UNION ALL SELECT 'notice', 'text', '库存说明', '商品状态或库存变化，以提交订单时的页面与后台数据为准。', 60
    UNION ALL SELECT 'notice', 'text', '售后说明', '如收到商品后有疑问，请保留包装与订单信息便于处理。', 70
) detail_defaults
WHERE p.`deleted` = 0
  AND NOT EXISTS (
      SELECT 1
      FROM `product_detail_entries` pde
      WHERE pde.`product_id` = p.`id`
        AND pde.`section_type` = detail_defaults.`section_type`
        AND pde.`title` = detail_defaults.`title`
  );

UPDATE `product_detail_entries` pde
JOIN `products` p ON pde.`product_id` = p.`id`
SET pde.`content` = p.`category`
WHERE pde.`content` = '__CATEGORY__';

UPDATE `product_detail_entries` pde
SET pde.`content` = CONCAT('WI-', LPAD(pde.`product_id`, 5, '0'))
WHERE pde.`content` = '__SKU__';

UPDATE `product_detail_entries` pde
JOIN `products` p ON pde.`product_id` = p.`id`
SET pde.`content` = CONCAT(p.`stock`, ' 件')
WHERE pde.`content` = '__STOCK__';

INSERT INTO `product_variants`
(`product_id`, `name`, `sku_code`, `price`, `original_price`, `stock`, `image_url`, `sort_order`, `status`)
SELECT p.`id`, '默认款', CONCAT('WI-', LPAD(p.`id`, 5, '0'), '-DEFAULT'), p.`price`, p.`original_price`, p.`stock`, p.`image`, 1, '启用'
FROM `products` p
WHERE p.`deleted` = 0
  AND NOT EXISTS (
      SELECT 1 FROM `product_variants` pv WHERE pv.`product_id` = p.`id`
  );

INSERT INTO `home_sections`
(`code`, `eyebrow`, `title`, `subtitle`, `body`, `image_url`, `link_text`, `link_url`, `sort_order`, `status`)
SELECT * FROM (
    SELECT 'hero' code, '暖屿手作 · 治愈系小店' eyebrow, '把慢下来的\n温柔日常\n做成礼物' title, NULL subtitle,
        '我们在一间小小手作室里调配饮品、烘烤甜点、整理香气与布料。每一件暖屿好物，都想陪你把平凡的一天过得更柔软一点。' body,
        NULL image_url, NULL link_text, NULL link_url, 1 sort_order, '启用' status
    UNION ALL
    SELECT 'category_cards', '慢慢挑选', '暖屿今日手作', NULL,
        '从饮品香气到烘焙甜点，再到能带回家的小物，每一类都保留一点手作温度。',
        NULL, NULL, NULL, 2, '启用'
    UNION ALL
    SELECT 'featured', '被反复带走的温柔', '人气手作好物', NULL,
        '暖屿最受欢迎的几件小东西，适合自留，也适合送人。',
        NULL, '查看全部', '/shop', 3, '启用'
    UNION ALL
    SELECT 'about', '关于暖屿', '一间把温柔做进日常的小店', NULL,
        '暖屿手作从一张木桌开始。我们喜欢奶油色的纸袋、刚出炉的黄油香、手写标签和拆开包裹时那一点点被照顾到的心情。\n\n从一杯特调咖啡，到一块手作甜点；从一个帆布袋，到一支香薰蜡烛，每一件商品都用柔和颜色、自然材质和小批量制作，留住慢生活里的安静片刻。',
        '/products/tote.jpg', '了解我们的故事', '/about', 4, '启用'
    UNION ALL
    SELECT 'cta', NULL, '给今天留一份暖意', NULL,
        '挑一件喜欢的手作好物，让奶油色、木质香和一点点甜，陪你慢慢度过这一天。',
        NULL, '去逛逛', '/shop', 5, '启用'
) default_rows
WHERE NOT EXISTS (
    SELECT 1 FROM `home_sections` hs WHERE hs.`code` = default_rows.code
);

INSERT INTO `home_section_items`
(`section_code`, `item_type`, `title`, `subtitle`, `description`, `image_url`, `link_text`, `link_url`, `sort_order`, `status`)
SELECT * FROM (
    SELECT 'hero' section_code, 'button' item_type, '逛逛手作好物' title, NULL subtitle, NULL description, NULL image_url, NULL link_text, '/shop' link_url, 1 sort_order, '启用' status
    UNION ALL SELECT 'hero', 'button', '听听小店故事', NULL, NULL, NULL, NULL, '/about', 2, '启用'
    UNION ALL SELECT 'hero', 'hero_image', '暖屿曲奇礼盒', NULL, '今日出炉：黄油曲奇、海盐焦糖蛋糕、棉布小袋与一盏温柔香气。', '/products/cookies.jpg', NULL, NULL, 3, '启用'
    UNION ALL SELECT 'hero', 'small_image', '暖屿咖啡', NULL, NULL, '/products/coffee.jpg', NULL, NULL, 4, '启用'
    UNION ALL SELECT 'hero', 'small_image', '海盐焦糖蛋糕', NULL, NULL, '/products/cake.jpg', NULL, NULL, 5, '启用'
    UNION ALL SELECT 'hero', 'small_image', '海岛香薰蜡烛', NULL, NULL, '/products/candle.jpg', NULL, NULL, 6, '启用'
    UNION ALL SELECT 'hero', 'float_image', '暖屿帆布袋', NULL, NULL, '/products/tote.jpg', NULL, NULL, 7, '启用'
    UNION ALL SELECT 'hero', 'float_image', '椰香芒果慕斯', NULL, NULL, '/products/mousse.jpg', NULL, NULL, 8, '启用'
    UNION ALL SELECT 'category_cards', 'category_card', '手冲与特调', '咖啡 · 抹茶 · 果茶', '适合给午后留一点空白', '/products/coffee.jpg', NULL, '/shop', 1, '启用'
    UNION ALL SELECT 'category_cards', 'category_card', '小炉烘焙', '慕斯 · 蛋糕 · 曲奇', '小批量制作，甜度刚刚好', '/products/cookies.jpg', NULL, '/shop', 2, '启用'
    UNION ALL SELECT 'category_cards', 'category_card', '温柔周边', '帆布袋 · 香薰 · 文创', '把小店气味带回日常', '/products/candle.jpg', NULL, '/shop', 3, '启用'
) default_rows
WHERE NOT EXISTS (
    SELECT 1
    FROM `home_section_items` hsi
    WHERE hsi.`section_code` = default_rows.section_code
      AND hsi.`item_type` = default_rows.item_type
      AND hsi.`title` = default_rows.title
);
