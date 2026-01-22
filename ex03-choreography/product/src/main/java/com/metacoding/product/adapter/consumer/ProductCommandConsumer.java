package com.metacoding.product.adapter.consumer;

import com.metacoding.product.adapter.message.*;
import com.metacoding.product.usecase.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCommandConsumer {
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    @KafkaListener(topics = "order-created", groupId = "product-service")
    public void handleOrderCreated(OrderCreated event) {
        try {
            // 상품 차감 처리 
            productService.decreaseQuantity(event.getProductId(), event.getQuantity());
            
            // 성공 이벤트 발행
            ProductDecreased productDecreasedEvent = new ProductDecreased(
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity(),
                true
            );
            kafkaTemplate.send("product-decreased", productDecreasedEvent);
        } catch (Exception e) {
            log.error("상품 차감 실패", e);
            // 실패 이벤트 발행
            ProductDecreased productDecreasedEvent = new ProductDecreased(
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity(),
                false
            );
            kafkaTemplate.send("product-decreased", productDecreasedEvent);
        }
    }
    
    @Transactional
    @KafkaListener(topics = "increase-product-command", groupId = "product-service")
    public void handleIncreaseProductCommand(IncreaseProductCommand command) {
        try {
            // 상품 재고 복구 처리
            productService.increaseQuantity(command.getProductId(), command.getQuantity());
        } catch (Exception e) {
            log.error("상품 재고 복구 실패", e);
            // 보상 트랜잭션 실패는 심각한 문제이므로 로깅만 수행
            // 필요시 알림 시스템에 전송할 수 있음
        }
    }
}



