package com.metacoding.product.adapter.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DecreaseProductCommand {
    private int orderId;
    private int productId;
    private int quantity;
}
