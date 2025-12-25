package com.metacoding.order.service;

import com.metacoding.order.domain.Order;
import com.metacoding.order.adapter.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;

    @Transactional
    public OrderResult createOrder(OrderCommand command) {
        // 1. 주문 엔티티 생성
        Order order = Order.create(
            command.userId(),
            command.productId(),
            command.quantity()
        );
        
        // 2. 주문 저장
        Order savedOrder = orderRepository.save(order);
        
        // 3. 상품 재고 확인
        productClient.getProduct(savedOrder.getProductId(), savedOrder.getQuantity());

        // 4. 상품 재고 감소
        productClient.decreaseQuantity(savedOrder.getProductId(), savedOrder.getQuantity());

        // 5. 배달 생성
        DeliveryRequest.SaveDTO deliveryRequest = new DeliveryRequest.SaveDTO(
            savedOrder.getId(), 
            "Default Address"
        );
        deliveryClient.saveDelivery(deliveryRequest);

        // 6. 주문 완료
        savedOrder.complete();
        
        // 7. 최종 저장
        Order completedOrder = orderRepository.save(savedOrder);
        
        return OrderResult.from(completedOrder);
    }

    public OrderResult findOrderById(int id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderResult.from(order);
    }
}
