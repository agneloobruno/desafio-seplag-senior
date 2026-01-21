package com.seplag.desafio.backend.service;

import com.seplag.desafio.backend.controller.dto.ArtistaRequestDTO;
import com.seplag.desafio.backend.domain.Artista;
import com.seplag.desafio.backend.repository.ArtistaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito
class ArtistaServiceTest {

    @Mock // Cria um Repositório "Falso" (Mock)
    private ArtistaRepository repository;

    @InjectMocks // Injeta o Mock dentro do Service Real
    private ArtistaService service;

    @Test
    @DisplayName("Deve criar um artista com sucesso")
    void deveCriarArtistaComSucesso() {
        // ARRANGE (Cenário)
        ArtistaRequestDTO dto = new ArtistaRequestDTO("Legião Urbana");
        Artista artistaSalvo = new Artista(1L, "Legião Urbana", null);

        // Quando o repository.save for chamado com qualquer artista, retorne o 'artistaSalvo'
        when(repository.save(any(Artista.class))).thenReturn(artistaSalvo);

        // ACT (Ação)
        Artista resultado = service.criar(dto);

        // ASSERT (Validação)
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Legião Urbana", resultado.getNome());

        // Verifica se o método save foi chamado exatamente 1 vez
        verify(repository, times(1)).save(any(Artista.class));
    }

    @Test
    @DisplayName("Deve listar filtrando por nome quando filtro for informado")
    void deveListarComFiltro() {
        // ARRANGE
        Pageable pageable = Pageable.unpaged();
        String filtro = "Rock";
        Page<Artista> paginaMock = new PageImpl<>(List.of(new Artista(1L, "Capital Rock", null)));

        when(repository.findByNomeContainingIgnoreCase(filtro, pageable)).thenReturn(paginaMock);

        // ACT
        Page<Artista> resultado = service.listar(pageable, filtro);

        // ASSERT
        assertEquals(1, resultado.getTotalElements());
        // Verifica se chamou o método ESPECÍFICO de filtro, e não o findAll genérico
        verify(repository, times(1)).findByNomeContainingIgnoreCase(filtro, pageable);
        verify(repository, never()).findAll(pageable);
    }
}