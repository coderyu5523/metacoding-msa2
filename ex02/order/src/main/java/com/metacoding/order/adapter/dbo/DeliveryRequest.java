package com.metacoding.order.adapter.dbo;

public class DeliveryRequest {
    public record SaveDTO(
        int orderId,
        String address
    ) {
    }
}




