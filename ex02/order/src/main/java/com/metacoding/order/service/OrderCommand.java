package com.metacoding.order.service;

public record OrderCommand(
    int userId,
    int productId,
    int quantity
) {
}

