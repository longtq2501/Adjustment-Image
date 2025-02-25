package com.tql.backend.service;

import com.tql.backend.dto.request.UserCreateRequest;
import com.tql.backend.dto.request.UserUpdateRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> createUser(UserCreateRequest request);
    ResponseEntity<?> updateUser(String id, UserUpdateRequest request);
    ResponseEntity<?> getUserByUsername(String username);
    void deleteUser(String username);
    ResponseEntity<?> getAllUsers();
}
