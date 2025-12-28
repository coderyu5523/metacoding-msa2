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
        delivery.complete();
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return DeliveryResult.from(savedDelivery);
    }

    public DeliveryResult findById(int id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배달 정보를 조회할 수 없습니다."));
        return DeliveryResult.from(delivery);
    }
}
