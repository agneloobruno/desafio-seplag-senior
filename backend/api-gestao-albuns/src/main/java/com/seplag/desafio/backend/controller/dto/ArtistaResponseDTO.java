package com.seplag.desafio.backend.controller.dto;

import com.seplag.desafio.backend.domain.Artista;

public record ArtistaResponseDTO(Long id, String nome, String fotoUrl) {
    public ArtistaResponseDTO(Artista artista, String fotoUrlGerada) {
        this(artista.getId(), artista.getNome(), fotoUrlGerada);
    }
    
    public ArtistaResponseDTO(Artista artista) {
        this(artista.getId(), artista.getNome(), null);
    }
}