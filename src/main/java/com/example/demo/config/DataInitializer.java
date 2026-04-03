package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:ADMIN_PASS_CHUA_CAI_DAT}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // Tìm tài khoản mang tên admin
        User admin = userRepository.findFirstByUsername(adminUsername).orElse(new User());
        
        // Cập nhật lại toàn bộ thông tin (nếu có sẵn thì ghi đè, chưa có thì tạo mới)
        admin.setUsername(adminUsername);
        admin.setPassword(adminPassword); 
        admin.setRole("ADMIN"); // Quyền tối cao
        
        userRepository.save(admin);
        System.out.println("====== TẠO/CẬP NHẬT TÀI KHOẢN ADMIN THÀNH CÔNG! ======");
    }
}
