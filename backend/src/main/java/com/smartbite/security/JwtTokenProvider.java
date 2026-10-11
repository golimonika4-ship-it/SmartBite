package com.smartbite.security;

import com.smartbite.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final String cookieName;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms:86400000}") long expirationMs,
            @Value("${security.jwt.cookie-name:smartbite_auth_token}") String cookieName) {
        // Enforce secure key initialization (at least 256 bits / 32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
        this.cookieName = cookieName;
    }

    /**
     * Generate signed JWT token with user identity and role claim
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("rollNumber", user.getRollNumber())
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extract username/email from token claims
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Validate JWT token signature and expiration safely without crashing
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token.trim());
            return true;
        } catch (ExpiredJwtException e) {
            // Token expired
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // Malformed, unsupported, or bad signature
            return false;
        }
    }

    /**
     * Parse claims from token
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token.trim())
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract token from request (Bearer header first, fallback to HttpOnly cookie)
     */
    public String resolveToken(HttpServletRequest request) {
        // 1. Check Authorization Bearer header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }

        // 2. Fallback to HttpOnly cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    return cookie.getValue().trim();
                }
            }
        }

        return null;
    }

    public String getCookieName() {
        return cookieName;
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
