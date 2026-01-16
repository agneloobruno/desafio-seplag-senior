package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Artista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // Relacionamento com Album (Muitos para Muitos)
    @ManyToMany(mappedBy = "artistas")
    private List<Album> albuns;
}