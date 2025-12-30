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
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;

    @Transactional
    public OrderResponse.DTO saveOrder(int userId, OrderRequest.SaveDTO requestDTO) {

        // 1. 상품 재고 감소 
        ProductResponse.DTO product = new ProductResponse.DTO(
                                        productClient.decreaseQuantity(requestDTO.productId(), requestDTO.quantity())
                                    );
                
        // 2. 주문 생성
        Order order = Order.create(
            userId,
            requestDTO.productId(),
            requestDTO.quantity()
        );
        Order savedOrder = orderRepository.save(order);

        // 3. 주문 아이템 생성 
        OrderItem orderItem = OrderItem.create(
            savedOrder.getId(),
            requestDTO.productId(),
            requestDTO.quantity(),
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

        return new OrderResponse.DTO(savedOrder);
    }

    public OrderResponse.DTO findById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return new OrderResponse.DTO(order);
    }
}
