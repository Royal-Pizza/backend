package com.example.royalpizza.config;

import com.example.royalpizza.entity.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import io.jsonwebtoken.*;
import java.security.Key;

@Component
public class JwtTokenManager {

    private final Key secretKey = Keys.hmacShaKeyFor("MaSuperCleSecreteJWT_Pour_RoyalPizza_2025_TresLongue!".getBytes());
    private final long validity = 8400000; // 24 hours in milliseconds


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
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public Long parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return ((Number) claims.get("id")).longValue();
    }

}
