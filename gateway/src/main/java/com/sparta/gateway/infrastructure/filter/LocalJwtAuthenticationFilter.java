package com.sparta.gateway.infrastructure.filter;


import static com.sparta.gateway.application.exception.Error.FORBIDDEN;

import com.sparta.gateway.application.exception.GatewayException;
import com.sparta.gateway.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "filter")
@Component
@RequiredArgsConstructor
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    // 사용자 헤더 id 변수
    public static final String  LOGIN_ID = "X-UserId";

    // 사용자 헤더 role 변수
    public static final String  ROLE = "X-Role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 헤더 초기화: 검증 전 기본값 설정
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(LOGIN_ID, "null")
                        .header(ROLE, "null")
                        .build())
                .build();

        if (path.equals("/auth/login") || path.equals("/auth/join")) {
            return chain.filter(exchange);
        }

        String token = jwtUtil.extractToken(exchange);

        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            throw new GatewayException(FORBIDDEN, HttpStatus.UNAUTHORIZED);
        }

        // 토큰 검증 성공 시 헤더 덮어쓰기
        Claims claims = jwtUtil.parseClaims(token);
        // 새 요청 객체를 만들어서 헤더를 설정하고, 기존 exchange의 요청으로 교체
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(LOGIN_ID, claims.getSubject())
                        .header(ROLE, claims.get("role", String.class))
                        .build())
                .build();

        return chain.filter(exchange);
    }


}