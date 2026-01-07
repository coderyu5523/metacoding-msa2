package com.metacoding.orchestrator.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreated {
    private int orderId;
    private int userId;
    private int productId;
    private int quantity;
    private Long price;
    private String address;
}










