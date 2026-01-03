package com.metacoding.delivery.usecase;

import com.metacoding.delivery.domain.delivery.Delivery;
import com.metacoding.delivery.domain.delivery.DeliveryStatus;
import java.time.LocalDateTime;

public record DeliveryResult(
    int id,
    int orderId,
    String address,
    DeliveryStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static DeliveryResult from(Delivery delivery) {
        return new DeliveryResult(
            delivery.getId(),
            delivery.getOrderId(),
            delivery.getAddress(),
            delivery.getStatus(),
            delivery.getCreatedAt(),
            delivery.getUpdatedAt()
        );
    }
}










