package com.yourorg.grading.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import com.yourorg.grading.adapter.in.dto.JwtUserInfoDto;

public class JwtUtil {

    // userId와 role을 모두 반환
    public static JwtUserInfoDto getUserInfoFromToken(String token, String secretKey) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.getSubject(); // 일반적으로 sub에 userId 저장
        String role = claims.get("role", String.class); // role이라는 key로 저장되었다고 가정

        return new JwtUserInfoDto(userId, role);
    }
}
