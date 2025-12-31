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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Delivery(int orderId, String address) {
        this.orderId = orderId;
        this.address = address;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Delivery create(int orderId, String address) {
        return new Delivery(orderId, address);
    }
}




