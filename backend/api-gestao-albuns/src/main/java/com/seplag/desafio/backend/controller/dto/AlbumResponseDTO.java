package com.seplag.desafio.backend.controller.dto;

import com.seplag.desafio.backend.domain.Album;

import java.util.List;

public record AlbumResponseDTO(
        Long id,
        String titulo,
        Integer ano,
        String capa,
        String capaUrl,
        String nomeArtista,
        List<MusicaResponseDTO> musicas
) {
    public AlbumResponseDTO(Album album, String capaUrlGerada) {
        this(
                album.getId(),
                album.getTitulo(),
                album.getAno(),
                album.getCapa(),
                capaUrlGerada,
                album.getArtista().getNome(),
                album.getMusicas() != null
                    ? album.getMusicas().stream().map(MusicaResponseDTO::new).toList()
                    : List.of()
        );
    }
}