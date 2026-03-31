package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;      // 1 - 5 sao
    private String comment;  // Nội dung bình luận
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "orders"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"orderItems", "reviews", "category"})
    private Product product;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
