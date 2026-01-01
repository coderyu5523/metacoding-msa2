package com.metacoding.product.usecase;

import com.metacoding.product.domain.product.Product;
import com.metacoding.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResult findById(int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        return ProductResult.from(product);
    }

    public List<ProductResult> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResult::from)
                .toList();
    }

    @Transactional
    public void decreaseQuantity(int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        product.decreaseQuantity(quantity);
    }

    @Transactional
    public void increaseQuantity(int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        product.increaseQuantity(quantity);
    }
}




