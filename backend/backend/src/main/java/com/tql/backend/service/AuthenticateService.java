package com.tql.backend.service;

import com.tql.backend.dto.request.LoginRequest;
import com.tql.backend.dto.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticateService {
    ResponseEntity<?> login(LoginRequest request);
    ResponseEntity<?> register(RegisterRequest request);
}
