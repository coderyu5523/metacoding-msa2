package com.metacoding.order.adapter.dto;

import com.metacoding.order.core.util.Resp;

public class ProductResponse {
    public record DTO(
        int id,
        String productName,
        int quantity,
        Long price
    ) {
        public DTO(Resp<DTO> resp) {
            this(
                resp.getBody().id(),
                resp.getBody().productName(),
                resp.getBody().quantity(),
                resp.getBody().price()
            );
        }
    }
}



