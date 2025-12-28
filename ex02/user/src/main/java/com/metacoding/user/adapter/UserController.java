package com.metacoding.user.adapter;

import com.metacoding.user.service.*;
import com.metacoding.user.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO requestDTO) {
        UserCommand command = new UserCommand(requestDTO.username(), requestDTO.password());
        LoginResult result = userService.login(command);
        return Resp.ok(result.token());
    }

    @GetMapping("/api/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable("userId") int userId) {
        UserResult result = userService.findById(userId);
        UserResponse.DTO response = new UserResponse.DTO(
            result.id(),
            result.username(),
            result.email()
        );
        return Resp.ok(response);
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserResult> results = userService.findAll();
        List<UserResponse.DTO> responses = results.stream()
            .map(result -> new UserResponse.DTO(
                result.id(),
                result.username(),
                result.email()
            ))
            .collect(Collectors.toList());
        return Resp.ok(responses);
    }
}
