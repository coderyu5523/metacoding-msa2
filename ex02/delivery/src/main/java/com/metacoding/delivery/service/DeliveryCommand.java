package com.metacoding.delivery.service;

public record DeliveryCommand(
    int orderId,
    String address
) {
}

