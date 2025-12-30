package com.metacoding.delivery.web;

import com.metacoding.delivery.usecase.*;
import com.metacoding.delivery.web.dto.*;
import com.metacoding.delivery.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> saveDelivery(@RequestBody CreateDeliveryRequest requestDTO) {
        DeliveryResult result = deliveryService.createDelivery(
            requestDTO.orderId(),
            requestDTO.address()
        );
        DeliveryResponse response = new DeliveryResponse(
            result.id(),
            result.orderId(),
            result.address(),
            result.createdAt(),
            result.updatedAt()
        );
        return Resp.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDelivery(@PathVariable int id) {
        DeliveryResult result = deliveryService.findById(id);
        DeliveryResponse response = new DeliveryResponse(
            result.id(),
            result.orderId(),
            result.address(),
            result.createdAt(),
            result.updatedAt()
        );
        return Resp.ok(response);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<?> cancelDelivery(@PathVariable int orderId) {
        deliveryService.cancelDeliveryByOrderId(orderId);
        return Resp.ok(null);
    }
}




