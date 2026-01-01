package com.metacoding.user.usecase;

import com.metacoding.user.core.handler.ex.Exception404;
import com.metacoding.user.core.util.JwtUtil;
import com.metacoding.user.domain.user.User;
import com.metacoding.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserResult findById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("회원 정보를 찾을 수 없습니다."));
        return UserResult.from(user);
    }

    public List<UserResult> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(UserResult::from)
            .toList();
    }

    @Transactional
    public LoginResult login(String username, String password) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new Exception404("유저네임을 찾을 수 없습니다."));
        user.passwordCheck(password);
        String token = jwtUtil.create(user.getId(), user.getUsername());
        return TOKEN_PREFIX + token;
    }
}




