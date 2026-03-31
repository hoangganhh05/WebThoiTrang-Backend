package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.register(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            // Spring Security tự động đối chiếu thông tin trong Database
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Sinh Thẻ Bài Dấu Nhận (JWT Token) siêu bảo mật
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            String jwt = tokenProvider.generateToken(principal);

            // Đóng gói Token và Info trả về cho ReactJS
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            // Fix circular reference nếu có bằng cách chỉ trả về những thứ cần thiết
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", principal.getUser().getId());
            userInfo.put("username", principal.getUser().getUsername());
            userInfo.put("role", principal.getUser().getRole());
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Tài khoản hoặc mật khẩu không chính xác!");
        }
    }
}