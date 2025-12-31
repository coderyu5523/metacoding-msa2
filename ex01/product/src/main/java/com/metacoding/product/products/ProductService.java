package com.metacoding.product.products;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse.DTO findById(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        return new ProductResponse.DTO(product);
    }

    public List<ProductResponse.DTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse.DTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse.DTO decreaseQuantity(int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        product.checkQuantity(product.getQuantity());
        product.decreaseQuantity(quantity);
        productRepository.save(product);
        return new ProductResponse.DTO(product);
    }

    @Transactional
    public ProductResponse.DTO increaseQuantity(int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        product.increaseQuantity(quantity);
        productRepository.save(product);
        return new ProductResponse.DTO(product);
    }
}
