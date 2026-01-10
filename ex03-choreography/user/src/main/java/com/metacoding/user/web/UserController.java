package com.metacoding.user.web;

import com.metacoding.user.usecase.*;
import com.metacoding.user.web.dto.*;
import com.metacoding.user.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        UserResponse response = userService.findById(userId);
        return Resp.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> responses = userService.findAll();
        return Resp.ok(responses);
    }
}












