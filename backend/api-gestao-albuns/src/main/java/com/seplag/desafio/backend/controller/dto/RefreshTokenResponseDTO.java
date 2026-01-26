package com.seplag.desafio.backend.controller.dto;

public record RefreshTokenResponseDTO(String token, String refreshToken) {
    public RefreshTokenResponseDTO(String token) {
        this(token, null);
    }
}