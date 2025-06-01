package com.yourorg.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret:mysecret}")
    private String jwtSecret;

    // 인증 예외 경로 패턴
    private static final String[] IGNORE_PATHS = {
        "/api/public/",         // 공개 API
        "/api/user-info/login", // 로그인
        "/api/user-info/signup",// 회원가입
        "/health",              // 헬스체크
        "/actuator/health",      // 헬스체크
        "/webjars/swagger-ui/index.html",
        "/swagger-ui/index.html",
    };

    private boolean isIgnored(String path) {
        for (String prefix : IGNORE_PATHS) {
            if (path.equals(prefix) || path.startsWith(prefix + "/")) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 인증 예외 경로는 통과
        if (isIgnored(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("인증 헤더: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                System.out.println("JWT Valid. Subject: " + claims.getSubject());
                return chain.filter(exchange);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        System.out.println("인증 헤더 없음");
        // 인증 헤더 없음: 401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // 우선 순위 높게
    }
}
