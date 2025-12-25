package com.metacoding.order.adapter;

public class DeliveryRequest {
    public record SaveDTO(
        int orderId,
        String address
    ) {
    }
}




