package com.metacoding.delivery.adapter.message;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IncreaseProductCommand {
    private int orderId;
    private int productId;
    private int quantity;
}
