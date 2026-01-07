package com.metacoding.product.adapter.messaging.consumer;

import com.metacoding.product.adapter.messaging.message.DecreaseProductCommand;
import com.metacoding.product.adapter.messaging.message.ProductDecreased;
import com.metacoding.product.usecase.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductCommandConsumer {
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    @KafkaListener(topics = "decrease-product-command", groupId = "product-service")
    public void handleDecreaseProductCommand(DecreaseProductCommand command) {
        System.out.println("[ProductService] DecreaseProductCommand 수신: orderId=" + command.getOrderId() + ", productId=" + command.getProductId() + ", quantity=" + command.getQuantity());
        try {
            // 상품 차감 처리 (원자적으로)
            productService.decreaseQuantity(command.getProductId(), command.getQuantity());
            
            // 성공 이벤트 발행
            ProductDecreased event = new ProductDecreased(
                command.getOrderId(),
                command.getProductId(),
                command.getQuantity(),
                true
            );
            kafkaTemplate.send("product-decreased", event);
            System.out.println("[ProductService] ProductDecreased 이벤트 발행 (성공): orderId=" + command.getOrderId());
        } catch (Exception e) {
            System.out.println("[ProductService] 상품 차감 실패: orderId=" + command.getOrderId() + ", error=" + e.getMessage());
            // 실패 이벤트 발행
            ProductDecreased event = new ProductDecreased(
                command.getOrderId(),
                command.getProductId(),
                command.getQuantity(),
                false
            );
            kafkaTemplate.send("product-decreased", event);
            System.out.println("[ProductService] ProductDecreased 이벤트 발행 (실패): orderId=" + command.getOrderId());
        }
    }
}


