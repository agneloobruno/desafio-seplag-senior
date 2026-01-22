package com.seplag.desafio.backend.repository;

import com.seplag.desafio.backend.domain.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    // Novo método para buscar por artista com paginação
    Page<Album> findByArtistaId(Long artistaId, Pageable pageable);
}