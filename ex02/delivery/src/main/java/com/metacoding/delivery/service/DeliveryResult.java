package com.metacoding.delivery.service;

import com.metacoding.delivery.domain.Delivery;
import java.time.LocalDateTime;

public record DeliveryResult(
    int id,
    int orderId,
    String address,
    String status,
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

