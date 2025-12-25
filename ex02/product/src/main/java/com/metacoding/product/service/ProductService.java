package com.metacoding.product.service;

import com.metacoding.product.domain.Product;
import com.metacoding.product.adapter.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResult getProduct(ProductCommand command) {
        Product product = productRepository.findById(command.productId())
            .orElseThrow(() -> new RuntimeException("등록된 상품이 없습니다."));
        
        product.quantityCheck(command.quantity()); // 재고 확인
        
        return ProductResult.from(product);
    }

    public List<ProductResult> findAll() {
        return productRepository.findAll().stream()
            .map(ProductResult::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void decreaseQuantity(ProductCommand command) {
        Product product = productRepository.findById(command.productId())
            .orElseThrow(() -> new RuntimeException("등록된 상품이 없습니다."));
        product.decreaseQuantity(command.quantity());
        product.updateStatus("PENDING");
        Product savedProduct = productRepository.save(product);
        savedProduct.updateStatus("SUCCESS");
        productRepository.save(savedProduct);
    }

    @Transactional
    public void increaseQuantity(ProductCommand command) {
        Product product = productRepository.findById(command.productId())
            .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        product.increaseQuantity(command.quantity());
        productRepository.save(product);
    }
}
