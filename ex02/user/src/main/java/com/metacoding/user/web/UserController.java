package com.metacoding.user.web;

import com.metacoding.user.usecase.*;
import com.metacoding.user.web.dto.*;
import com.metacoding.user.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest requestDTO) {
        return Resp.ok(userService.login(requestDTO.username(), requestDTO.password()));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable("userId") int userId) {
        UserResult result = userService.findById(userId);
        UserResponse response = new UserResponse(
            result.id(),
            result.username(),
            result.email()
        );
        return Resp.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserResult> results = userService.findAll();
        List<UserResponse> responses = results.stream()
            .map(result -> new UserResponse(
                result.id(),
                result.username(),
                result.email()
            ))
            .toList();
        return Resp.ok(responses);
    }
}













