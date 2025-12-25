package com.example.royalpizza.config;

import com.example.royalpizza.entity.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenManager {

    private Key secretKey = Keys.hmacShaKeyFor("MaSuperCleSecreteJWT_Pour_RoyalPizza_2025_TresLongue!".getBytes());

    @Value("${jwt.expiration}")
    private long validity; // 24 hours in milliseconds


    public String generateToken(Customer customer) {
        return Jwts.builder()
                .claim("id", customer.getIdCustomer())
                .claim("firstName", customer.getFirstName())
                .claim("lastName", customer.getLastName())
                .claim("emailAddress", customer.getEmailAddress())
                .claim("wallet", customer.getWallet())
                .claim("isAdmin", customer.getIsAdmin())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return false; // pas d'exception → pas expiré
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true; // exception → token expiré
        }
    }


    public Long parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return ((Number) claims.get("id")).longValue();
    }

    public boolean isAdminFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (Boolean) claims.get("isAdmin");
    }


}
