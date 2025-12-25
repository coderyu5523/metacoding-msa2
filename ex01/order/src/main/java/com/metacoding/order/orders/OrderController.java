package com.metacoding.order.orders;

import org.springframework.http.ResponseEntity;
import com.metacoding.order.core.util.Resp;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody OrderRequest.SaveDTO requestDTO, @RequestAttribute("userId") Integer userId) {
        return Resp.ok(orderService.saveOrder(userId, requestDTO));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") int orderId) {
        return Resp.ok(orderService.findById(orderId));
    }
}
