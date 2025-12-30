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
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;

    @Transactional
    public OrderResult createOrder(int userId, int productId, int quantity) {
        ProductResponse.DTO product = null;
        Order savedOrder = null;
        OrderItem orderItem = null;
        boolean deliveryCreated = false;

        try {
            // 1. 상품 재고 감소 
            product = new ProductResponse.DTO(
                productClient.decreaseQuantity(productId, quantity)
            );
                    
            // 2. 주문 생성
            Order order = Order.create(userId, productId, quantity);
            savedOrder = orderRepository.save(order);

            // 3. 주문 아이템 생성 
            orderItem = OrderItem.create(
                savedOrder.getId(),
                productId,
                quantity,
                product.price()
            );
            orderItemRepository.save(orderItem);

            // 4. 배달 생성
            DeliveryRequest.SaveDTO deliveryRequest = new DeliveryRequest.SaveDTO(savedOrder.getId(), "ADRESS 4");
            deliveryClient.saveDelivery(deliveryRequest);
            deliveryCreated = true;
            
            // 5. 주문 및 주문 아이템 완료
            savedOrder.complete();
            orderItem.complete();
            orderRepository.save(savedOrder);
            orderItemRepository.save(orderItem);

            return OrderResult.from(savedOrder);
            
        } catch (Exception e) {
            // 보상 트랜잭션 실행 (역순으로 롤백)

            // 4. 배달 취소 (배달이 생성된 경우)
            if (deliveryCreated && savedOrder != null) {
                System.out.println("배달 취소");
                deliveryClient.cancelDelivery(savedOrder.getId());
            }
            
            // 3. 주문 아이템 삭제
            if (orderItem != null) {
                System.out.println("주문 아이템 삭제");
                orderItemRepository.delete(orderItem);
            }
            
            // 2. 주문 취소
            if (savedOrder != null) {
                System.out.println("주문 취소");
                savedOrder.cancel();
                orderRepository.save(savedOrder);
            }
            
            // 1. 상품 재고 복구
            if (product != null) {
                System.out.println("상품 재고 복구");
                productClient.increaseQuantity(productId, quantity);
            }
            
            throw new Exception500("주문 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public OrderResult findById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderResult.from(order);
    }
}

