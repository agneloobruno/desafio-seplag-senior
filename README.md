# 🎵 Sistema de Gestão de Álbuns Musicais (Desafio SEPLAG)

Este projeto é uma solução **Full Stack** desenvolvida como parte do processo seletivo para o cargo de Desenvolvedor Full Stack Sênior. O sistema permite a gestão de artistas, álbuns e músicas, com funcionalidades avançadas como upload de imagens, notificações em tempo real e sincronização de dados regionais.

---

## 🚀 Tecnologias Utilizadas

### Backend (Java/Spring Boot)
* **Java 21** & **Spring Boot 3+**
* **PostgreSQL**: Banco de dados relacional.
* **Flyway**: Versionamento e migração de banco de dados.
* **Spring Security + JWT**: Autenticação e Autorização.
* **MinIO**: Object Storage (S3 Compatible) para capas de álbuns.
* **WebSocket (STOMP)**: Notificações em tempo real.
* **Bucket4j**: Rate Limiting (Proteção de API).
* **Swagger (SpringDoc)**: Documentação da API.
* **JUnit 5 & Mockito**: Testes automatizados.

### Frontend (React)
* **React 19**
* **Vite**: Build tool e servidor de desenvolvimento.
* **TypeScript**: Tipagem estática para maior segurança.
* **Tailwind CSS**: Estilização moderna e responsiva.
* **Axios**: Cliente HTTP.
* **SockJS & StompJS**: Cliente WebSocket para notificações.
* **React Router DOM**: Roteamento SPA.

### Infraestrutura (DevOps)
* **Docker**: Containerização de todos os serviços.
* **Docker Compose**: Orquestração do ambiente (API, Frontend, BD, MinIO).
* **Nginx**: Servidor web para o Frontend (Multi-stage build).

---

## ✨ Funcionalidades Principais

1.  **Gestão de Artistas e Álbuns**:
    * Listagem paginada.
    * Filtros de busca.
    * Cadastro e Edição.
2.  **Upload de Imagens (MinIO)**:
    * Integração transparente para upload de capas.
    * Geração de URLs pré-assinadas (Presigned URLs) com expiração automática de 30 minutos.
3.  **Notificações em Tempo Real (WebSocket)**:
    * Alerta visual ("Toast") instantâneo no frontend para todos os usuários conectados quando um novo álbum é cadastrado.
4.  **Sincronização de Regionais**:
    * Rotina para integração com API externa de dados regionais.
5.  **Segurança e Performance**:
    * Login com JWT (Access Token + Refresh Token).
    * Rate Limiting por IP (10 req/min).

---

## 🛠️ Como Executar o Projeto

A aplicação está totalmente containerizada. Para rodar, você precisa apenas ter o **Docker** e o **Docker Compose** instalados.

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/desafio-seplag-senior.git](https://github.com/seu-usuario/desafio-seplag-senior.git)
    cd desafio-seplag-senior
    ```

2.  **Suba o ambiente:**
    Na raiz do projeto (onde está o arquivo `docker-compose.yml`), execute:
    ```bash
    docker-compose up --build -d
    ```
    *Aguarde alguns instantes para que o Maven (Backend) e o Node (Frontend) baixem as dependências e façam o build.*

3.  **Acesse a Aplicação:**
    * **Frontend (Aplicação Principal):** [http://localhost:3000](http://localhost:3000)
    * **Swagger UI (Documentação API):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    * **MinIO Console:** [http://localhost:9001](http://localhost:9001)

---

## 🔐 Credenciais Padrão

O sistema inicia com um usuário administrador criado automaticamente via migration (`V1__create_tables.sql`):

| Serviço | Usuário | Senha |
| :--- | :--- | :--- |
| **Sistema Web** | `admin` | `123` |
| **MinIO Console** | `minioadmin` | `minioadmin` |

---

## 📂 Estrutura do Projeto

```text
desafio-seplag-senior/
├── backend/                # API Java Spring Boot
│   ├── api-gestao-albuns/  # Código Fonte
│   │   ├── src/main/java   # Controllers, Services, Domain
│   │   └── Dockerfile      # Definição da imagem Java
├── frontend/               # Aplicação React
│   ├── src/                # Componentes, Páginas, Hooks
│   ├── Dockerfile          # Build Multi-stage (Node -> Nginx)
│   └── nginx.conf          # Configuração do servidor web
└── docker-compose.yml      # Orquestração dos serviços

## 🧪 Como Testar o WebSocket

1. Abra o sistema em duas abas do navegador (ou use uma aba anônima).

2. Faça login em ambas.

3. Na Aba 1, vá até um artista e cadastre um Novo Álbum.

4. Observe a Aba 2: Uma notificação ("Toast") aparecerá automaticamente no canto inferior direito, sem necessidade de recarregar a página.