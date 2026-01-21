-- 1. Tabela de Artistas
CREATE TABLE artista (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de Álbuns
-- (Adicionei 'ano', 'artista_id' e renomeei 'capa_url' para 'capa' para evitar erros futuros)
CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ano INTEGER,
    capa VARCHAR(500),
    artista_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_album_artista FOREIGN KEY (artista_id) REFERENCES artista(id)
);

-- 3. Tabela de Músicas (O que estava causando o seu erro atual!)
CREATE TABLE musica (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    segundos INTEGER NOT NULL,
    album_id BIGINT NOT NULL,
    CONSTRAINT fk_musica_album FOREIGN KEY (album_id) REFERENCES album(id)
);

-- 4. Tabela de Associação (Feats)
CREATE TABLE artista_album (
    artista_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artista_id, album_id),
    CONSTRAINT fk_artista FOREIGN KEY (artista_id) REFERENCES artista(id),
    CONSTRAINT fk_album FOREIGN KEY (album_id) REFERENCES album(id)
);

-- 5. Tabela de Usuários (Necessária para o login funcionar)
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER'
);

-- Usuário admin padrão (senha: 123)
INSERT INTO usuario (login, senha, role)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376ye.5.Z954YjRjET/o.s/i.pQ.M1M.Z/1.z6', 'ADMIN');

-- 6. Tabela de Regionais
CREATE TABLE regional (
    id BIGSERIAL PRIMARY KEY,
    id_externo INTEGER NOT NULL,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_sincronizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_regional_id_externo ON regional(id_externo);