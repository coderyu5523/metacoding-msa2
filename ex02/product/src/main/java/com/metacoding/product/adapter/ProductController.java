package com.metacoding.product.adapter;

import com.metacoding.product.service.*;
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
        ProductCommand command = new ProductCommand(productId, quantity);
        ProductResult result = productService.getProduct(command);
        ProductResponse.DTO response = new ProductResponse.DTO(
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
        List<ProductResponse.DTO> responses = results.stream()
            .map(result -> new ProductResponse.DTO(
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
        ProductCommand command = new ProductCommand(productId, quantity);
        productService.decreaseQuantity(command);
        return Resp.ok(null);
    }

    @PostMapping("/{productId}/increase")
    public ResponseEntity<?> increaseQuantity(@PathVariable("productId") int productId, @RequestParam int quantity) {
        ProductCommand command = new ProductCommand(productId, quantity);
        productService.increaseQuantity(command);
        return Resp.ok(null);
    }
}
