package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.controller.dto.LoginRequestDTO;
import com.seplag.desafio.backend.controller.dto.LoginResponseDTO;
import com.seplag.desafio.backend.domain.Usuario;
import com.seplag.desafio.backend.infra.security.TokenService;
import com.seplag.desafio.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        if (auth.getPrincipal() instanceof Usuario usuario) {
            var token = tokenService.generateToken(usuario);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // --- NOVO ENDPOINT DE REGISTRO ---
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody LoginRequestDTO data) {
        // Verifica se já existe
        if (this.repository.findByLogin(data.login()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }

        // Criptografa a senha antes de salvar
        String encryptedPassword = new String(passwordEncoder.encode(data.senha()));

        // Cria o usuário novo (Role padrão ADMIN para testarmos)
        Usuario newUser = new Usuario(data.login(), encryptedPassword, "ADMIN");

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}