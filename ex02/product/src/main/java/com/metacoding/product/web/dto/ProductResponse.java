package com.metacoding.product.web.dto;

public record ProductResponse(
    int id,
    String productName,
    int quantity,
    Long price
) {
}






