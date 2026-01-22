package com.seplag.desafio.backend.controller;

import com.seplag.desafio.backend.domain.UserRole;
import com.seplag.desafio.backend.domain.Usuario;
import com.seplag.desafio.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminSeedController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${ADMIN_SEED_ENABLED:false}")
    private boolean adminSeedEnabled;

    @PostMapping("/seed")
    public ResponseEntity<Void> seedAdmin() {
        if (!adminSeedEnabled) {
            return ResponseEntity.status(403).build();
        }
        String adminLogin = "admin";
        String rawPassword = "123"; // senha conhecida

        var maybe = usuarioRepository.findByLogin(adminLogin);
        if (maybe.isPresent()) {
            var userDetails = maybe.get();
            if (userDetails instanceof Usuario) {
                Usuario u = (Usuario) userDetails;
                u.setSenha(passwordEncoder.encode(rawPassword));
                u.setRole(UserRole.ADMIN);
                usuarioRepository.save(u);
                return ResponseEntity.ok().build();
            }
        }

        Usuario newAdmin = Usuario.builder()
                .login(adminLogin)
                .senha(passwordEncoder.encode(rawPassword))
                .role(UserRole.ADMIN)
                .build();

        usuarioRepository.save(newAdmin);
        return ResponseEntity.ok().build();
    }
}
