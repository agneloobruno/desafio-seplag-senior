package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;
import lombok.*; // Importando Lombok

import java.util.List;

@Entity
@Table(name = "artista")
@Data // Faz Getters, Setters, ToString, Equals, HashCode
@NoArgsConstructor // Construtor vazio
@AllArgsConstructor // Construtor cheio
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Nome do arquivo da foto armazenado no MinIO
    private String foto;
    private String foto; // Nome do arquivo da foto no MinIO

    // Correção do mappedBy mantida aqui:
    @OneToMany(mappedBy = "artista", cascade = CascadeType.ALL)
    private List<Album> albuns;

    public Artista(Long id, String nome, String foto) {
        this.id = id;
        this.nome = nome;
        this.foto = foto;
    }
}