package com.metacoding.product.adapter;

public class ProductResponse {
    public record DTO(
        int id,
        String productName,
        int quantity,
        Long price,
        String status
    ) {
    }
}

















