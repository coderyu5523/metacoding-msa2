package com.metacoding.delivery.deliveries;

import org.springframework.http.ResponseEntity;
import com.metacoding.delivery.core.util.Resp;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> saveDelivery(@RequestBody DeliveryRequest.SaveDTO requestDTO) {
        return Resp.ok(deliveryService.saveDelivery(requestDTO.orderId(), requestDTO.address()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDelivery(@PathVariable int id) {
        return Resp.ok(deliveryService.findById(id));
    }
}
