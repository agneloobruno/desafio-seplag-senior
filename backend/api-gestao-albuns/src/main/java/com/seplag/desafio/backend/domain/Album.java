package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@Getter
@Setter
@NoArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private Integer ano;
    private String capa; // Nome do arquivo no MinIO

    @ManyToOne
    @JoinColumn(name = "artista_id")
    private Artista artista;

    // --- NOVO: Mapeamento da Lista de Músicas ---
    // mappedBy = "album": Refere-se ao campo 'album' na classe Musica
    // CascadeType.ALL: Se deletar o álbum, deleta as músicas
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Musica> musicas = new ArrayList<>();

    public Album(String titulo, Integer ano, Artista artista) {
        this.titulo = titulo;
        this.ano = ano;
        this.artista = artista;
    }
}