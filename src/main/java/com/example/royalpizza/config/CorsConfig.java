package com.example.royalpizza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class CorsConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public CorsConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configuration CORS pour autoriser Angular (localhost:4200)
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")                        // tous les endpoints
                        .allowedOrigins("http://localhost:4200")  // ton frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // méthodes autorisées
                        .allowedHeaders("*")                      // tous les headers autorisés
                        .allowCredentials(true);                 // si cookies/auth nécessaires
            }
        };
    }

    /**
     * Configuration Spring Security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // active la configuration CORS définie par WebMvcConfigurer
                .csrf(csrf -> csrf.disable()) // désactive CSRF pour API REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/pizzas/**",
                                "/customers/register",
                                "/customers/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());

        return http.build();
    }

}
