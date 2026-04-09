package com.example.demo.config;

import com.example.demo.exception.JwtAuthenticationException;
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
        String token = tokenProvider.generateTokenFromUsername(username, null);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void generateTokenFromUsername_WithUserId_ShouldGenerateValidToken() {
        String username = "testuser";
        Long userId = 123L;
        String token = tokenProvider.generateTokenFromUsername(username, userId);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        String username = "testuser";
        String token = tokenProvider.generateTokenFromUsername(username, null);

        String extractedUsername = tokenProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void getUserIdFromToken_ShouldReturnCorrectUserId() {
        String username = "testuser";
        Long userId = 123L;
        String token = tokenProvider.generateTokenFromUsername(username, userId);

        Long extractedUserId = tokenProvider.getUserIdFromToken(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void getUserIdFromToken_WhenNoUserId_ShouldReturnNull() {
        String username = "testuser";
        String token = tokenProvider.generateTokenFromUsername(username, null);

        Long extractedUserId = tokenProvider.getUserIdFromToken(token);

        assertNull(extractedUserId);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String token = tokenProvider.generateTokenFromUsername("testuser", 123L);

        boolean isValid = tokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.here";

        JwtAuthenticationException exception = assertThrows(JwtAuthenticationException.class,
                () -> tokenProvider.validateToken(invalidToken));

        assertEquals("TOKEN_MALFORMED", exception.getErrorCode());
    }

    @Test
    void validateToken_ShouldThrowExceptionForMalformedToken() {
        String malformedToken = "this-is-not-a-valid-jwt-token";

        JwtAuthenticationException exception = assertThrows(JwtAuthenticationException.class,
                () -> tokenProvider.validateToken(malformedToken));

        assertEquals("TOKEN_MALFORMED", exception.getErrorCode());
    }
}
