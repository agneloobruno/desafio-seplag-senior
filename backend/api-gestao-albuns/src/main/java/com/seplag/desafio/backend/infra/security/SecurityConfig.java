package com.seplag.desafio.backend.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// Indica ao Spring que esta é uma classe de configuração onde definiremos Beans
@Configuration
// Habilita a segurança Web padrão do Spring Security e permite customizá-la
@EnableWebSecurity
public class SecurityConfig {

    // Injeta automaticamente a nossa classe de filtro de Token (SecurityFilter)
    @Autowired
    private SecurityFilter securityFilter;

    // Injeta automaticamente a nossa classe de filtro de limite de requisições (RateLimitFilter)
    @Autowired // <--- 1. INJEÇÃO NOVA
    private RateLimitFilter rateLimitFilter;

    // Define o Bean principal que configura toda a cadeia de segurança (regras de acesso, filtros, etc.)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Inicia a configuração do objeto HttpSecurity
        http
                // Desabilita a proteção contra CSRF (Cross-Site Request Forgery) pois usaremos Tokens, não Cookies de sessão
                .csrf(csrf -> csrf.disable())
                // Ativa a configuração de CORS e diz para usar a configuração definida no método 'corsConfigurationSource' abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Define a política de sessão como STATELESS: o servidor não guardará estado (sessão) do usuário na memória
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Inicia a definição de quais rotas (URLs) precisam de autenticação e quais são públicas
                .authorizeHttpRequests(authorize -> authorize
                        // Permite explicitamente o método OPTIONS para todas as rotas (vital para o navegador fazer o 'Preflight' do CORS sem erro 403)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Libera o acesso total (sem login) para as rotas de documentação do Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Libera o acesso para a rota de Login (para o usuário conseguir gerar o token)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // Libera o acesso para a rota de Registro (para criar conta)
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        // Libera o acesso para a rota de Refresh Token (para renovar acesso quando o token expira)
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()

                        // Define que apenas usuários com perfil 'ADMIN' podem cadastrar (POST) artistas, álbuns e músicas
                        .requestMatchers(HttpMethod.POST, "/artistas", "/albuns", "/musicas").hasRole("ADMIN")
                        // Define que apenas ADMIN pode fazer upload de capa de álbum
                        .requestMatchers(HttpMethod.POST, "/albuns/*/capa").hasRole("ADMIN")

                        // Diz que qualquer outra requisição não listada acima exige que o usuário esteja autenticado
                        .anyRequest().authenticated()
                )
                // Adiciona o nosso filtro de validação de Token (SecurityFilter) ANTES do filtro padrão de login do Spring
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                // Adiciona o filtro de Rate Limit DEPOIS do filtro de segurança (para limitar o usuário já identificado pelo token)
                .addFilterAfter(rateLimitFilter, SecurityFilter.class);

        // Constrói o objeto de segurança final e o retorna para o Spring
        return http.build();
    }

    // Define um Bean que contém as configurações globais de CORS (quem pode chamar nossa API)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Cria uma nova instância de configuração
        CorsConfiguration configuration = new CorsConfiguration();
        // Define quais origens (domínios) são permitidas. '*' aceita qualquer lugar (bom para dev, cuidado em prod)
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:8080", "*"));
        // Define quais métodos HTTP (verbos) o navegador pode usar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        // Define quais cabeçalhos o navegador pode enviar (Authorization é essencial para o Bearer Token)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "x-auth-token"));
        // Define quais cabeçalhos da resposta o navegador/frontend consegue ler
        configuration.setExposedHeaders(List.of("x-auth-token"));

        // Cria a fonte de configuração baseada em URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica essas regras de CORS para todas as rotas (/**) da aplicação
        source.registerCorsConfiguration("/**", configuration);
        // Retorna a configuração pronta
        return source;
    }

    // Expõe o AuthenticationManager como um Bean para podermos injetá-lo no AuthController (usado para validar login/senha)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Pega o gerenciador padrão do Spring
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Define o algoritmo de criptografia de senha (BCrypt) que será usado para salvar e verificar senhas no banco
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Retorna uma instância do BCrypt
        return new BCryptPasswordEncoder();
    }
}