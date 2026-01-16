package com.seplag.desafio.backend.service;

import com.seplag.desafio.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor; // <--- Importante
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // <--- Lombok gera o construtor com os argumentos 'final'
public class AuthorizationService implements UserDetailsService {

    // 'final' garante imutabilidade e obriga a injeção no construtor
    private final UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}