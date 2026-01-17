package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.ArtistaRequestDTO;
import com.seplag.desafio.backend.controller.dto.ArtistaResponseDTO;
import com.seplag.desafio.backend.domain.Artista;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaRepository repository;

    @PostMapping
    public ResponseEntity<ArtistaResponseDTO> create(@RequestBody ArtistaRequestDTO data, UriComponentsBuilder uriBuilder) {
        if (data.nome() == null || data.nome().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Artista artista = new Artista();
        artista.setNome(data.nome());

        repository.save(artista);

        var uri = uriBuilder.path("/artistas/{id}").buildAndExpand(artista.getId()).toUri();

        return ResponseEntity.created(uri).body(new ArtistaResponseDTO(artista));
    }

    @GetMapping
    public ResponseEntity<Page<ArtistaResponseDTO>> list(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable paginacao,
            @RequestParam(required = false) String nome) {

        Page<Artista> pagina;

        if (nome != null && !nome.isBlank()) {
            pagina = repository.findByNomeContainingIgnoreCase(nome, paginacao);
        } else {
            pagina = repository.findAll(paginacao);
        }

        return ResponseEntity.ok(pagina.map(ArtistaResponseDTO::new));
    }
}