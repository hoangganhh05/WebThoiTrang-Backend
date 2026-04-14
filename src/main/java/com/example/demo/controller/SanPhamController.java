package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class SanPhamController {
    @Autowired
    private ProductRepository sanPhamRepository;

    @Autowired
    private Cloudinary cloudinary;

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
        return sanPhamRepository.findBySanPhamContaining(keyword);
    }


    @PostMapping
    public Product save(
            @RequestPart("product") Product product,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (product.getPrice() <= 0) {
            throw new RuntimeException("Giá Phải Lớn Hơn 0!");
        }
        if (product.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng không hợp lệ!");
        }

        try {
            if (file != null && !file.isEmpty()) {
                // Upload lên Cloudinary thay vì lưu local
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String url = uploadResult.get("url").toString();
                product.setImageUrl(url);
            }

            return sanPhamRepository.save(product);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage());
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
                    // Upload lên Cloudinary cho hàm update
                    Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    String url = uploadResult.get("url").toString();
                    sanPham.setImageUrl(url);
                }
                return sanPhamRepository.save(sanPham);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi cập nhật ảnh lên Cloudinary: " + e.getMessage());
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sanPhamRepository.deleteById(id);
    }
}