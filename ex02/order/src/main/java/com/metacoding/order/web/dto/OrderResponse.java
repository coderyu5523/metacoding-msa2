package com.metacoding.order.web.dto;

import com.metacoding.order.domain.order.OrderStatus;
import java.time.LocalDateTime;

public record OrderResponse(
    int id,
    int userId,
    int productId,
    int quantity,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}







