package com.metacoding.product.usecase;

import com.metacoding.product.domain.product.Product;
import java.time.LocalDateTime;

public record ProductResult(
    int id,
    String productName,
    int quantity,
    Long price,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProductResult from(Product product) {
        return new ProductResult(
            product.getId(),
            product.getProductName(),
            product.getQuantity(),
            product.getPrice(),
            product.getStatus(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}




