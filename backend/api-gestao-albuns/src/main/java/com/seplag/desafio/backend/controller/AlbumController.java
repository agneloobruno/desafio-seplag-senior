package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.AlbumRequestDTO;
import com.seplag.desafio.backend.controller.dto.AlbumResponseDTO;
import com.seplag.desafio.backend.domain.Album;
import com.seplag.desafio.backend.domain.Artista;
import com.seplag.desafio.backend.repository.AlbumRepository;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import com.seplag.desafio.backend.service.MinioService;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@RestController
@RequestMapping("/v1/albuns")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;
    private final MinioService minioService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> create(@RequestBody AlbumRequestDTO data, UriComponentsBuilder uriBuilder) {
        Artista artista = artistaRepository.findById(data.artistaId())
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        Album album = new Album(data.titulo(), data.ano(), artista);
        albumRepository.save(album);

        // --- LÓGICA WEBSOCKET ---
        String mensagem = "Novo álbum lançado: " + album.getTitulo() + " (" + album.getArtista().getNome() + ")";
        messagingTemplate.convertAndSend("/topic/albuns", mensagem);
        // ------------------------

        var uri = uriBuilder.path("/albuns/{id}").buildAndExpand(album.getId()).toUri();
        // Na criação, ainda não tem capa, então a URL é null
        return ResponseEntity.created(uri).body(new AlbumResponseDTO(album, null));
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> list(
            @RequestParam(required = false) Long artistaId,
            @PageableDefault(size = 10, sort = "ano", direction = Sort.Direction.DESC) Pageable paginacao) {

        Page<Album> pagina;

        if (artistaId != null) {
            pagina = albumRepository.findByArtistaId(artistaId, paginacao);
        } else {
            pagina = albumRepository.findAll(paginacao);
        }

        // Converte para DTO e gera URL da capa (MinIO)
        var dtoPagina = pagina.map(album -> {
            String url = null;
            if (album.getCapa() != null && !album.getCapa().isEmpty()) {
                url = minioService.getUrl(album.getCapa());
            }
            return new AlbumResponseDTO(album, url);
        });

        return ResponseEntity.ok(dtoPagina);
    }

    @PostMapping(value = "/{id}/capa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponseDTO> uploadCapa(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));
        String arquivoAntigo = album.getCapa();

        String nomeArquivo = minioService.upload(file);
        album.setCapa(nomeArquivo);
        albumRepository.save(album);

        // tenta remover a antiga capa (não falha se der erro)
        if (arquivoAntigo != null && !arquivoAntigo.isBlank() && !arquivoAntigo.equals(nomeArquivo)) {
            minioService.delete(arquivoAntigo);
        }

        String url = minioService.getUrl(nomeArquivo);

        return ResponseEntity.ok(new AlbumResponseDTO(album, url));
    }

    @PutMapping(value = "/{id}/capa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponseDTO> updateCapa(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        String nomeArquivo = minioService.upload(file);
        album.setCapa(nomeArquivo);
        albumRepository.save(album);

        String url = minioService.getUrl(nomeArquivo);

        return ResponseEntity.ok(new AlbumResponseDTO(album, url));
    }
}