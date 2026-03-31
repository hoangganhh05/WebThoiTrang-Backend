package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173") // Khớp với port React của bạn
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        try {
            Order newOrder = orderService.createOrder(order);
            // Thay vì trả về newOrder, ta chỉ trả về ID hoặc một Map đơn giản
            return ResponseEntity.ok("Đặt hàng thành công với ID: " + newOrder.getId());
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi ra console để debug
            return ResponseEntity.internalServerError().body("Lỗi Server: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Order> getAllOrders() {
        // API này dành cho trang Admin Dashboard [cite: 13]
        return orderService.findAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        return orderService.findByUserId(userId);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}