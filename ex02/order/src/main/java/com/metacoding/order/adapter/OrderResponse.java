package com.metacoding.order.adapter;

import java.time.LocalDateTime;

public class OrderResponse {
    public record DTO(
        int id,
        int userId,
        int productId,
        int quantity,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}

















