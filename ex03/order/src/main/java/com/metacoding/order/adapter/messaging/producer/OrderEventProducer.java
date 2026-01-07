package com.metacoding.order.adapter.messaging.producer;

import com.metacoding.order.adapter.messaging.message.OrderCreated;
import com.metacoding.order.adapter.messaging.message.OrderCancelled;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void publishOrderCreated(OrderCreated event) {
        System.out.println("[OrderService] OrderCreated 이벤트 발행: orderId=" + event.getOrderId());
        kafkaTemplate.send("order-created", event);
    }
    
    public void publishOrderCancelled(OrderCancelled event) {
        kafkaTemplate.send("order-cancelled", event);
    }
}

