package com.metacoding.order.usecase;

import com.metacoding.order.domain.order.*;
import com.metacoding.order.repository.*;
import com.metacoding.order.adapter.*;
import com.metacoding.order.core.handler.ex.Exception500;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {

    @Transactional
    public OrderResult saveOrder(int userId, int productId, int quantity, Long price) {

        // 보상 트랜잭션 실행 시 실행된 작업만 롤백 처리를 위해 변수 선언
        Boolean productDecreased = false;
        Boolean orderCreated = false;
        Boolean orderItemCreated = false;
        Boolean deliveryCreated = false;
        // 보상 트랜잭션에서 주문 정보 저장을 위해 변수 선언
        Order savedOrder = null;

        try {
            // 1. 상품 재고 감소 
            productClient.decreaseQuantity(productId, quantity);
            productDecreased = true;

            // 2. 주문 생성
            Order order = Order.create(userId, productId, quantity);
            savedOrder = orderRepository.save(order);
            orderCreated = true;

            // 3. 주문 아이템 생성 
            OrderItem orderItem = OrderItem.create(
                order.getId(),
                productId,
                quantity,
                price
            );
            orderItemRepository.save(orderItem);
            orderItemCreated = true;

            // 4. 배달 생성
            DeliveryRequest.SaveDTO deliveryRequest = new DeliveryRequest.SaveDTO(order.getId(), "Addr 4");
            deliveryClient.saveDelivery(deliveryRequest);
            deliveryCreated = true;
            
            // 5. 주문 완료
            savedOrder.complete();  // 더티 체킹으로 상태 저장

            return OrderResult.from(savedOrder);
            
        } catch (Exception e) {
            // 보상 트랜잭션 실행

            // 배달 취소
            if (deliveryCreated == true) {
                System.out.println("배달 취소");
                deliveryClient.cancelDelivery(savedOrder.getId());
            }
        
            // 상품 재고 복구
            if (productDecreased == true) {
                System.out.println("상품 재고 복구");
                productClient.increaseQuantity(productId, quantity);
            }

            // 주문 생성 중 오류가 발생하면 자동 롤백
            if (orderCreated == true || orderItemCreated == true) {
                System.out.println("자동 롤백");
            }
            
            throw new Exception500("주문 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public OrderResult findById(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderResult.from(order);
    }
}

