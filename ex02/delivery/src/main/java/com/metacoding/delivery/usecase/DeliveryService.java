package com.metacoding.delivery.usecase;

import com.metacoding.delivery.domain.delivery.Delivery;
import com.metacoding.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResult createDelivery(int orderId, String address) {
        Delivery delivery = Delivery.create(orderId, address);
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

