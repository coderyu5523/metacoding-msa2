package com.metacoding.delivery.deliveries;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResponse.DTO saveDelivery(int orderId, String address) {
        Delivery delivery = Delivery.create(orderId, address);
        deliveryRepository.save(delivery);
        return new DeliveryResponse.DTO(delivery);
    }

    public DeliveryResponse.DTO findById(int deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("배달 정보를 조회할 수 없습니다."));
        return new DeliveryResponse.DTO(delivery);
    }

    @Transactional
    public void cancelDelivery(int deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("배달 정보를 조회할 수 없습니다."));
        deliveryRepository.delete(delivery);
    }
}
