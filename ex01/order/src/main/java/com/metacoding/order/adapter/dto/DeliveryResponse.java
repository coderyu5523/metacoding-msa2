package com.metacoding.order.adapter.dto;

import java.time.LocalDateTime;

public class DeliveryResponse {
    public record DTO(
        int id,
        int orderId,
        String address,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}


