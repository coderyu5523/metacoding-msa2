package com.metacoding.order.adapter.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendOrderCompleted(int orderId, int userId) {
        String message = String.format("{\"orderId\":%d,\"message\":\"주문이 완료되었습니다.\"}", orderId);
        messagingTemplate.convertAndSend("/topic/orders/" + userId, message);
    }
}
