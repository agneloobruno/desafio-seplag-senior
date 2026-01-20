package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "regional")
@Data
@NoArgsConstructor
public class Regional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_externo", nullable = false)
    private Integer idExterno;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "data_sincronizacao")
    private LocalDateTime dataSincronizacao;

    public Regional(Integer idExterno, String nome, Boolean ativo) {
        this.idExterno = idExterno;
        this.nome = nome;
        this.ativo = ativo;
        this.dataSincronizacao = LocalDateTime.now();
    }
}