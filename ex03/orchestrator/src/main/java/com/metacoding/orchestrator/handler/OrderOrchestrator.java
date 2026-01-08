package com.metacoding.orchestrator.handler;

import com.metacoding.orchestrator.message.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderOrchestrator {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // 워크플로우 상태 관리 (orderId -> 상태)
    private final Map<Integer, WorkflowState> workflowStates = new ConcurrentHashMap<>();
    
    // OrderCreated 이벤트 수신
    @KafkaListener(topics = "order-created", groupId = "orchestrator-service")
    public void handleOrderCreated(OrderCreated event) {
        int orderId = event.getOrderId();
        
        // 워크플로우 상태 초기화 (주소 정보 포함)
        WorkflowState state = new WorkflowState(event.getAddress());
        state.setProductId(event.getProductId());
        state.setQuantity(event.getQuantity());
        workflowStates.put(orderId, state);
        
        // 1. 상품 차감 명령 발행
        DecreaseProductCommand productCommand = new DecreaseProductCommand(
            orderId,
            event.getProductId(),
            event.getQuantity()
        );
        kafkaTemplate.send("decrease-product-command", productCommand);
        System.out.println("order 이벤트 수신");
    }
    
    // ProductDecreased 이벤트 수신
    @KafkaListener(topics = "product-decreased", groupId = "orchestrator-service")
    public void handleProductDecreased(ProductDecreased event) {
        int orderId = event.getOrderId();
        WorkflowState state = workflowStates.get(orderId);
        
        if (state == null) {
            return;
        }
        
        if (event.isSuccess()) {
            state.setProductDecreased(true);
            
            // 상품 차감 성공 시 배달 생성 명령 발행
            CreateDeliveryCommand deliveryCommand = new CreateDeliveryCommand(
                orderId,
                state.getAddress()
            );
            kafkaTemplate.send("create-delivery-command", deliveryCommand);
            System.out.println("product 이벤트 수신");
        } else {
            // 상품 차감 실패 시 워크플로우 실패 처리 (보상 트랜잭션 불필요 - 아직 차감되지 않음)
            workflowStates.remove(orderId);
        }
    }
    
    // DeliveryCreated 이벤트 수신
    @KafkaListener(topics = "delivery-created", groupId = "orchestrator-service")
    public void handleDeliveryCreated(DeliveryCreated event) {
        int orderId = event.getOrderId();
        WorkflowState state = workflowStates.get(orderId);
        
        if (state == null) {
            return;
        }
        
        if (event.isSuccess()) {
            state.setDeliveryCreated(true);
            
            // 모든 단계 완료 시 주문 완료 명령 발행
            if (state.isProductDecreased() && state.isDeliveryCreated()) {
                CompleteOrderCommand completeCommand = new CompleteOrderCommand(orderId);
                kafkaTemplate.send("complete-order-command", completeCommand);
                
                // 워크플로우 완료
                workflowStates.remove(orderId);
                System.out.println("order 완료");
            }
        } else {
            // 배달 생성 실패 시 보상 트랜잭션 수행
            // 상품 재고 복구 (이미 차감된 경우)
            if (state.isProductDecreased()) {
                IncreaseProductCommand compensateProductCommand = new IncreaseProductCommand(
                    orderId,
                    state.getProductId(),
                    state.getQuantity()
                );
                kafkaTemplate.send("increase-product-command", compensateProductCommand);
                System.out.println("delivery 이벤트 수신 실패, 보상 트랜잭션 시작");
            }
            
            workflowStates.remove(orderId);
        }
    }
    
    // 워크플로우 상태 관리 클래스
    private static class WorkflowState {
        private final String address;
        private boolean productDecreased = false;
        private boolean deliveryCreated = false;
        private int productId;
        private int quantity;
        
        public WorkflowState(String address) {
            this.address = address;
        }
        
        public String getAddress() {
            return address;
        }
        
        public boolean isProductDecreased() {
            return productDecreased;
        }
        
        public void setProductDecreased(boolean productDecreased) {
            this.productDecreased = productDecreased;
        }
        
        public boolean isDeliveryCreated() {
            return deliveryCreated;
        }
        
        public void setDeliveryCreated(boolean deliveryCreated) {
            this.deliveryCreated = deliveryCreated;
        }
        
        public int getProductId() {
            return productId;
        }
        
        public void setProductId(int productId) {
            this.productId = productId;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}


