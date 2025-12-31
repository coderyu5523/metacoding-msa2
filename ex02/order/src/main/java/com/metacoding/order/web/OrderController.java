package com.metacoding.order.web;

import com.metacoding.order.usecase.*;
import com.metacoding.order.web.dto.*;
import com.metacoding.order.core.util.Resp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody CreateOrderRequest requestDTO, HttpServletRequest request) {
        // Gateway에서 전달한 userId 사용
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다");
        }
        
        OrderResult result = orderService.saveOrder(userId,requestDTO.productId(),requestDTO.quantity(),requestDTO.price());
        
        OrderResponse response = new OrderResponse(
            result.id(),
            result.userId(),
            result.productId(),
            result.quantity(),
            result.status(),
            result.createdAt(),
            result.updatedAt()
        );
        return Resp.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") int orderId) {
        OrderResult result = orderService.findById(orderId);
        OrderResponse response = new OrderResponse(
            result.id(),
            result.userId(),
            result.productId(),
            result.quantity(),
            result.status(),
            result.createdAt(),
            result.updatedAt()
        );
        return Resp.ok(response);
    }
}

