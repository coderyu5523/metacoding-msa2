package com.metacoding.product.web;

import com.metacoding.product.usecase.ProductService;
import com.metacoding.product.usecase.ProductResult;
import com.metacoding.product.web.dto.ProductResponse;
import com.metacoding.product.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") int productId, @RequestParam int quantity) {
        ProductResult result = productService.findById(productId, quantity);
        ProductResponse response = new ProductResponse(
            result.id(),
            result.productName(),
            result.quantity(),
            result.price(),
            result.status()
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
                result.price(),
                result.status()
            ))
            .collect(Collectors.toList());
        return Resp.ok(responses);
    }

    @PostMapping("/{productId}/decrease")
    public ResponseEntity<?> decreaseQuantity(@PathVariable("productId") int productId, @RequestParam int quantity) {
        productService.decreaseQuantity(productId, quantity);
        return Resp.ok(null);
    }
}




