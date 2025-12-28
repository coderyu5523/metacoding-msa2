package com.metacoding.order.adapter.dto;

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


