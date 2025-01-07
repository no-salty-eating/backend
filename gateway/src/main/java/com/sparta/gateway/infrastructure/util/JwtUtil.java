package com.sparta.gateway.infrastructure.util;


import static com.sparta.gateway.application.exception.Error.EXPIRED_JWT_TOKEN;
import static com.sparta.gateway.application.exception.Error.INVALID_JWT_TOKEN;
import static com.sparta.gateway.application.exception.Error.INVALID_TOKEN_SIGNATURE;
import static com.sparta.gateway.application.exception.Error.NOT_FOUND_TOKEN;
import static com.sparta.gateway.application.exception.Error.UNSUPPORTED_JWT_TOKEN;

import com.sparta.gateway.application.exception.GatewayException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${service.jwt.secret-key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(7);
        }
        throw new GatewayException(NOT_FOUND_TOKEN, HttpStatus.NOT_FOUND);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            // 잘못된 JWT 서명
            throw new GatewayException(INVALID_TOKEN_SIGNATURE, HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰
            throw new GatewayException(EXPIRED_JWT_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
            throw new GatewayException(UNSUPPORTED_JWT_TOKEN, HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // 잘못된 JWT 토큰
            throw new GatewayException(INVALID_JWT_TOKEN, HttpStatus.BAD_REQUEST);
        }
    }
}
