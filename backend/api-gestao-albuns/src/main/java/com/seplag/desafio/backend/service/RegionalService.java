package com.seplag.desafio.backend.service;

import com.seplag.desafio.backend.controller.dto.RegionalExternoDTO;
import com.seplag.desafio.backend.domain.Regional;
import com.seplag.desafio.backend.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalService {

    private final RegionalRepository repository;
    private final RestClient.Builder restClientBuilder;

    private static final String API_URL = "https://integrador-argus-api.geia.vip/v1/regionais";

    @Transactional
    public void sincronizarRegionais() {
        log.info("Iniciando sincronização de regionais...");

        // 1. Buscar dados da API Externa
        List<RegionalExternoDTO> externas;
        try {
            externas = restClientBuilder.build()
                    .get()
                    .uri(API_URL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Falha ao conectar na API de regionais: {}", e.getMessage());
            // Em cenário real, poderia lançar erro ou não fazer nada para não parar o sistema
            return;
        }

        if (externas == null) return;

        // 2. Buscar estado atual do banco (apenas os ativos)
        // Transformar em Map para busca O(1) -> Performance Sênior
        Map<Integer, Regional> regionaisAtuaisMap = repository.findByAtivoTrue().stream()
                .collect(Collectors.toMap(Regional::getIdExterno, Function.identity()));

        List<Regional> paraSalvar = new ArrayList<>();

        // 3. Processar Lógica
        for (RegionalExternoDTO externa : externas) {
            Regional atual = regionaisAtuaisMap.get(externa.id());

            if (atual == null) {
                // CASO 1: Novo no endpoint -> Inserir
                log.info("Nova regional encontrada: {}", externa.nome());
                paraSalvar.add(new Regional(externa.id(), externa.nome(), true));
            } else {
                // Remove do mapa para sabermos quem SOBROU (quem não veio na API)
                regionaisAtuaisMap.remove(externa.id());

                if (!atual.getNome().equals(externa.nome())) {
                    // CASO 3: Atributo alterado -> Inativar antigo e criar novo
                    log.info("Regional alterada: {} -> {}", atual.getNome(), externa.nome());

                    atual.setAtivo(false); // Inativa o velho
                    paraSalvar.add(atual); // Salva a inativação

                    // Cria o novo
                    paraSalvar.add(new Regional(externa.id(), externa.nome(), true));
                }
            }
        }

        // CASO 2: Não disponível no endpoint -> Inativar local
        for (Regional sobrando : regionaisAtuaisMap.values()) {
            log.info("Regional removida na origem: {}", sobrando.getNome());
            sobrando.setAtivo(false);
            paraSalvar.add(sobrando);
        }

        repository.saveAll(paraSalvar);
        log.info("Sincronização concluída. {} registros processados.", paraSalvar.size());
    }
}