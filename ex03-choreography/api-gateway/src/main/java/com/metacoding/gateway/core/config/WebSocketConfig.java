package com.metacoding.gateway.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Value("${gateway.services.order:http://order-service:8081}")
    private String orderServiceUrl;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String wsUrl = orderServiceUrl.replace("http://", "ws://").replace("https://", "wss://") + "/ws/orders";
        registry.addHandler(new WebSocketProxyConfig(java.net.URI.create(wsUrl)), "/api/ws/orders")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
