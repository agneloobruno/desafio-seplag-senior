package com.seplag.desafio.backend.repository;

import com.seplag.desafio.backend.domain.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    // Busca todas as regionais que estão ativas no momento
    List<Regional> findByAtivoTrue();

    // Busca uma regional específica ativa pelo ID externo (para validações pontuais)
    Optional<Regional> findByIdExternoAndAtivoTrue(Integer idExterno);
}