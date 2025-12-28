package com.metacoding.order.usecase;

import com.metacoding.order.domain.order.Order;
import com.metacoding.order.repository.OrderRepository;
import com.metacoding.order.adapter.DeliveryClient;
import com.metacoding.order.adapter.DeliveryRequest;
import com.metacoding.order.adapter.ProductClient;
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
    public OrderResult createOrder(int userId, int productId, int quantity) {
        // 1. 상품 재고 확인 및 감소 
        productClient.getProduct(productId, quantity);

        // 2. 상품 재고 감소
        productClient.decreaseQuantity(productId, quantity);
                
        // 3. 주문 insert
        Order order = Order.create(userId, productId, quantity);

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

