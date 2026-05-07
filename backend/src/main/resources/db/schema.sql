-- Warm Island Shop Database Schema

CREATE DATABASE IF NOT EXISTS `warm_island` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `warm_island`;

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

INSERT INTO `product_images` (`product_id`, `image_url`, `sort_order`)
SELECT p.`id`, p.`image`, 0
FROM `products` p
WHERE p.`image` IS NOT NULL
  AND p.`image` <> ''
  AND NOT EXISTS (
      SELECT 1 FROM `product_images` pi WHERE pi.`product_id` = p.`id`
  );
