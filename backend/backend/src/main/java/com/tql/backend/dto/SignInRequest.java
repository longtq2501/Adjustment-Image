package com.tql.backend.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
