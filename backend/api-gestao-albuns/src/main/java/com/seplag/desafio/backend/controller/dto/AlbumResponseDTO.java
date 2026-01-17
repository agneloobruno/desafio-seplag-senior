package com.seplag.desafio.backend.controller.dto;

import com.seplag.desafio.backend.domain.Album;

public record AlbumResponseDTO(Long id, String titulo, Integer ano, String capa, String nomeArtista) {
    public AlbumResponseDTO(Album album) {
        this(album.getId(), album.getTitulo(), album.getAno(), album.getCapa(), album.getArtista().getNome());
    }
}