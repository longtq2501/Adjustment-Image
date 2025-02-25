package com.tql.backend.service.Impl;

import com.tql.backend.dto.request.UserCreateRequest;
import com.tql.backend.dto.request.UserUpdateRequest;
import com.tql.backend.entity.User;
import com.tql.backend.repository.UserRepository;
import com.tql.backend.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public ResponseEntity<?> createUser(UserCreateRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
        return ResponseEntity.ok(userRepository.save(user));
    }

    @Override
    public ResponseEntity<?> updateUser(String id, UserUpdateRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUsername(request.getUsername());

        return ResponseEntity.ok(userRepository.save(user));
    }

    @Override
    public ResponseEntity<?> getUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @Override
    public void deleteUser(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(List.of(users));
    }
}
