package com.metacoding.product.usecase;

public interface GetProductUseCase {
    ProductResult findById(int productId, int quantity);
}

