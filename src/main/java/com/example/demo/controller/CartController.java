package com.example.demo.controller;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Lấy giỏ hàng của User
    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });
    }

    // Thêm sản phẩm vào giỏ
    @PostMapping("/{userId}/add")
    @Transactional
    public Cart addToCart(@PathVariable Long userId, @RequestBody CartItem itemRequest) {
        Cart cart = getCart(userId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(itemRequest.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + itemRequest.getQuantity());
            cartItemRepository.save(item);
        } else {
            itemRequest.setCart(cart);
            cart.getItems().add(itemRequest);
            cartItemRepository.save(itemRequest);
        }
        
        return cartRepository.findByUserId(userId).get();
    }

    // Cập nhật số lượng (+ / -)
    @PutMapping("/{userId}/update/{productId}")
    @Transactional
    public Cart updateQuantity(@PathVariable Long userId, @PathVariable Long productId, @RequestParam int amount) {
        Cart cart = getCart(userId);
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(Math.max(1, item.getQuantity() + amount));
                    cartItemRepository.save(item);
                });
        return cartRepository.findByUserId(userId).get();
    }

    // Xóa một món
    @DeleteMapping("/{userId}/remove/{productId}")
    @Transactional
    public Cart removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> {
            if (item.getProductId().equals(productId)) {
                cartItemRepository.delete(item);
                return true;
            }
            return false;
        });
        return cartRepository.save(cart);
    }

    // Làm trống giỏ hàng sau khi đặt thành công
    @DeleteMapping("/{userId}/clear")
    @Transactional
    public void clearCart(@PathVariable Long userId) {
        Cart cart = getCart(userId);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
