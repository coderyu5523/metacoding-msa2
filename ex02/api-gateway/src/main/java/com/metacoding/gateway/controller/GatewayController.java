package com.metacoding.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import com.metacoding.gateway.service.GatewayService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    @RequestMapping("/api/orders/**")
    public ResponseEntity<?> routeOrder(HttpServletRequest request) throws IOException {
        return routeRequest(request, "order");
    }

    @RequestMapping("/api/users/**")
    public ResponseEntity<?> routeUser(HttpServletRequest request) throws IOException {
        return routeRequest(request, "user");
    }

    @RequestMapping("/api/products/**")
    public ResponseEntity<?> routeProduct(HttpServletRequest request) throws IOException {
        return routeRequest(request, "product");
    }

    @RequestMapping("/api/deliveries/**")
    public ResponseEntity<?> routeDelivery(HttpServletRequest request) throws IOException {
        return routeRequest(request, "delivery");
    }

    @RequestMapping("/login")
    public ResponseEntity<?> routeLogin(HttpServletRequest request) throws IOException {
        return routeRequest(request, "user");
    }

    private ResponseEntity<?> routeRequest(HttpServletRequest request, String serviceType) throws IOException {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            path = path + "?" + queryString;
        }
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        
        HttpHeaders headers = new HttpHeaders();
        String contentType = request.getContentType();
        if (contentType != null) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        }
        
        // 토큰에서 추출한 userId를 헤더에 담아서 각 서버에 전달
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null) {
            headers.set("X-User-Id", String.valueOf(userId));
        }
                
        String body = request.getContentLength() > 0 
            ? StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8) 
            : null;
        
        return gatewayService.forwardRequest(serviceType, path, method, headers, body);
    }
}
