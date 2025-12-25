package com.metacoding.order.orders;

import com.metacoding.order.clients.*;
import com.metacoding.order.clients.dto.*;
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
        // 1. 주문 insert
        Order order = Order.create(
                userId,
                requestDTO.productId(),
                requestDTO.quantity()
        );
        Order savedOrder = orderRepository.save(order);

        // 2. 상품 재고 확인 및 감소 (ProductService에서 재고 확인 포함)
        productClient.getProduct(requestDTO.productId(), requestDTO.quantity());

        // 3. 상품 재고 감소 (상태가 PENDING으로 변경됨)
        productClient.decreaseQuantity(requestDTO.productId(), requestDTO.quantity());
                
        // 4. 배달 생성
        DeliveryRequest.SaveDTO deliveryRequest = new DeliveryRequest.SaveDTO(savedOrder.getId(), "Default Address");
        deliveryClient.saveDelivery(deliveryRequest);

        // 5. 주문 상태 변경
        savedOrder.updateStatus("success");
        Order completedOrder = orderRepository.save(savedOrder);

        return new OrderResponse.DTO(completedOrder);
    }

    public OrderResponse.DTO findById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        return new OrderResponse.DTO(order);
    }
}
