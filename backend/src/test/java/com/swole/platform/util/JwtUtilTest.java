package com.swole.platform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret = "testsecretforunittestingwhichislongenoughtomeetsecurityrequirements";
    private Long expiration = 86400000L; // 24 hours

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    @Test
    public void testGenerateToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertTrue(!token.isEmpty());
    }

    @Test
    public void testGetUsernameFromToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testValidateTokenValid() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        boolean isValid = jwtUtil.validateToken(token, username);
        assertTrue(isValid);
    }

    @Test
    public void testValidateTokenInvalidUsername() {
        String username = "testuser";
        String wrongUsername = "wronguser";
        String token = jwtUtil.generateToken(username);

        boolean isValid = jwtUtil.validateToken(token, wrongUsername);
        assertFalse(isValid);
    }

    @Test
    public void testGetExpirationDateFromToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);
        assertNotNull(expirationDate);
    }
}