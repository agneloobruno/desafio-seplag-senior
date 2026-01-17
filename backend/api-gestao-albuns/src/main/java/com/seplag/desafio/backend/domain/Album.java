package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Integer ano;

    private String capa; // Guardará o nome do arquivo ou URL da imagem

    @ManyToOne // Muitos álbuns podem pertencer a um Artista
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    // --- Construtores ---
    public Album() {} // Obrigatório pro Hibernate

    public Album(String titulo, Integer ano, Artista artista) {
        this.titulo = titulo;
        this.ano = ano;
        this.artista = artista;
    }

    // --- Getters e Setters Manuais ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getCapa() { return capa; }
    public void setCapa(String capa) { this.capa = capa; }

    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }
}