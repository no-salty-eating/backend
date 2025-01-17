package com.sparta.product.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReqResLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ReqResLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        MDC.put("traceId", UUID.randomUUID().toString());

        final ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

        String uri = cachingRequestWrapper.getRequestURI();

        if (!uri.startsWith("/products")) {
            filterChain.doFilter(cachingRequestWrapper, contentCachingResponseWrapper);
            contentCachingResponseWrapper.copyBodyToResponse();
            MDC.clear();
            return;
        }

        String clientIp = getClientIp(request);

        logger.info("Request IP: {}", clientIp);
        logger.info("Request Method: {}", cachingRequestWrapper.getMethod());
        logger.info("Request URL: {}", cachingRequestWrapper.getRequestURL());

        StringBuilder headers = new StringBuilder();
        cachingRequestWrapper.getHeaderNames().asIterator().forEachRemaining(headerName ->
                headers.append(headerName).append(": ").append(cachingRequestWrapper.getHeader(headerName)).append("\n"));
        logger.info("Request Headers:\n{}", headers);

        filterChain.doFilter(cachingRequestWrapper, contentCachingResponseWrapper);

        String requestBody = new String(cachingRequestWrapper.getContentAsByteArray(), cachingRequestWrapper.getCharacterEncoding());
        logger.info("Request Body: \n{}", requestBody);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String responseBody = new String(contentCachingResponseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        logger.info("Response Status: {}", contentCachingResponseWrapper.getStatus());
        logger.info("Response Header - Authorization: {}", contentCachingResponseWrapper.getHeader("Authorization"));

        if (!responseBody.isEmpty()) {
            Object json = objectMapper.readValue(responseBody, Object.class);
            String prettyResponseBody = objectMapper.writeValueAsString(json);
            logger.info("Response Content: \n{}", prettyResponseBody);
        } else {
            logger.info("Response body is empty");
        }

        contentCachingResponseWrapper.copyBodyToResponse();

        MDC.clear();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
