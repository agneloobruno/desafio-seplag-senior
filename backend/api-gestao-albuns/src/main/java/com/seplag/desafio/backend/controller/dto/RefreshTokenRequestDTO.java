package com.seplag.desafio.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank
        String refreshToken
) {}