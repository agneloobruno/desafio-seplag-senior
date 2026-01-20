package com.seplag.desafio.backend.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                // CORREÇÃO 1: Ativar o CORS explicitamente e vincular ao Bean abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // CORREÇÃO 2: Permitir explicitamente requisições OPTIONS (Preflight)
                        // Isso impede que o Spring Security bloqueie a verificação do navegador com 403
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Rotas Públicas
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()

                        // Rotas Admin
                        .requestMatchers(HttpMethod.POST, "/artistas", "/albuns", "/musicas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/albuns/*/capa").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // CORREÇÃO 3: Bean de Configuração de CORS dedicado
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite a origem do Swagger (ajuste se necessário para sua porta frontend)
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:8080", "*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        // AllowCredentials(true) não pode ser usado com AllowedOrigins("*"),
        // mas é necessário se o swagger mandar cookies/auth headers complexos.
        // Se der erro de origem, troque o "*" por origens específicas.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}