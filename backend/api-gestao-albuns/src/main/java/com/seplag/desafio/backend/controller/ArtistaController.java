package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.ArtistaRequestDTO;
import com.seplag.desafio.backend.controller.dto.ArtistaResponseDTO;
import com.seplag.desafio.backend.service.ArtistaService; // Importa o Service
import com.seplag.desafio.backend.service.MinioService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import com.seplag.desafio.backend.service.MinioService;
import jakarta.validation.Valid; // Importante para validação automática
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/v1/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService service; // Injeta o Service, não o Repository
    private final ArtistaRepository artistaRepository;
    private final MinioService minioService;

    @PostMapping
    // Adicionei @Valid. Garanta que no DTO o campo nome tenha @NotBlank
    public ResponseEntity<ArtistaResponseDTO> create(@RequestBody @Valid ArtistaRequestDTO data, UriComponentsBuilder uriBuilder) {

        var artista = service.criar(data);

        var uri = uriBuilder.path("/artistas/{id}").buildAndExpand(artista.getId()).toUri();

        String url = null;
        if (artista.getFoto() != null && !artista.getFoto().isEmpty()) {
            url = minioService.getUrl(artista.getFoto());
        }

        return ResponseEntity.created(uri).body(new ArtistaResponseDTO(artista, url));
    }

    @GetMapping
    public ResponseEntity<Page<ArtistaResponseDTO>> list(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable paginacao,
            @RequestParam(required = false) String nome) {

        var pagina = service.listar(paginacao, nome);

        var dtoPage = pagina.map(artista -> {
            String url = null;
            if (artista.getFoto() != null && !artista.getFoto().isEmpty()) {
                url = minioService.getUrl(artista.getFoto());
            }
            return new ArtistaResponseDTO(artista, url);
        });

        return ResponseEntity.ok(dtoPage);
        return ResponseEntity.ok(pagina.map(artista -> {
            String fotoUrl = null;
            if (artista.getFoto() != null && !artista.getFoto().isEmpty()) {
                fotoUrl = minioService.getUrl(artista.getFoto());
            }
            return new ArtistaResponseDTO(artista, fotoUrl);
        }));
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistaResponseDTO> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        var artista = service.findById(id).orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        String fotoAntiga = artista.getFoto();

        String nomeArquivo = minioService.upload(file);
        artista.setFoto(nomeArquivo);
        service.save(artista);

        // tenta remover a foto antiga (se existir)
        if (fotoAntiga != null && !fotoAntiga.isBlank() && !fotoAntiga.equals(nomeArquivo)) {
            minioService.delete(fotoAntiga);
        }

        String url = minioService.getUrl(nomeArquivo);

        return ResponseEntity.ok(new ArtistaResponseDTO(artista, url));
        var artista = artistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        String nomeArquivo = minioService.upload(file);
        artista.setFoto(nomeArquivo);
        artistaRepository.save(artista);

        String fotoUrl = minioService.getUrl(nomeArquivo);

        return ResponseEntity.ok(new ArtistaResponseDTO(artista, fotoUrl));
    }
}