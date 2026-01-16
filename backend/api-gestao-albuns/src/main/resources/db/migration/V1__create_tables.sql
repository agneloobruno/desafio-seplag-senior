-- Tabela de Artistas
CREATE TABLE artista (
                         id BIGSERIAL PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Álbuns
CREATE TABLE album (
                       id BIGSERIAL PRIMARY KEY,
                       titulo VARCHAR(255) NOT NULL,
                       capa_url VARCHAR(500),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Associação (Relacionamento N:N)
-- Um artista tem vários álbuns, um álbum pode ter vários artistas (feats)
CREATE TABLE artista_album (
                               artista_id BIGINT NOT NULL,
                               album_id BIGINT NOT NULL,
                               PRIMARY KEY (artista_id, album_id),
                               CONSTRAINT fk_artista FOREIGN KEY (artista_id) REFERENCES artista(id),
                               CONSTRAINT fk_album FOREIGN KEY (album_id) REFERENCES album(id)
);

-- Tabela de Regionais (Exigência Sênior - Sincronização)
CREATE TABLE regional (
                          id INTEGER PRIMARY KEY, -- ID vem da API externa, não é auto-incremento
                          nome VARCHAR(200),
                          ativo BOOLEAN DEFAULT TRUE,
                          last_sync TIMESTAMP
);