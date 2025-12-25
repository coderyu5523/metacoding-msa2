package com.metacoding.product.service;

import com.metacoding.product.domain.Product;

public record ProductResult(
    int id,
    String productName,
    int quantity,
    Long price,
    String status
) {
    public static ProductResult from(Product product) {
        return new ProductResult(
            product.getId(),
            product.getProductName(),
            product.getQuantity(),
            product.getPrice(),
            product.getStatus()
        );
    }
}

