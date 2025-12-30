package com.metacoding.order.adapter;

import com.metacoding.order.core.util.Resp;

public class DeliveryResponse {
    public record DTO(
        int id,
        int orderId,
        String address,
        String status
    ) {
        public DTO(Resp<DTO> resp) {
            this(
                resp.getBody().id(),
                resp.getBody().orderId(),
                resp.getBody().address(),
                resp.getBody().status()
            );
        }
    }
}





















