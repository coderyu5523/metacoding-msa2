package com.metacoding.order.adapter;

import com.metacoding.order.adapter.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "http://product-service:8082")
public interface ProductClient {
    
    @GetMapping("/api/products/{productId}")
    ProductResponse.DTO getProduct(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity);
        
    @PostMapping("/api/products/{productId}/decrease")
    void decreaseQuantity(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity);
}


