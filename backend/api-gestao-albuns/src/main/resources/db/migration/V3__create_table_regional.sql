CREATE TABLE regional (
                          id BIGSERIAL PRIMARY KEY,
                          id_externo INTEGER NOT NULL,
                          nome VARCHAR(200) NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          data_sincronizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regional_id_externo ON regional(id_externo);