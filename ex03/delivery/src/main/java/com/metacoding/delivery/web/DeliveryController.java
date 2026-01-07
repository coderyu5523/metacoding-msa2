package com.metacoding.delivery.web;

import com.metacoding.delivery.usecase.*;
import com.metacoding.delivery.web.dto.*;
import com.metacoding.delivery.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> saveDelivery(@RequestBody CreateDeliveryRequest requestDTO) {
        DeliveryResponse response = deliveryService.saveDelivery(
            requestDTO.orderId(),
            requestDTO.address()
        );
        return Resp.ok(response);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<?> getDelivery(@PathVariable("deliveryId") int deliveryId) {
        DeliveryResponse response = deliveryService.findById(deliveryId);
        return Resp.ok(response);
    }

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<?> cancelDelivery(@PathVariable("deliveryId") int deliveryId) {
        deliveryService.cancelDelivery(deliveryId);
        return Resp.ok(null);
    }
}












