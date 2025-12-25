package com.metacoding.user.service;

import com.metacoding.user.core.util.JwtUtil;
import com.metacoding.user.domain.User;
import com.metacoding.user.adapter.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserResult findUserById(int id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        return UserResult.from(user);
    }

    public List<UserResult> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(UserResult::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public LoginResult login(UserCommand command) {
        User user = userRepository.findByUsername(command.username())
            .orElseThrow(() -> new RuntimeException("유저네임을 찾을 수 없습니다."));
        user.passwordCheck(command.password());
        String token = jwtUtil.create(user.getId(), user.getUsername());
        return new LoginResult(JwtUtil.TOKEN_PREFIX + token);
    }
}
