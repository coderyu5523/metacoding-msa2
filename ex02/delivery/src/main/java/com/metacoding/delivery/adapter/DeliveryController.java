package com.metacoding.delivery.adapter;

import com.metacoding.delivery.service.DeliveryCommand;
import com.metacoding.delivery.service.DeliveryResult;
import com.metacoding.delivery.service.DeliveryService;
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
    public ResponseEntity<?> saveDelivery(@RequestBody DeliveryRequest.SaveDTO requestDTO) {
        DeliveryCommand command = new DeliveryCommand(
            requestDTO.orderId(),
            requestDTO.address()
        );
        DeliveryResult result = deliveryService.createDelivery(command);
        DeliveryResponse.DTO response = new DeliveryResponse.DTO(
            result.id(),
            result.orderId(),
            result.address(),
            result.status(),
            result.createdAt(),
            result.updatedAt()
        );
        return Resp.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDelivery(@PathVariable int id) {
        DeliveryResult result = deliveryService.findDeliveryById(id);
        DeliveryResponse.DTO response = new DeliveryResponse.DTO(
            result.id(),
            result.orderId(),
            result.address(),
            result.status(),
            result.createdAt(),
            result.updatedAt()
        );
        return Resp.ok(response);
    }
}
