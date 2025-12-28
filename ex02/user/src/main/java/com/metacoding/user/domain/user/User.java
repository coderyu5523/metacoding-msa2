package com.metacoding.user.domain.user;

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
    private String roles;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private User(String username, String email, String password, String roles, String status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
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

    public void validateUsername() {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("사용자명은 필수입니다.");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new RuntimeException("사용자명은 3자 이상 20자 이하여야 합니다.");
        }
    }

    public void validateEmail() {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("이메일은 필수입니다.");
        }
    }

    public void validatePassword() {
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("비밀번호는 필수입니다.");
        }
        if (password.length() < 4) {
            throw new RuntimeException("비밀번호는 4자 이상이어야 합니다.");
        }
    }
}




