package com.metacoding.order.usecase;

import com.metacoding.order.domain.order.Order;
import com.metacoding.order.domain.order.OrderItem;
import com.metacoding.order.repository.OrderRepository;
import com.metacoding.order.repository.OrderItemRepository;
import com.metacoding.order.adapter.*;
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
        // 1. 상품 재고 감소 
        ProductResponse.DTO product = new ProductResponse.DTO(
                                        productClient.decreaseQuantity(productId, quantity)
                                    );
                
        // 2. 주문 생성
        Order order = Order.create(userId, productId, quantity);
        Order savedOrder = orderRepository.save(order);

        // 3. 주문 아이템 생성 
        OrderItem orderItem = OrderItem.create(
            savedOrder.getId(),
            productId,
            quantity,
            product.price()
        );
        orderItemRepository.save(orderItem);

        // 4. 배달 생성
        DeliveryRequest.SaveDTO deliveryRequest = new DeliveryRequest.SaveDTO(savedOrder.getId(), "ADRESS 4");
        deliveryClient.saveDelivery(deliveryRequest);
        
        // 5. 주문 및 주문 아이템 완료
        savedOrder.complete();
        orderItem.complete();
        orderRepository.save(savedOrder);
        orderItemRepository.save(orderItem);

        return OrderResult.from(savedOrder);
    }

    public OrderResult findById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return OrderResult.from(order);
    }
}

