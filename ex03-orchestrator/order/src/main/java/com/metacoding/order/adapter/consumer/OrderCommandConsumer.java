package com.metacoding.order.adapter.consumer;

import com.metacoding.order.adapter.message.CompleteOrderCommand;
import com.metacoding.order.adapter.websocket.OrderWebSocketService;
import com.metacoding.order.domain.order.Order;
import com.metacoding.order.repository.OrderRepository;
import com.metacoding.order.core.handler.ex.Exception404;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderCommandConsumer {
    private final OrderRepository orderRepository;
    private final OrderWebSocketService webSocketService;
    
    @Transactional
    @KafkaListener(topics = "complete-order-command", groupId = "order-service")
    public void handleCompleteOrderCommand(CompleteOrderCommand command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new Exception404("주문을 찾을 수 없습니다."));
        order.complete();
        
        // 웹소켓으로 주문 완료 알림 전송
        webSocketService.sendOrderCompleted(order.getId(), order.getUserId());
        System.out.println("order 완료 처리");
    }
}

