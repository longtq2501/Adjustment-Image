package com.tql.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthWhitelistFilter extends OncePerRequestFilter {
    private static final List<String> WHITELIST = Arrays.asList(
            "/api/auth/signup",
            "/api/test",
            "/api/images/download" // Thêm vào danh sách trắng
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (WHITELIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response); // Bỏ qua xác thực
            return;
        }
        filterChain.doFilter(request, response);
    }
}