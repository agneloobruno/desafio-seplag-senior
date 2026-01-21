package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "usuario")
@Entity(name = "usuario")
@Data // Faz Getter, Setter, Equals, HashCode e toString
@NoArgsConstructor // Construtor vazio (obrigatório pro JPA/Hibernate)
@AllArgsConstructor // Construtor cheio (útil para testes/builder)
@Builder // Padrão de criação elegante (Usuario.builder().login(...).build())
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING) // Grava no banco como texto ("ADMIN"), não número (0)
    @Column(nullable = false)
    private UserRole role;

    // --- Lógica de Segurança (UserDetails) ---
    // Esta parte NÃO PODE ser Lombok, pois é regra de negócio da interface

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Padrão Sênior: Se for ADMIN, tem permissão de ADMIN e USER. Se for USER, só USER.
        // Sem prefixo "ROLE_" (conforme configuramos no SecurityConfig)
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("USER"));
        }
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}