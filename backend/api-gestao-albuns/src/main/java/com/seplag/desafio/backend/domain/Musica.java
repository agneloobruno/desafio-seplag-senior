package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "musica")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Musica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Integer segundos;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;
}