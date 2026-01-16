CREATE TABLE usuario (
                         id BIGSERIAL PRIMARY KEY,
                         login VARCHAR(100) NOT NULL UNIQUE,
                         senha VARCHAR(255) NOT NULL,
                         role VARCHAR(50) DEFAULT 'USER'
);

-- Vamos inserir um usuário 'admin' padrão para você conseguir logar depois.
-- A senha é '123' (mas aqui já está criptografada como hash BCrypt, que é o padrão de segurança)
INSERT INTO usuario (login, senha, role)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376ye.5.Z954YjRjET/o.s/i.pQ.M1M.Z/1.z6', 'ADMIN');