package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.*;
import com.seplag.desafio.backend.domain.Usuario;
import com.seplag.desafio.backend.infra.security.TokenService;
import com.seplag.desafio.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository; // 1. Renomeei de 'repository' para 'usuarioRepository' para bater com o resto

    // Removi o PasswordEncoder injetado aqui pois usaremos 'new BCryptPasswordEncoder()'
    // ou você pode injetá-lo se preferir, mas mantive simples.

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 2. Correção da dupla definição: Usamos apenas o cast direto, pois o authenticate já garante que é um usuário válido
        var usuario = (Usuario) auth.getPrincipal();

        var token = tokenService.generateToken(usuario);
        var refreshToken = tokenService.generateRefreshToken(usuario); // Adicionado conforme planejado

        return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO data) {
        if (this.usuarioRepository.findByLogin(data.login()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        Usuario newUser = new Usuario(data.login(), encryptedPassword, data.role());

        this.usuarioRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO data) {
        String login = tokenService.validateToken(data.refreshToken());

        if (login.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        // 3. Agora funciona porque o campo lá em cima chama 'usuarioRepository'
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var newAccessToken = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new RefreshTokenResponseDTO(newAccessToken));
    }
}