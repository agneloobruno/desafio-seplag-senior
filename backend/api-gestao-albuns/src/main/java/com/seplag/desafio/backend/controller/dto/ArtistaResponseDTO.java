package com.seplag.desafio.backend.controller.dto;

import com.seplag.desafio.backend.domain.Artista;

public record ArtistaResponseDTO(Long id, String nome, String fotoUrl) {
    public ArtistaResponseDTO(Artista artista, String fotoUrl) {
        this(artista.getId(), artista.getNome(), fotoUrl);
    }
}