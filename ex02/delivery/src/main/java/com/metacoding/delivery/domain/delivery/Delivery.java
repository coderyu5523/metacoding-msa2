package com.metacoding.delivery.domain.delivery;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "delivery_tb")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int orderId;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Delivery(int orderId, String address, String status) {
        this.orderId = orderId;
        this.address = address;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Delivery create(int orderId, String address) {
        return new Delivery(orderId, address, "PENDING");
    }

    public void complete() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = "CANCELLED";
        this.updatedAt = LocalDateTime.now();
    }

    public void validateOrderId() {
        if (orderId <= 0) {
            throw new RuntimeException("유효한 주문 ID가 필요합니다.");
        }
    }

    public void validateAddress() {
        if (address == null || address.trim().isEmpty()) {
            throw new RuntimeException("배송 주소는 필수입니다.");
        }
        if (address.length() < 5 || address.length() > 200) {
            throw new RuntimeException("배송 주소는 5자 이상 200자 이하여야 합니다.");
        }
    }
}




