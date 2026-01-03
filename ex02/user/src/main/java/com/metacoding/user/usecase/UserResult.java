package com.metacoding.user.usecase;

import com.metacoding.user.domain.user.User;

public record UserResult(
    int id,
    String username,
    String email
) {
    public static UserResult from(User user) {
        return new UserResult(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}









