package com.example.demo;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.controller.HelloWorldController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelloWorldControllerTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private HelloWorldController controller;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "testSecretKeyForJwtTokenGenerationMustBeAtLeast256BitsLong");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 7200L);
    }

    @Test
    void hello_WithoutToken_ShouldReturnHelloWorld() {
        when(request.getHeader("Authorization")).thenReturn(null);
        String result = controller.hello(request);
        assertEquals("Hello World!", result);
    }

    @Test
    void hello_WithToken_ShouldReturnHelloWorldWithUserId() {
        String token = "test-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.getUserIdFromToken(token)).thenReturn(123L);

        String result = controller.hello(request);
        assertEquals("Hello World! User ID: 123", result);
    }
}
