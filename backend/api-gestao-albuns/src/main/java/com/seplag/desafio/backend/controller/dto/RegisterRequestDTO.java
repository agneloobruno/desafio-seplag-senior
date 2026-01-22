package com.seplag.desafio.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;


public record RegisterRequestDTO(
        @NotBlank String login,
        @NotBlank String senha,
        String role // Opcional, padrão é "USER"
) {}