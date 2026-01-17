package com.seplag.desafio.backend.controller.dto;

import com.seplag.desafio.backend.domain.Musica;

public record MusicaResponseDTO (Long id, String titulo, Integer segundos, Long albumId) {

    public MusicaResponseDTO(Musica musica) {
        this(musica.getId(), musica.getTitulo(), musica.getSegundos(), musica.getAlbum().getId());
    }
}
