package com.seplag.desafio.backend.infra.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    // Armazena os "baldes" dos usuários em memória
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Ignora rotas livres (Swagger e Auth) para não travar o login
        String path = request.getRequestURI();
        if (path.startsWith("/swagger") || path.startsWith("/v3/api-docs") || path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Tenta pegar o usuário logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String key;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Se logado, usa o login do usuário como chave
            key = auth.getName();
        } else {
            // Se anônimo (ou erro de auth), usa o IP
            key = request.getRemoteAddr();
        }

        // Pega ou cria o balde desse usuário
        Bucket bucket = buckets.computeIfAbsent(key, this::createNewBucket);

        // Tenta consumir 1 token
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429); // HTTP 429 - Too Many Requests
            response.getWriter().write("Muitas requisicoes. Tente novamente em 1 minuto.");
        }
    }

    private Bucket createNewBucket(String key) {
        // Regra: 10 requisições por 1 minuto
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}