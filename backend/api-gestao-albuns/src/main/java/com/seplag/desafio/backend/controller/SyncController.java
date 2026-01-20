package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.service.RegionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {

    private final RegionalService regionalService;

    @PostMapping("/regionais")
    public ResponseEntity<String> sync() {
        regionalService.sincronizarRegionais();
        return ResponseEntity.ok("Sincronização solicitada com sucesso.");
    }
}