package com.sparta.coupon.infrastructure.config;

import static com.sparta.coupon.application.exception.Error.INVALID_HEADER;
import static com.sparta.coupon.application.exception.Error.NOT_FOUND_HEADER;
import static com.sparta.coupon.application.exception.Error.REQUIRED_HEADER;

import com.sparta.coupon.application.exception.HeaderException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserIdInterceptor implements HandlerInterceptor {
    private static final String USER_ID_HEADER = "X-Id";
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userIdStr = request.getHeader(USER_ID_HEADER);
        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new HeaderException(REQUIRED_HEADER, HttpStatus.BAD_REQUEST);
        }
        try {
            currentUserId.set(Long.parseLong(userIdStr));
            return true;
        } catch (NumberFormatException e) {
            throw new HeaderException(INVALID_HEADER, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        currentUserId.remove();
    }

    public static Long getCurrentUserId() {
        Long userId = currentUserId.get();
        if (userId == null) {
            throw new HeaderException(NOT_FOUND_HEADER, HttpStatus.NOT_FOUND);
        }
        return userId;
    }
}