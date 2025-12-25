package com.metacoding.order.clients.dto;

public class DeliveryRequest {
    public record SaveDTO(
        int orderId,
        String address
    ) {
    }
}

