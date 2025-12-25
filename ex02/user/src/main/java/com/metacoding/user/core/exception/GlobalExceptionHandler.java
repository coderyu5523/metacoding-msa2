package com.metacoding.user.core.exception;

import com.metacoding.user.core.util.Resp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        
        // 로그인 관련 에러는 401 Unauthorized
        if (message != null && (message.contains("비밀번호") || message.contains("유저네임"))) {
            return Resp.fail(HttpStatus.UNAUTHORIZED, message);
        }
        
        // 기타 에러는 400 Bad Request
        return Resp.fail(HttpStatus.BAD_REQUEST, message != null ? message : "요청 처리 중 오류가 발생했습니다.");
    }
}

