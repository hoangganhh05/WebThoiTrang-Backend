package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // Tạm thời dùng userId kiểu số nguyên
    private String userName; // Lưu tên người đánh giá
    
    private Long productId; // Đánh giá cho SP nào

    private int rating; // Số sao từ 1 đến 5
    
    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
