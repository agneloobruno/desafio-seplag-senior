package com.seplag.desafio.backend.controller.dto;

import com.seplag.desafio.backend.domain.Album;

public record AlbumResponseDTO(Long id, String titulo, Integer ano, String capa, String capaUrl, String nomeArtista) {
    public AlbumResponseDTO(Album album, String capaUrlGerada) {
        this(album.getId(), album.getTitulo(), album.getAno(), album.getCapa(), capaUrlGerada, album.getArtista().getNome());
    }
}