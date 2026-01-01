package com.metacoding.delivery.usecase;

import com.metacoding.delivery.domain.delivery.Delivery;
import java.time.LocalDateTime;

public record DeliveryResult(
    int id,
    int orderId,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static DeliveryResult from(Delivery delivery) {
        return new DeliveryResult(
            delivery.getId(),
            delivery.getOrderId(),
            delivery.getAddress(),
            delivery.getCreatedAt(),
            delivery.getUpdatedAt()
        );
    }
}








