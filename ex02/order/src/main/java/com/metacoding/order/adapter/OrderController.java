package com.metacoding.order.adapter;

import com.metacoding.order.service.OrderCommand;
import com.metacoding.order.service.OrderResult;
import com.metacoding.order.service.OrderService;
import com.metacoding.order.core.util.Resp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> saveOrder(
            @RequestBody OrderRequest.SaveDTO requestDTO,
            HttpServletRequest request) {
        // Gateway에서 전달한 userId 사용
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다");
        }
        
        OrderCommand command = new OrderCommand(
            userId,
            requestDTO.productId(),
            requestDTO.quantity()
        );
        OrderResult result = orderService.createOrder(command);
        OrderResponse.DTO response = new OrderResponse.DTO(
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
        OrderResult result = orderService.findOrderById(orderId);
        OrderResponse.DTO response = new OrderResponse.DTO(
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
