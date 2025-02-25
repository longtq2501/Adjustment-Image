package com.tql.backend.controller;

import com.tql.backend.dto.request.LoginRequest;
import com.tql.backend.dto.request.RegisterRequest;
import com.tql.backend.service.AuthenticateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CrossOrigin(origins = "*")
public class AuthenticateController {
    AuthenticateService authenticateService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("email") String email) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
       return ResponseEntity.ok( authenticateService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return ResponseEntity.ok(authenticateService.login(request));
    }
}
