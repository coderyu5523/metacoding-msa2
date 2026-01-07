package com.metacoding.orchestrator.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDecreased {
    private int orderId;
    private int productId;
    private int quantity;
    private boolean success;
}


