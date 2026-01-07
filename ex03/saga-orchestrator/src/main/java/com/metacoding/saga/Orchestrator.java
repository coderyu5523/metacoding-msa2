package com.metacoding.saga;

import com.metacoding.saga.event.*;
import com.metacoding.saga.command.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Orchestrator {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // 워크플로우 상태 관리 (orderId -> 상태)
    private final Map<Integer, WorkflowState> workflowStates = new ConcurrentHashMap<>();
    
    private static final String DECREASE_PRODUCT_COMMAND_TOPIC = "decrease-product-command";
    private static final String CREATE_DELIVERY_COMMAND_TOPIC = "create-delivery-command";
    private static final String COMPLETE_ORDER_COMMAND_TOPIC = "complete-order-command";
    
    // OrderCreated 이벤트 수신
    @KafkaListener(topics = "order-created", groupId = "orchestrator")
    public void handleOrderCreated(OrderCreated event) {
        int orderId = event.getOrderId();
        
        // 워크플로우 상태 초기화 (주소 정보 포함)
        workflowStates.put(orderId, new WorkflowState(orderId, event.getAddress()));
        
        // 1. 상품 차감 명령 발행
        DecreaseProductCommand productCommand = new DecreaseProductCommand(
            orderId,
            event.getProductId(),
            event.getQuantity()
        );
        kafkaTemplate.send(DECREASE_PRODUCT_COMMAND_TOPIC, productCommand);
    }
    
    // ProductDecreased 이벤트 수신
    @KafkaListener(topics = "product-decreased", groupId = "orchestrator")
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
            kafkaTemplate.send(CREATE_DELIVERY_COMMAND_TOPIC, deliveryCommand);
        } else {
            // 상품 차감 실패 시 워크플로우 실패 처리
            state.setFailed(true);
            workflowStates.remove(orderId);
        }
    }
    
    // DeliveryCreated 이벤트 수신
    @KafkaListener(topics = "delivery-created", groupId = "orchestrator")
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
                kafkaTemplate.send(COMPLETE_ORDER_COMMAND_TOPIC, completeCommand);
                
                // 워크플로우 완료
                workflowStates.remove(orderId);
            }
        } else {
            // 배달 생성 실패 시 워크플로우 실패 처리
            state.setFailed(true);
            workflowStates.remove(orderId);
        }
    }
    
    // 워크플로우 상태 관리 클래스
    private static class WorkflowState {
        private final int orderId;
        private final String address;
        private boolean productDecreased = false;
        private boolean deliveryCreated = false;
        private boolean failed = false;
        
        public WorkflowState(int orderId, String address) {
            this.orderId = orderId;
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
        
        public boolean isFailed() {
            return failed;
        }
        
        public void setFailed(boolean failed) {
            this.failed = failed;
        }
    }
}

