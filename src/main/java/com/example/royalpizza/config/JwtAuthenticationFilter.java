package com.example.royalpizza.config;

import com.example.royalpizza.entity.Customer;
import com.example.royalpizza.exception.CustomerException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;

    public JwtAuthenticationFilter(JwtTokenManager jwtTokenManager) {
        this.jwtTokenManager = jwtTokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Vérifie l’expiration
                if (jwtTokenManager.isTokenExpired(token)) {
                    throw new CustomerException("Le token a expiré, veuillez vous reconnecter");
                }

                // Parse et valide le token (vérifie la signature et extrait le customer)
                Long idCustomer = jwtTokenManager.parseToken(token);

                // Crée l’objet Authentication pour Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(idCustomer, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (CustomerException e) {
                // Laisse le GlobalExceptionHandler gérer le message proprement
                throw e;
            } catch (Exception e) {
                // Autre erreur inattendue
                throw new CustomerException("Token invalide ou corrompu");
            }
        }

        filterChain.doFilter(request, response);
    }
}
