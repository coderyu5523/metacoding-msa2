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
        System.out.println("[Orchestrator] OrderCreated 이벤트 수신: orderId=" + event.getOrderId());
        int orderId = event.getOrderId();
        
        // 워크플로우 상태 초기화 (주소 정보 포함)
        workflowStates.put(orderId, new WorkflowState(orderId, event.getAddress()));
        
        // 1. 상품 차감 명령 발행
        DecreaseProductCommand productCommand = new DecreaseProductCommand(
            orderId,
            event.getProductId(),
            event.getQuantity()
        );
        kafkaTemplate.send("decrease-product-command", productCommand);
        System.out.println("[Orchestrator] DecreaseProductCommand 발행: orderId=" + orderId);
    }
    
    // ProductDecreased 이벤트 수신
    @KafkaListener(topics = "product-decreased", groupId = "orchestrator-service")
    public void handleProductDecreased(ProductDecreased event) {
        System.out.println("[Orchestrator] ProductDecreased 이벤트 수신: orderId=" + event.getOrderId() + ", success=" + event.isSuccess());
        int orderId = event.getOrderId();
        WorkflowState state = workflowStates.get(orderId);
        
        if (state == null) {
            System.out.println("[Orchestrator] 경고: orderId=" + orderId + "에 대한 WorkflowState를 찾을 수 없습니다.");
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
            System.out.println("[Orchestrator] CreateDeliveryCommand 발행: orderId=" + orderId);
        } else {
            // 상품 차감 실패 시 워크플로우 실패 처리
            System.out.println("[Orchestrator] 상품 차감 실패: orderId=" + orderId);
            state.setFailed(true);
            workflowStates.remove(orderId);
        }
    }
    
    // DeliveryCreated 이벤트 수신
    @KafkaListener(topics = "delivery-created", groupId = "orchestrator-service")
    public void handleDeliveryCreated(DeliveryCreated event) {
        System.out.println("[Orchestrator] DeliveryCreated 이벤트 수신: orderId=" + event.getOrderId() + ", success=" + event.isSuccess());
        int orderId = event.getOrderId();
        WorkflowState state = workflowStates.get(orderId);
        
        if (state == null) {
            System.out.println("[Orchestrator] 경고: orderId=" + orderId + "에 대한 WorkflowState를 찾을 수 없습니다.");
            return;
        }
        
        if (event.isSuccess()) {
            state.setDeliveryCreated(true);
            
            // 모든 단계 완료 시 주문 완료 명령 발행
            if (state.isProductDecreased() && state.isDeliveryCreated()) {
                CompleteOrderCommand completeCommand = new CompleteOrderCommand(orderId);
                kafkaTemplate.send("complete-order-command", completeCommand);
                System.out.println("[Orchestrator] CompleteOrderCommand 발행: orderId=" + orderId);
                
                // 워크플로우 완료
                workflowStates.remove(orderId);
                System.out.println("[Orchestrator] 워크플로우 완료: orderId=" + orderId);
            } else {
                System.out.println("[Orchestrator] 아직 모든 단계가 완료되지 않음: orderId=" + orderId + ", productDecreased=" + state.isProductDecreased() + ", deliveryCreated=" + state.isDeliveryCreated());
            }
        } else {
            // 배달 생성 실패 시 워크플로우 실패 처리
            System.out.println("[Orchestrator] 배달 생성 실패: orderId=" + orderId);
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


