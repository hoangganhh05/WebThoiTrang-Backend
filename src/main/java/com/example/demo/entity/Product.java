package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product")
public class Product {
    @Id
//    id, name, price, quantity, category_id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sanPham;
    private double price;
    private int quantity;
    @Column(name = "image_url")
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {}

}
