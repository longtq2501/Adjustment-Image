package com.tql.backend.service.Impl;

import com.tql.backend.dto.request.LoginRequest;
import com.tql.backend.dto.request.RegisterRequest;
import com.tql.backend.entity.User;
import com.tql.backend.repository.UserRepository;
import com.tql.backend.service.AuthenticateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticateServiceImpl implements AuthenticateService {
    UserRepository userRepository;

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        // 1. Tìm user theo username (hoặc email)// Sửa thành findByUsername
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (userOptional.isEmpty()) {
//            // Không tìm thấy user
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
//        }

//        User user = userOptional.get();

        // 2. So sánh mật khẩu (PLAIN TEXT - KHÔNG AN TOÀN)
        if (loginRequest.getPassword().equals(user.getPassword())) {
            // Mật khẩu khớp
            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } else {
            // Mật khẩu không khớp
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
        }
    }

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        var newAccount = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
        userRepository.save(newAccount);
        return ResponseEntity.ok("User registered successfully");
    }
}
