package com.project.backend.controller;

import com.project.backend.dto.OrderDTO;
import com.project.backend.entity.Order;
import com.project.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderDTO orderDTO){
        return orderService.placeOrder(orderDTO);
    }

    @GetMapping("/{customerId}/customer")
    public ResponseEntity<?> getOrderByCustomerId(@PathVariable Long customerId){
        return orderService.getOrderByCustomerId(customerId);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancleOrder(@PathVariable Long orderId){
        Order order = orderService.cancleOrder((orderId));
        return ResponseEntity.ok(order);
    }
}
