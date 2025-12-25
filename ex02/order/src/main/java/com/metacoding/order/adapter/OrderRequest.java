package com.metacoding.order.adapter;

public class OrderRequest {
    public record SaveDTO(
        int userId,
        int productId,
        int quantity
    ) {
    }
}

