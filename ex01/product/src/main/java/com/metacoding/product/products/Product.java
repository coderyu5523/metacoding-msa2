package com.metacoding.product.products;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "product_tb")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String productName;
    private int quantity;
    private Long price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Product(String productName, int quantity, Long price, String status) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Product create(String productName, int quantity, Long price) {
        return new Product(productName, quantity, price, "PENDING");
    }
    
    public void complete() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = "CANCELLED";
        this.updatedAt = LocalDateTime.now();
    }

    public void decreaseQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new RuntimeException("제품의 수량이 부족합니다.");
        }
        this.quantity -= quantity;
    }

    public void checkQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new RuntimeException("제품의 수량이 부족합니다.");
        }
    }

    public void validateQuantity() {
        if (quantity < 0) {
            throw new RuntimeException("재고 수량은 0 이상이어야 합니다.");
        }
    }

    public void validatePrice() {
        if (price == null || price <= 0) {
            throw new RuntimeException("상품 가격은 0보다 커야 합니다.");
        }
    }
}
