package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.ArtistaRequestDTO;
import com.seplag.desafio.backend.controller.dto.ArtistaResponseDTO;
import com.seplag.desafio.backend.service.ArtistaService; // Importa o Service
import jakarta.validation.Valid; // Importante para validação automática
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

    private final ArtistaService service; // Injeta o Service, não o Repository

    @PostMapping
    // Adicionei @Valid. Garanta que no DTO o campo nome tenha @NotBlank
    public ResponseEntity<ArtistaResponseDTO> create(@RequestBody @Valid ArtistaRequestDTO data, UriComponentsBuilder uriBuilder) {

        var artista = service.criar(data);

        var uri = uriBuilder.path("/artistas/{id}").buildAndExpand(artista.getId()).toUri();

        return ResponseEntity.created(uri).body(new ArtistaResponseDTO(artista));
    }

    @GetMapping
    public ResponseEntity<Page<ArtistaResponseDTO>> list(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable paginacao,
            @RequestParam(required = false) String nome) {

        var pagina = service.listar(paginacao, nome);

        return ResponseEntity.ok(pagina.map(ArtistaResponseDTO::new));
    }
}