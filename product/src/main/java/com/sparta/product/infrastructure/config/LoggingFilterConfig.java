package com.sparta.product.infrastructure.config;

import com.sparta.product.infrastructure.logging.ReqResLoggingFilter;
import lombok.Getter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoggingFilterConfig {
    @Bean
    public FilterRegistrationBean<ReqResLoggingFilter> loggingFilter() {
        FilterRegistrationBean<ReqResLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ReqResLoggingFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
