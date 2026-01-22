package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.*;
import com.seplag.desafio.backend.domain.UserRole;
import com.seplag.desafio.backend.domain.Usuario; // <--- O IMPORT ESSENCIAL PARA O SAVE FUNCIONAR
import com.seplag.desafio.backend.infra.security.TokenService;
import com.seplag.desafio.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());

        // 1. Autentica o usuário (chama o UserDetailsService por baixo dos panos)
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 2. Faz o cast explícito de UserDetails (interface) para Usuario (nossa entidade)
        // Isso é seguro porque nosso UserDetailsService retorna um objeto do tipo Usuario
        var usuario = (Usuario) auth.getPrincipal();

        var token = tokenService.generateToken(usuario);
        var refreshToken = tokenService.generateRefreshToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO data) {
        // Verifica se já existe (findByLogin retorna UserDetails, verificamos se o Optional está cheio)
        if (this.usuarioRepository.findByLogin(data.login()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        // --- Lógica de conversão segura String -> Enum ---
        UserRole role;
        try {
            // Se role vier nulo ou vazio, padrão é USER
            String roleStr = data.role() == null || data.role().isBlank() ? "USER" : data.role();
            // Tenta converter o que veio no JSON (ex: "admin") para o Enum (UserRole.ADMIN)
            role = UserRole.valueOf(roleStr.toUpperCase());
        } catch (Exception e) {
            // Se vier qualquer coisa inválida, força ser USER comum
            role = UserRole.USER;
        }

        // --- Criação do Usuário usando Builder (Lombok) ---
        Usuario newUser = Usuario.builder()
                .login(data.login())
                .senha(encryptedPassword)
                .role(role)
                .build();

        // Agora o tipo 'newUser' é exatamente o 'Usuario' que o Repository espera
        this.usuarioRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO data) {
        String login = tokenService.validateToken(data.refreshToken());

        if (login.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        // Busca o usuário. Como findByLogin retorna UserDetails, precisamos fazer o cast para Usuario
        // para passar para o tokenService.generateToken
        UserDetails userDetails = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!(userDetails instanceof Usuario)) {
            throw new RuntimeException("Tipo de usuário inválido no contexto de segurança");
        }

        var newAccessToken = tokenService.generateToken((Usuario) userDetails);

        return ResponseEntity.ok(new RefreshTokenResponseDTO(newAccessToken));
    }
}