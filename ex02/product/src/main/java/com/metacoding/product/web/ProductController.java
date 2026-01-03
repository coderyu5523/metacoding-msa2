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
    public ResponseEntity<?> getProduct(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity) {
        ProductResult result = productService.findById(productId, quantity);
        ProductResponse response = new ProductResponse(
            result.id(),
            result.productName(),
            result.quantity(),
            result.price()
        );
        return Resp.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getProducts() {
        List<ProductResult> results = productService.findAll();
        List<ProductResponse> responses = results.stream()
            .map(result -> new ProductResponse(
                result.id(),
                result.productName(),
                result.quantity(),
                result.price()
            ))
            .toList();
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
        ProductResult result = productService.findById(productId, 0);
        ProductResponse response = new ProductResponse(
            result.id(),
            result.productName(),
            result.quantity(),
            result.price()
        );
        return Resp.ok(response);
    }
}











