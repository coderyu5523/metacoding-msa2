package com.metacoding.product.web;

import com.metacoding.product.usecase.*;
import com.metacoding.product.web.dto.*;
import com.metacoding.product.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final GetProductUseCase getProductUseCase;
    private final GetProductsUseCase getProductsUseCase;
    private final DecreaseProductUseCase decreaseProductUseCase;
    private final IncreaseProductUseCase increaseProductUseCase;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") int productId) {
        ProductResponse response = getProductUseCase.findById(productId);
        return Resp.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getProducts() {
        List<ProductResponse> responses = getProductsUseCase.findAll();
        return Resp.ok(responses);
    }

    @PostMapping("/{productId}/decrease")
    public ResponseEntity<?> decreaseQuantity(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity) {
        decreaseProductUseCase.decreaseQuantity(productId, quantity);
        return Resp.ok(null);
    }

    @PostMapping("/{productId}/increase")
    public ResponseEntity<?> increaseQuantity(@PathVariable("productId") int productId, @RequestParam("quantity") int quantity) {
        increaseProductUseCase.increaseQuantity(productId, quantity);
        ProductResponse response = getProductUseCase.findById(productId);
        return Resp.ok(response);
    }
}












