package com.warmisland.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warmisland.entity.Order;
import com.warmisland.entity.OrderItem;
import com.warmisland.entity.Product;
import com.warmisland.mapper.OrderItemMapper;
import com.warmisland.mapper.OrderMapper;
import com.warmisland.mapper.ProductMapper;
import com.warmisland.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    public OrderServiceImpl(OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, String shippingAddress,
                             String recipientName, String recipientPhone,
                             List<Map<String, Object>> items) {

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在: " + productId);
            }
            BigDecimal price = product.getPrice();
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setShippingAddress(shippingAddress);
        order.setRecipientName(recipientName);
        order.setRecipientPhone(recipientPhone);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        getBaseMapper().insert(order);

        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            Product product = productMapper.selectById(productId);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(productId);
            orderItem.setVariantId(item.containsKey("variantId") ? Long.valueOf(item.get("variantId").toString()) : null);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());
            orderItem.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);
        }

        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return getBaseMapper().selectByMap(Map.of("user_id", userId));
    }

    @Override
    public Order getOrderById(Long orderId, Long userId) {
        Order order = getBaseMapper().selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return null;
        }
        return order;
    }

    @Override
    public boolean updateOrderStatus(Long orderId, String status) {
        Order order = getBaseMapper().selectById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        getBaseMapper().updateById(order);
        return true;
    }
}
