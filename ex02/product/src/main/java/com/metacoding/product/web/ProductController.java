package com.metacoding.product.web;

import com.metacoding.product.usecase.*;
import com.metacoding.product.web.dto.*;
import com.metacoding.product.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") int productId) {
        ProductResponse response = productService.findById(productId);
        return Resp.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getProducts() {
        List<ProductResponse> responses = productService.findAll();
        return Resp.ok(responses);
    }

    @PostMapping("/{productId}/decrease")
    public ResponseEntity<?> decreaseQuantity(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity) {
        productService.decreaseQuantity(productId, quantity);
        return Resp.ok(null);
    }

    @PostMapping("/{productId}/increase")
    public ResponseEntity<?> increaseQuantity(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity) {
        productService.increaseQuantity(productId, quantity);
        ProductResponse response = productService.findById(productId);
        return Resp.ok(response);
    }
}


















