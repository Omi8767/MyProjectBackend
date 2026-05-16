package com.project.backend.controller;

import com.project.backend.dto.OrderDTO;
import com.project.backend.entity.Order;
import com.project.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    //newly added

    @GetMapping("/{sellerId}/seller")
    public ResponseEntity<Map<String,Object>> getOrderBySellerId(@PathVariable Long sellerId){
        Map<String, Object> orderBySellerId = orderService.getOrderBySellerId(sellerId);
        return ResponseEntity.ok(orderBySellerId);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancleOrder(@PathVariable Long orderId){
        Order order = orderService.cancleOrder((orderId));
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(){
        return  orderService.getAllOrders();
    }

    @PutMapping("/{id}/status")
    public  ResponseEntity<?> updateOrder(@PathVariable Long id,@RequestParam String status){
        return  orderService.updateStatus(id,status);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String,Object>> getDashboard(){
        Map<String, Object> dashboard = orderService.getDashboard();
        return  ResponseEntity.ok(dashboard);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities(){
        List<String> allCities = orderService.getAllCities();
        return  ResponseEntity.ok(allCities);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Order>> filterOrders(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String city
    ){
        List<Order> filters = orderService.filters(from, to, city);
        return ResponseEntity.ok(filters);
    }
}

