package com.metacoding.order.usecase;

import com.metacoding.order.domain.order.*;
import java.time.LocalDateTime;

public record OrderResult(
    int id,
    int userId,
    int productId,
    int quantity,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static OrderResult from(Order order) {
        return new OrderResult(
            order.getId(),
            order.getUserId(),
            order.getProductId(),
            order.getQuantity(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}










