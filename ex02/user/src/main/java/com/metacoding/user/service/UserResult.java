package com.metacoding.user.service;

import com.metacoding.user.domain.User;

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

