package com.metacoding.delivery.web.dto;

import java.time.LocalDateTime;

public record DeliveryResponse(
    int id,
    int orderId,
    String address,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

