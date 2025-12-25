package com.metacoding.order.clients.dto;

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


