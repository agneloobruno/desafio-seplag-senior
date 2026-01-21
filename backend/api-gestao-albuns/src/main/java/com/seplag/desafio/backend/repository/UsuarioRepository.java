package com.seplag.desafio.backend.repository;

import com.seplag.desafio.backend.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método usado pelo Spring Security para carregar o usuário no login
    Optional<UserDetails> findByLogin(String login);

    // Método auxiliar caso precisemos buscar a entidade Usuario pura (não UserDetails)
    // Optional<Usuario> findUsuarioByLogin(String login);
    // (Opcional, geralmente o findByLogin já resolve se fizermos o cast, mas o de cima retorna UserDetails)
}