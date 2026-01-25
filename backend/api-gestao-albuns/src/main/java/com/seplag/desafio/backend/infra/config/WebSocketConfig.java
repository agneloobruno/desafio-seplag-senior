package com.seplag.desafio.backend.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita um broker simples em memória para enviar mensagens aos clientes
        config.enableSimpleBroker("/topic");
        // Prefixo para mensagens que vão DO cliente PARA o servidor (se houver)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Define o endpoint de conexão (Handshake)
        registry.addEndpoint("/ws-albuns")
            // Permite somente o dev-server do frontend e localhosts listados
            .setAllowedOriginPatterns("http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:8080")
            .withSockJS(); // Habilita fallback para navegadores antigos
    }
}
