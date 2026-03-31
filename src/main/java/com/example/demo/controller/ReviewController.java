package com.example.demo.controller;

import com.example.demo.entity.Review;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Lấy tất cả đánh giá cho 1 sản phẩm
    @GetMapping("/product/{productId}")
    public List<Review> getReviews(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    // Thêm đánh giá mới (yêu cầu đăng nhập)
    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.parseLong(body.get("userId").toString());
            Long productId = Long.parseLong(body.get("productId").toString());
            int rating = Integer.parseInt(body.get("rating").toString());
            String comment = body.get("comment").toString();

            Review review = reviewService.addReview(userId, productId, rating, comment);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi gửi đánh giá: " + e.getMessage());
        }
    }

    // Xóa đánh giá (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Đã xóa đánh giá!");
    }

    // API thống kê đơn giản cho Admin Dashboard
    @GetMapping("/stats/orders")
    public ResponseEntity<?> getOrderStats() {
        // Trả về JSON đơn giản để Admin Dashboard dùng
        return ResponseEntity.ok(Map.of("message", "Xem /api/orders để lấy thống kê"));
    }
}
