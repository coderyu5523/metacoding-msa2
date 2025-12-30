package com.metacoding.order.adapter;

import com.metacoding.order.adapter.dto.*;
import com.metacoding.order.core.util.Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "http://product-service:8082")
public interface ProductClient {
    
    @GetMapping("/api/products/{productId}")
    Resp<ProductResponse.DTO> getProduct(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity);
        
    @PostMapping("/api/products/{productId}/decrease")
    Resp<ProductResponse.DTO> decreaseQuantity(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity);
    
    @PostMapping("/api/products/{productId}/increase")
    Resp<ProductResponse.DTO> increaseQuantity(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity);
}



