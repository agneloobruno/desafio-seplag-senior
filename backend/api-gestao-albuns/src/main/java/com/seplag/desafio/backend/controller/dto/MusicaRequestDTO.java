package com.seplag.desafio.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MusicaRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        Integer segundos,

        @NotNull(message = "O ID do álbum é obrigatório")
        Long albumId
) {
}