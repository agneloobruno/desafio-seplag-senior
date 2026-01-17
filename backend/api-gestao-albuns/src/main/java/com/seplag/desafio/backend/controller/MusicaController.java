package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.MusicaRequestDTO;
import com.seplag.desafio.backend.controller.dto.MusicaResponseDTO;
import com.seplag.desafio.backend.domain.Album;
import com.seplag.desafio.backend.domain.Musica;
import com.seplag.desafio.backend.repository.AlbumRepository;
import com.seplag.desafio.backend.repository.MusicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/musicas")
@RequiredArgsConstructor
public class MusicaController {

    private final MusicaRepository musicaRepository;
    private final AlbumRepository albumRepository;

    @PostMapping
    public ResponseEntity<MusicaResponseDTO> create(@RequestBody MusicaRequestDTO data, UriComponentsBuilder uriBuilder) {
        Album album = albumRepository.findById(data.albumId())
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        Musica musica = new Musica();
        musica.setTitulo(data.titulo());
        musica.setSegundos(data.segundos());
        musica.setAlbum(album);

        musicaRepository.save(musica);

        var uri = uriBuilder.path("/musicas/{id}").buildAndExpand(musica.getId()).toUri();

        return ResponseEntity.created(uri).body(new MusicaResponseDTO(musica));
    }

    @GetMapping
    public ResponseEntity<List<MusicaResponseDTO>> list() {
        var musicas = musicaRepository.findAll();

        var dtos = musicas.stream().map(MusicaResponseDTO::new).toList();

        return ResponseEntity.ok(dtos);
    }
}