package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Chuỗi bí mật ký Token (PHẢI GIỮ BÍ MẬT 100%)
    private final String JWT_SECRET = "DayLaChuoiBaoMatCucKyDaiCuaDuAnSpringBootReactJSKiemTraDoAnTotNghiep2026S-StyleSuperSecretKey";
    
    // Thời gian sống: 7 ngày (604800000 ms)
    private final long JWT_EXPIRATION = 604800000L; 
    
    private final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    // Tạo mã vé (Token)
    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getUser().getId())) // ID Khách
                .claim("username", userDetails.getUsername()) // Tên đăng nhập
                .claim("role", userDetails.getUser().getRole()) // Quyền hiển thị
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512) // Ký bằng mã HmacSHA512
                .compact();
    }

    // Lấy ID Khách từ vé
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }

    // Soi vé xe (Kiểm tra vé mạo danh, vé hết hạn)
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            System.out.println("Lỗi xác thực Token: " + ex.getMessage());
            return false;
        }
    }
}
