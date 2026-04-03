package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy vé JWT từ thẻ Gửi Hàng (Request Header)
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromJWT(jwt);

                // Nạp thông tin Khách Hàng vào RAM của Security Context
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                CustomUserDetails customUser = (CustomUserDetails) userDetails;
                
                // KIỂM TRA ĐỘC QUYỀN 1 THIẾT BỊ (Phát hiện đăng nhập chỗ khác)
                if(customUser.getUser().getCurrentToken() == null || !customUser.getUser().getCurrentToken().equals(jwt)) {
                    throw new RuntimeException("Phát hiện Token cũ! Tài khoản này đã đăng nhập ở thiết bị khác.");
                }
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Báo cho Cảnh Sát Trưởng (Spring Security) là "Thằng này Đã Duyệt"
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            System.out.println("Lỗi Màng Lọc Lớp JwtAuthFilter: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Nhận diện chuẩn OAuth2 (Bearer + Khoảng trắng + Token)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
