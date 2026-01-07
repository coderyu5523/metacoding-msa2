package com.metacoding.orchestrator.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCreated {
    private int orderId;
    private int deliveryId;
    private boolean success;
}










