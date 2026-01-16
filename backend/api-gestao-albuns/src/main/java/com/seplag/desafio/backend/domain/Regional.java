package com.seplag.desafio.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Regional {
    @Id
    private Integer id; // ID manual vindo da API externa

    private String nome;

    private Boolean ativo;
}