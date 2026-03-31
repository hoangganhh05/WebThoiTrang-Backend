package com.example.demo.service;


import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(Order order) {
        // Duyệt qua danh sách lấy từ hàm getOrderItems() vừa tạo ở trên
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(order); // Gán ngược lại để lưu đúng foreign key order_id
            }
        }
        return orderRepository.save(order);
    }
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}