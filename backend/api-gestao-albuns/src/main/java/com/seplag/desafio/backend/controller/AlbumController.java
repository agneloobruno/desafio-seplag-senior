package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.AlbumRequestDTO;
import com.seplag.desafio.backend.controller.dto.AlbumResponseDTO;
import com.seplag.desafio.backend.domain.Album;
import com.seplag.desafio.backend.domain.Artista;
import com.seplag.desafio.backend.repository.AlbumRepository;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import com.seplag.desafio.backend.service.MinioService; // <--- Import Novo
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // <--- Import Novo
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/albuns")
@RequiredArgsConstructor // Lombok faz os construtores pra nós
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;
    private final MinioService minioService; // <--- Injetando o serviço de upload

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> create(@RequestBody AlbumRequestDTO data, UriComponentsBuilder uriBuilder) {
        Artista artista = artistaRepository.findById(data.artistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        Album album = new Album(data.titulo(), data.ano(), artista);
        albumRepository.save(album);

        var uri = uriBuilder.path("/albuns/{id}").buildAndExpand(album.getId()).toUri();
        return ResponseEntity.created(uri).body(new AlbumResponseDTO(album));
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDTO>> list() {
        var albuns = albumRepository.findAll();
        var dtos = albuns.stream().map(AlbumResponseDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    // --- NOVO ENDPOINT: Upload da Capa ---
    @PostMapping("/{id}/capa")
    public ResponseEntity<AlbumResponseDTO> uploadCapa(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // 1. Buscar o álbum
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        // 2. Fazer upload pro MinIO e pegar o nome do arquivo
        String nomeArquivo = minioService.upload(file);

        // 3. Atualizar o nome da capa no banco
        album.setCapa(nomeArquivo);
        albumRepository.save(album);

        return ResponseEntity.ok(new AlbumResponseDTO(album));
    }
}