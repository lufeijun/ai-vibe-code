package com.example.demo.controller;

import com.example.demo.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private final JwtTokenProvider tokenProvider;

    public HelloWorldController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            Long userId = tokenProvider.getUserIdFromToken(token);
            return "Hello World! User ID: " + userId;
        }
        return "Hello World!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}