package com.metacoding.order.core.config;

import com.metacoding.order.core.filter.JwtAuthenticationFilter;
import com.metacoding.order.core.util.JwtProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtProvider jwtProvider) {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
