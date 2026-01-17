package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.AlbumRequestDTO;
import com.seplag.desafio.backend.controller.dto.AlbumResponseDTO;
import com.seplag.desafio.backend.domain.Album;
import com.seplag.desafio.backend.domain.Artista;
import com.seplag.desafio.backend.repository.AlbumRepository;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import com.seplag.desafio.backend.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/albuns")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;
    private final MinioService minioService;

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> create(@RequestBody AlbumRequestDTO data, UriComponentsBuilder uriBuilder) {
        Artista artista = artistaRepository.findById(data.artistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        Album album = new Album(data.titulo(), data.ano(), artista);
        albumRepository.save(album);

        var uri = uriBuilder.path("/albuns/{id}").buildAndExpand(album.getId()).toUri();
        // Na criação, ainda não tem capa, então a URL é null
        return ResponseEntity.created(uri).body(new AlbumResponseDTO(album, null));
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDTO>> list() {
        var albuns = albumRepository.findAll();

        // Transformação mágica: Para cada álbum, gera a URL se tiver capa
        var dtos = albuns.stream().map(album -> {
            String url = null;
            if (album.getCapa() != null && !album.getCapa().isEmpty()) {
                url = minioService.getUrl(album.getCapa());
            }
            return new AlbumResponseDTO(album, url);
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/capa")
    public ResponseEntity<AlbumResponseDTO> uploadCapa(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        String nomeArquivo = minioService.upload(file);
        album.setCapa(nomeArquivo);
        albumRepository.save(album);

        // Gera a URL fresquinha para retornar na hora
        String url = minioService.getUrl(nomeArquivo);

        return ResponseEntity.ok(new AlbumResponseDTO(album, url));
    }
}