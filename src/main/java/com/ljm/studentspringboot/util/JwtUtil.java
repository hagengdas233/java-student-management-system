package com.ljm.studentspringboot.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "student-springboot-auth-version-jwt-secret-key-32bytes-minimum";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRE_MILLIS = 24 * 60 * 60 * 1000L;

    private JwtUtil() {
    }

    public static String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRE_MILLIS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean validateToken(String token) {
        parseToken(token);
        return true;
    }
}
