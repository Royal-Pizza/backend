package com.example.royalpizza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
}
