package com.metacoding.order.adapter.dto;

import com.metacoding.order.core.util.Resp;
import java.time.LocalDateTime;

public class DeliveryResponse {
    public record DTO(
        int id,
        int orderId,
        String address
    ) {
        public DTO(Resp<DTO> resp) {
            this(
                resp.getBody().id(),
                resp.getBody().orderId(),
                resp.getBody().address()
            );
        }
    }
}
