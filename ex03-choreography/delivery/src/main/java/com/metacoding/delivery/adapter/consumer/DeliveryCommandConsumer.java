package com.metacoding.delivery.adapter.consumer;

import com.metacoding.delivery.adapter.message.OrderCreated;
import com.metacoding.delivery.adapter.message.ProductDecreased;
import com.metacoding.delivery.adapter.message.DeliveryCreated;
import com.metacoding.delivery.adapter.message.IncreaseProductCommand;
import com.metacoding.delivery.usecase.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class DeliveryCommandConsumer {
    private final DeliveryService deliveryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    // OrderCreated 이벤트에서 받은 주소 정보를 임시 저장
    private final Map<Integer, String> orderAddressMap = new ConcurrentHashMap<>();
    
    @KafkaListener(topics = "order-created", groupId = "delivery-service")
    public void handleOrderCreated(OrderCreated event) {
        // 주문 생성 이벤트에서 주소 정보 저장
        orderAddressMap.put(event.getOrderId(), event.getAddress());
    }
    
    @Transactional
    @KafkaListener(topics = "product-decreased", groupId = "delivery-service")
    public void handleProductDecreased(ProductDecreased event) {
        if (!event.isSuccess()) {
            // 상품 차감 실패 시 처리할 것이 없음 (주문이 이미 실패)
            orderAddressMap.remove(event.getOrderId());
            return;
        }
        
        // 주소 정보 조회
        String address = orderAddressMap.get(event.getOrderId());
        if (address == null) {
            return;
        }
        
        try {
            // 배달 생성 처리
            var result = deliveryService.saveDelivery(event.getOrderId(), address);
            
            // 성공 이벤트 발행
            DeliveryCreated deliveryEvent = new DeliveryCreated(
                event.getOrderId(),
                result.id(),
                true
            );
            kafkaTemplate.send("delivery-created", deliveryEvent);
            orderAddressMap.remove(event.getOrderId());
        } catch (Exception e) {
            // 실패 이벤트 발행 및 보상 트랜잭션
            DeliveryCreated deliveryEvent = new DeliveryCreated(
                event.getOrderId(),
                0,
                false
            );
            kafkaTemplate.send("delivery-created", deliveryEvent);
            
            // 보상 트랜잭션: 상품 재고 복구
            IncreaseProductCommand compensateCommand = new IncreaseProductCommand(
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity()
            );
            kafkaTemplate.send("increase-product-command", compensateCommand);
            orderAddressMap.remove(event.getOrderId());
        }
    }
}

