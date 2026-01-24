package com.metacoding.gateway.core.config;

import com.metacoding.gateway.core.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeaders().getFirst("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        // 토큰 검증 및 userId 추출
        if (token != null && jwtUtil.validateToken(token)) {
            try {
                int userId = jwtUtil.getUserId(token);
                attributes.put("userId", userId);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        return false; // 토큰이 없거나 유효하지 않으면 연결 거부
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
    }
}
