package com.metacoding.order.core.filter;

import com.metacoding.order.core.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        String token = jwtProvider.resolveToken(request);
        Integer userId = null;
        
        // API 게이트웨이에서 토큰 검증 후 userId 추출해서 각 서버로 전달하기 때문에 API Server만 토큰 추출, 다른 서버는 userId 헤더 사용
        // 토큰이 있으면 토큰에서 userId 추출
        if (token != null && jwtProvider.validateToken(token)) {
            userId = jwtProvider.getUserId(request);
        }
        
        // 토큰이 없거나 유효하지 않으면 X-User-Id 헤더 체크
        if (userId == null) {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                try {
                    userId = Integer.parseInt(userIdHeader);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 사용자 ID입니다");
                    return;
                }
            }
        }
        
        // userId가 있으면 요청 속성에 저장하고 필터 체인 진행
        if (userId != null) {
            request.setAttribute("userId", userId);
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/login") ||
               path.startsWith("/h2-console");
    }
}
