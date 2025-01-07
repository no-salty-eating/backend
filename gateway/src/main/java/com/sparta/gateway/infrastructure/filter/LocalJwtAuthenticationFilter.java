package com.sparta.gateway.infrastructure.filter;


import com.sparta.gateway.infrastructure.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 헤더 초기화: 검증 전 기본값 설정
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-UserId", "null")
                        .header("X-Role", "null")
                        .build())
                .build();

        if (path.equals("/auth/logIn") || path.equals("/auth/signIn")) {
            return chain.filter(exchange);
        }

        String token = jwtUtil.extractToken(exchange);

        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            String errorMessage = "Unauthorized access: Invalid or missing token.";
            return writeErrorResponse(exchange, errorMessage);
        }

        // 토큰 검증 성공 시 헤더 덮어쓰기
        Claims claims = jwtUtil.parseClaims(token);
        // 새 요청 객체를 만들어서 헤더를 설정하고, 기존 exchange의 요청으로 교체
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-UserId", claims.getSubject())
                        .header("X-Role", claims.get("role", String.class))
                        .build())
                .build();

        return chain.filter(exchange);
    }

    // 401 에러 응답 본문에 오류 메시지를 작성하는 메서드
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, String message) {
        // 응답 본문을 설정
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = String.format("{\"error\": \"%s\"}", message);
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(responseBody.getBytes());

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}