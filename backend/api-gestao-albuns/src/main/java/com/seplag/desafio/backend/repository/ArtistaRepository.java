package com.seplag.desafio.backend.repository;

import com.seplag.desafio.backend.domain.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    // O Spring cria o SQL automaticamente para buscar por nome
    boolean existsByNome(String nome);
}