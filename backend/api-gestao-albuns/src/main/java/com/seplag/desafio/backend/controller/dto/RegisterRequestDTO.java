package com.seplag.desafio.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record RegisterRequestDTO(
        @NotBlank String login,
        @NotBlank String senha,
        @NotNull String role // Ou UserRole role
) {}