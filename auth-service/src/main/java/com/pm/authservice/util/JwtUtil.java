package com.pm.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private final Key secretKey;
    private final String plainTextSecret;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // Use the UTF-8 encoded bytes of the secret directly
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.plainTextSecret = secret;
        log.warn("JWT Secret Key (plain text) for verification: {}", plainTextSecret);
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                   .setSubject(email)
                   .claim("role", role)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                   .signWith(secretKey)
                   .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token);
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT");
        }
    }


}
