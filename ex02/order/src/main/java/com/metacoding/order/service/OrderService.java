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
        // 1. 상품 재고 확인 및 감소 
        productClient.getProduct(command.productId(), command.quantity());

        // 2. 상품 재고 감소
        productClient.decreaseQuantity(command.productId(), command.quantity());
                
        // 3. 주문 insert
        Order order = Order.create(
            command.userId(),
            command.productId(),
            command.quantity()
        );

        // 4. 배달 생성
        DeliveryRequest.SaveDTO deliveryRequest = new DeliveryRequest.SaveDTO(order.getId(), "Default Address");
        deliveryClient.saveDelivery(deliveryRequest);
        
        // 5. 주문 완료
        order.complete();
        Order savedOrder = orderRepository.save(order);

        return OrderResult.from(savedOrder);
    }

    public OrderResult findById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderResult.from(order);
    }
}
