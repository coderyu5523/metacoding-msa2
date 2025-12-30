package com.metacoding.order.adapter;

import com.metacoding.order.core.util.Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "delivery-service", url = "http://delivery-service:8084")
public interface DeliveryClient {
        
    @PostMapping("/api/deliveries")
    Resp<DeliveryResponse.DTO> saveDelivery(@RequestBody DeliveryRequest.SaveDTO request);
    
    @GetMapping("/api/deliveries/{id}")
    Resp<DeliveryResponse.DTO> getDelivery(@PathVariable("id") int id);
    
    @DeleteMapping("/api/deliveries/order/{orderId}")
    Resp<Void> cancelDelivery(@PathVariable("orderId") int orderId);
}

