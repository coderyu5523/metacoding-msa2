package com.metacoding.order.adapter.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelled {
    private int orderId;
    private int productId;
    private int quantity;
}
