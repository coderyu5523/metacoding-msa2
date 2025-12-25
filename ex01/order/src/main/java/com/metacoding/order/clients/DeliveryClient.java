package com.metacoding.order.clients;

import com.metacoding.order.clients.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "delivery-service", url = "http://delivery-service:8084")
public interface DeliveryClient {
        
    @PostMapping("/api/deliveries")
    DeliveryResponse.DTO saveDelivery(@RequestBody DeliveryRequest.SaveDTO request);
    
    @GetMapping("/api/deliveries/{id}")
    DeliveryResponse.DTO getDelivery(@PathVariable("id") int id);
    
}


