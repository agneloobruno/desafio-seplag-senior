package com.seplag.desafio.backend.service;

import com.seplag.desafio.backend.controller.dto.ArtistaRequestDTO;
import com.seplag.desafio.backend.domain.Artista;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    private final ArtistaRepository repository;

    @Transactional
    public Artista criar(ArtistaRequestDTO data) {
        // Aqui poderíamos ter regras de negócio (ex: verificar se já existe artista com esse nome)
        Artista artista = new Artista();
        artista.setNome(data.nome());
        return repository.save(artista);
    }

    public Page<Artista> listar(Pageable pageable, String filtroNome) {
        if (filtroNome != null && !filtroNome.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(filtroNome, pageable);
        }
        return repository.findAll(pageable);
    }
}