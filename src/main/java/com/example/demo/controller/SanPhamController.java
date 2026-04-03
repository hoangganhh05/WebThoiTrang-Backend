package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class SanPhamController {
    @Autowired
    private ProductRepository sanPhamRepository;

    @GetMapping
    public List<Product> getProducts() {
        return sanPhamRepository.findAll();
    }
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam("keyword") String keyword) {
        // Gọi repository để tìm kiếm theo tên (trường sanPham) có chứa từ khóa
        return sanPhamRepository.findBySanPhamContaining(keyword);
    }


    @PostMapping
    public Product save(
            @RequestPart("product") Product product, // Spring Boot sẽ tự ép JSON thành Product!
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        // 1. Validate dữ liệu gốc
        if (product.getPrice() <= 0) {
            throw new RuntimeException("Giá Phải Lớn Hơn 0!");
        }
        if (product.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng không hợp lệ!");
        }

        try {
            // 2. Xử lý lưu ảnh vào thư mục uploads/img
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path uploadDir = Paths.get("uploads/img");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path filePath = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Gán đường dẫn ảnh
                product.setImageUrl("/uploads/img/" + fileName);
            }

            // 3. Lưu xuống DB
            return sanPhamRepository.save(product);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xử lý file ảnh: " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public Product update(
            @PathVariable Long id,
            @RequestPart("product") Product product,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Product sanPham = sanPhamRepository.findById(id).orElse(null);

        if (sanPham != null) {
            sanPham.setSanPham(product.getSanPham());
            sanPham.setPrice(product.getPrice());
            sanPham.setQuantity(product.getQuantity());
            if (product.getCategory() != null) {
                sanPham.setCategory(product.getCategory());
            }

            try {
                if (file != null && !file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path uploadDir = Paths.get("uploads/img");

                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }

                    Path filePath = uploadDir.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    sanPham.setImageUrl("/uploads/img/" + fileName);
                }
                return sanPhamRepository.save(sanPham);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi xử lý cập nhật file ảnh: " + e.getMessage());
            }
        }
        return null;
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sanPhamRepository.deleteById(id);
    }
}