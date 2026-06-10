package com.warmisland.controller;

import com.warmisland.dto.Result;
import com.warmisland.entity.Order;
import com.warmisland.security.UserPrincipal;
import com.warmisland.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST /api/orders - 创建订单
     */
    @PostMapping
    public Result<Order> createOrder(@RequestBody Map<String, Object> body,
                                      Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();

        String shippingAddress = (String) body.get("shippingAddress");
        String recipientName = (String) body.get("recipientName");
        String recipientPhone = (String) body.get("recipientPhone");
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        if (items == null || items.isEmpty()) {
            return Result.error(400, "订单商品不能为空");
        }

        Order order = orderService.createOrder(userId, shippingAddress, recipientName, recipientPhone, items);
        return Result.success(order);
    }

    /**
     * GET /api/orders - 获取当前用户订单列表
     */
    @GetMapping
    public Result<List<Order>> listOrders(Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return Result.success(orderService.getOrdersByUserId(userId));
    }

    /**
     * GET /api/orders/{id} - 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        Order order = orderService.getOrderById(id, userId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    /**
     * PATCH /api/orders/{id}/status - 更新订单状态（管理员）
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (!orderService.updateOrderStatus(id, status)) {
            return Result.error(404, "订单不存在");
        }
        return Result.success();
    }
}
