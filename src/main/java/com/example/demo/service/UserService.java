package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // Kiểm tra nếu tên đăng nhập đã tồn tại
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Tên tài khoản đã tồn tại!");
        }
        // Mặc định đăng ký mới là role USER nếu chưa có
        if (user.getRole() == null) user.setRole("USER");
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // So sánh mật khẩu (tạm thời để plain text theo yêu cầu của bạn)
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        return user;
    }
}