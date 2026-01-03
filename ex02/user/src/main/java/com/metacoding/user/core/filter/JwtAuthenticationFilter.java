package com.metacoding.user.core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        // 내부 IP만 허용
        String clientIp = getClientIpAddress(request);
        if (!isInternalIp(clientIp)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "비정상적인 요청입니다.");
            return;
        }
        
        // X-User-Id 헤더만 검증 (토큰 인증 제거)
        String userIdHeader = request.getHeader("X-User-Id");
        
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다");
            return;
        }
        
        try {
            Integer userId = Integer.parseInt(userIdHeader);
            request.setAttribute("userId", userId);
            filterChain.doFilter(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 사용자 ID입니다");
            return;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/login") ||
               path.startsWith("/h2-console");
    }

    /**
     * 클라이언트의 실제 IP 주소를 가져옵니다.
     * X-Forwarded-For 헤더를 우선 확인하고, 없으면 RemoteAddr을 사용합니다.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For는 여러 IP가 콤마로 구분될 수 있음 (프록시 체인)
            // 첫 번째 IP가 원본 클라이언트 IP
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * IP 주소가 내부 IP 대역인지 확인합니다.
     * - 127.0.0.0/8 (localhost)
     * - 10.0.0.0/8 (private)
     * - 172.16.0.0/12 (private)
     * - 192.168.0.0/16 (private)
     */
    private boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        try {
            // IPv6 루프백 주소 처리
            if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")) {
                return true;
            }
            
            InetAddress inetAddress = InetAddress.getByName(ip);
            byte[] addressBytes = inetAddress.getAddress();
            
            // IPv4만 처리
            if (addressBytes.length != 4) {
                return false;
            }
            
            int firstOctet = addressBytes[0] & 0xFF;
            int secondOctet = addressBytes[1] & 0xFF;
            
            // 127.0.0.0/8 (localhost)
            if (firstOctet == 127) {
                return true;
            }
            
            // 10.0.0.0/8 (private)
            if (firstOctet == 10) {
                return true;
            }
            
            // 172.16.0.0/12 (private)
            if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) {
                return true;
            }
            
            // 192.168.0.0/16 (private)
            if (firstOctet == 192 && secondOctet == 168) {
                return true;
            }
            
            return false;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
