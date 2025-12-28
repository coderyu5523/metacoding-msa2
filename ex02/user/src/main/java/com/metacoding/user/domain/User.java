package com.metacoding.user.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user_tb")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private User(String username, String email, String password, String role, String status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void passwordCheck(String password) {
        if (!this.password.equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    public static User create(String username, String email, String password) {
        return new User(username, email, password, "USER", "PENDING");
    }
    
    public void complete() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = "CANCELLED";
        this.updatedAt = LocalDateTime.now();
    }
}




