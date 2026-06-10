package com.warmisland.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.warmisland.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order createOrder(Long userId, String shippingAddress,
                     String recipientName, String recipientPhone,
                     java.util.List<java.util.Map<String, Object>> items);

    List<Order> getOrdersByUserId(Long userId);

    Order getOrderById(Long orderId, Long userId);

    boolean updateOrderStatus(Long orderId, String status);
}
