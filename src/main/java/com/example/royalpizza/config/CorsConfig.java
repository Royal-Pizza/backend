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

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")                  // tous les endpoints
                        .allowedOrigins("http://localhost:4200") // ton frontend
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // méthodes autorisées
                        .allowedHeaders("*")                  // headers autorisés
                        .allowCredentials(true);             // si tu utilises cookies ou auth
            }
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/pizzas/**",
                                "/customers/**"
                        ).permitAll() // endpoints publics
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()); // pas besoin d'authenticationEntryPoint ici
        return http.build();
    }
}
