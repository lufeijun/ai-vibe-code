package com.example.demo.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "testSecretKeyForJwtTokenGenerationMustBeAtLeast256BitsLong");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 7200L);
    }

    @Test
    void generateTokenFromUsername_ShouldGenerateValidToken() {
        String username = "testuser";
        String token = tokenProvider.generateTokenFromUsername(username);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        String username = "testuser";
        String token = tokenProvider.generateTokenFromUsername(username);

        String extractedUsername = tokenProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String token = tokenProvider.generateTokenFromUsername("testuser");

        boolean isValid = tokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = tokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalseForMalformedToken() {
        String malformedToken = "this-is-not-a-valid-jwt-token";

        boolean isValid = tokenProvider.validateToken(malformedToken);

        assertFalse(isValid);
    }
}
