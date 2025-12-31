package com.metacoding.user.users;

import com.metacoding.user.core.util.JwtUtil;
import static com.metacoding.user.core.util.JwtUtil.TOKEN_PREFIX;
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

    public UserResponse.DTO findById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        return new UserResponse.DTO(user);
    }

    public List<UserResponse.DTO> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse.DTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public String login(UserRequest.LoginDTO requestDTO) {
        User user = userRepository.findByUsername(requestDTO.username())
                .orElseThrow(() -> new RuntimeException("유저네임을 찾을 수 없습니다."));
        user.passwordCheck(requestDTO.password());   
        String token = jwtUtil.create(user.getId(), user.getUsername());
        return TOKEN_PREFIX + token;
    }
}
