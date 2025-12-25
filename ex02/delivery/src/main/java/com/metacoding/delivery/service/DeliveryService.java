package com.metacoding.delivery.service;

import com.metacoding.delivery.domain.Delivery;
import com.metacoding.delivery.adapter.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResult createDelivery(DeliveryCommand command) {
        Delivery delivery = Delivery.create(command.orderId(), command.address());
        Delivery savedDelivery = deliveryRepository.save(delivery);
        savedDelivery.complete();
        Delivery updatedDelivery = deliveryRepository.save(savedDelivery);
        return DeliveryResult.from(updatedDelivery);
    }

    public DeliveryResult findDeliveryById(int id) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("배달 정보를 조회할 수 없습니다."));
        return DeliveryResult.from(delivery);
    }
}
