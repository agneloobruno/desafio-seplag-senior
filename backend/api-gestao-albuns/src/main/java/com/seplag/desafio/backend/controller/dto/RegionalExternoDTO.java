package com.seplag.desafio.backend.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegionalExternoDTO(
        @JsonProperty("id") Integer id,
        @JsonProperty("nome") String nome
) {}