package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.AlbumRequestDTO;
import com.seplag.desafio.backend.controller.dto.AlbumResponseDTO;
import com.seplag.desafio.backend.domain.Album;
import com.seplag.desafio.backend.domain.Artista;
import com.seplag.desafio.backend.repository.AlbumRepository;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/albuns")
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;

    // Construtor manual (Injeção de dependência)
    public AlbumController(AlbumRepository albumRepository, ArtistaRepository artistaRepository) {
        this.albumRepository = albumRepository;
        this.artistaRepository = artistaRepository;
    }

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> create(@RequestBody AlbumRequestDTO data, UriComponentsBuilder uriBuilder) {
        // 1. Achar o artista pelo ID
        Artista artista = artistaRepository.findById(data.artistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        // 2. Criar o álbum ligado ao artista
        Album album = new Album(data.titulo(), data.ano(), artista);

        // 3. Salvar
        albumRepository.save(album);

        // 4. Retornar 201 Created
        var uri = uriBuilder.path("/albuns/{id}").buildAndExpand(album.getId()).toUri();
        return ResponseEntity.created(uri).body(new AlbumResponseDTO(album));
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDTO>> list() {
        var albuns = albumRepository.findAll();
        var dtos = albuns.stream().map(AlbumResponseDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }
}