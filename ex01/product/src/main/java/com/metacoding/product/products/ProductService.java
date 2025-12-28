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

    public ProductResponse.DTO findById(int id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        product.checkQuantity(quantity);
        return new ProductResponse.DTO(product);
    }

    public List<ProductResponse.DTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse.DTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void decreaseQuantity(int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다."));
        product.decreaseQuantity(quantity);
        product.complete();
        productRepository.save(product);
    }
}
