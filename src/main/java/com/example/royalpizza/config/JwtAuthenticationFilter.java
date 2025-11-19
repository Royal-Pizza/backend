package com.example.royalpizza.config;

import com.example.royalpizza.exception.CustomerException;
import com.example.royalpizza.exception.ErrorMessages;
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

        try {
            String stok = authHeader;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // Vérifie l’expiration
                if (jwtTokenManager.isTokenExpired(token)) {
                    throw new CustomerException(ErrorMessages.EXPIRED_TOKEN);
                }

                // Parse et valide le token (vérifie la signature et extrait le customer)
                Long idCustomer = jwtTokenManager.parseToken(token);

                // Crée l’objet Authentication pour Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(idCustomer, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // Continue la chaîne
            filterChain.doFilter(request, response);

            // On intercepte ici les erreurs métier (ex: token expiré)
            // et on envoie une **réponse JSON propre** directement depuis le filtre,
            // car le GlobalExceptionHandler ne peut pas intercepter les exceptions
            // levées avant d’atteindre le contrôleur (Spring Security s’exécute avant Spring MVC).
        } catch (CustomerException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String json = String.format("{\"message\": \"%s\"}", e.getMessage());
            response.getWriter().write(json);

        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/customers/login")
                || path.equals("/customers/register")
                || path.startsWith("/pizzas/");
    }

}
