package com.seplag.desafio.backend.controller.dto;

import com.seplag.desafio.backend.domain.Artista;

public record ArtistaResponseDTO(Long id, String nome) {
    // Construtor auxiliar para converter Entidade -> DTO
    public ArtistaResponseDTO(Artista artista) {
        this(artista.getId(), artista.getNome());
    }
}