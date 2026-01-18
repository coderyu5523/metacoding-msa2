package com.metacoding.order.adapter.consumer;

import com.metacoding.order.adapter.message.DeliveryCreated;
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
    @KafkaListener(topics = "delivery-created", groupId = "order-service")
    public void handleDeliveryCreated(DeliveryCreated event) {
        if (!event.isSuccess()) {
            // 배달 생성 실패 시 주문 취소 처리
            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new Exception404("주문을 찾을 수 없습니다."));
            order.cancel();
            System.out.println("order 취소 처리 (delivery 생성 실패)");
            return;
        }
        
        // 배달 생성 성공 시 주문 완료 처리
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new Exception404("주문을 찾을 수 없습니다."));
        order.complete();
        
        // 웹소켓으로 주문 완료 알림 전송
        webSocketService.sendOrderCompleted(order.getId(), order.getUserId());
    }
}

