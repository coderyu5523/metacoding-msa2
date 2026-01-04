package com.metacoding.delivery.web.dto;

import com.metacoding.delivery.domain.delivery.DeliveryStatus;
import java.time.LocalDateTime;

public record DeliveryResponse(
    int id,
    int orderId,
    String address,
    DeliveryStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}














