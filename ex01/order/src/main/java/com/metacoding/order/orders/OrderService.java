package com.metacoding.order.orders;

import com.metacoding.order.adapter.*;
import com.metacoding.order.adapter.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;

    @Transactional
    public OrderResponse.DTO saveOrder(int userId, OrderRequest.SaveDTO requestDTO) {

        // 1. 상품 재고 확인 및 감소 
        productClient.getProduct(requestDTO.productId(), requestDTO.quantity());

        // 2. 상품 재고 감소
        productClient.decreaseQuantity(requestDTO.productId(), requestDTO.quantity());
                
        // 3. 주문 insert
        Order order = Order.create(
            userId,
            requestDTO.productId(),
            requestDTO.quantity()
        );

        // 4. 배달 생성
        DeliveryRequest.SaveDTO deliveryRequest = new DeliveryRequest.SaveDTO(order.getId(), "ADRESS 4");
        deliveryClient.saveDelivery(deliveryRequest);
        
        // 5. 주문 완료
        order.complete();
        Order savedOrder = orderRepository.save(order);

        return new OrderResponse.DTO(savedOrder);
    }

    public OrderResponse.DTO findById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return new OrderResponse.DTO(order);
    }
}
