package com.metacoding.order.adapter;

import com.metacoding.order.adapter.dto.*;
import com.metacoding.order.core.util.Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "delivery-service", url = "http://delivery-service:8084")
public interface DeliveryClient {
        
    @PostMapping("/api/deliveries")
    Resp<DeliveryResponse.DTO> saveDelivery(@RequestBody DeliveryRequest.SaveDTO request);
    
    @GetMapping("/api/deliveries/{deliveryId}")
    Resp<DeliveryResponse.DTO> getDelivery(@PathVariable("deliveryId") int deliveryId);
    
    @DeleteMapping("/api/deliveries/{deliveryId}")
    Resp<Void> cancelDelivery(@PathVariable("deliveryId") int deliveryId);
    
}





