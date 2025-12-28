package com.metacoding.order.web.dto;

import java.time.LocalDateTime;

public record OrderResponse(
    int id,
    int userId,
    int productId,
    int quantity,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}




