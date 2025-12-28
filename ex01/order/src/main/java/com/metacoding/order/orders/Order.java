package com.metacoding.order.orders;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "order_tb")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Order(int userId, int productId, int quantity, String status) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Order create(int userId, int productId, int quantity) {
        return new Order(userId, productId, quantity, "PENDING");
    }

    public void complete() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = "CANCELLED";
        this.updatedAt = LocalDateTime.now();
    }

    public void validateUserId() {
        if (userId <= 0) {
            throw new RuntimeException("유효한 사용자 ID가 필요합니다.");
        }
    }

    public void validateProductId() {
        if (productId <= 0) {
            throw new RuntimeException("유효한 상품 ID가 필요합니다.");
        }
    }

    public void validateQuantity() {
        if (quantity <= 0) {
            throw new RuntimeException("주문 수량은 1개 이상이어야 합니다.");
        }
    }
}






