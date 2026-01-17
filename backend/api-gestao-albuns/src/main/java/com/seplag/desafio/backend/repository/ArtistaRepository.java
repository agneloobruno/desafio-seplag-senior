package com.seplag.desafio.backend.repository;

import com.seplag.desafio.backend.domain.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    boolean existsByNome(String nome);

    // Busca inteligente do Spring Data
    Page<Artista> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}