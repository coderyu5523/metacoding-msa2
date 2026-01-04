package com.metacoding.delivery.usecase;

import com.metacoding.delivery.core.handler.ex.Exception404;
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
    public DeliveryResult saveDelivery(int orderId, String address) {
        Delivery delivery = Delivery.create(orderId, address);
        deliveryRepository.save(delivery);
        delivery.complete();
        return DeliveryResult.from(delivery);
    }

    public DeliveryResult findById(int deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new Exception404("배달 정보를 조회할 수 없습니다."));
        return DeliveryResult.from(delivery);
    }

    @Transactional
    public void cancelDelivery(int deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new Exception404("배달 정보를 조회할 수 없습니다."));
        delivery.cancel();
    }
}














