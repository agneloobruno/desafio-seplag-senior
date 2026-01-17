package com.seplag.desafio.backend.domain;

import jakarta.persistence.*;
import lombok.*; // Lombok ainda está aqui, mas estamos garantindo com código manual
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "usuario")
@Entity(name = "usuario")
@Getter
@Setter
// @NoArgsConstructor  <-- O Lombok falhou aqui
// @AllArgsConstructor <-- E aqui
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String senha;

    private String role;

    // --- 1. CONSTRUTOR VAZIO (OBRIGATÓRIO PRO HIBERNATE) ---
    public Usuario() {
    }

    // --- 2. CONSTRUTOR CHEIO (USADO NO REGISTER) ---
    public Usuario(String login, String senha, String role){
        this.login = login;
        this.senha = senha;
        this.role = role;
    }

    // ... Resto do código (getAuthorities, etc) continua igual ...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role != null && this.role.equalsIgnoreCase("ADMIN")) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
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
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}