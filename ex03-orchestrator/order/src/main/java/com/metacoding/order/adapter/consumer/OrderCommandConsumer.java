package com.metacoding.order.adapter.consumer;

import com.metacoding.order.adapter.message.CompleteOrderCommand;
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
    
    @Transactional
    @KafkaListener(topics = "complete-order-command", groupId = "order-service")
    public void handleCompleteOrderCommand(CompleteOrderCommand command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new Exception404("주문을 찾을 수 없습니다."));
        order.complete();
        System.out.println("order 완료 처리");
    }
}

