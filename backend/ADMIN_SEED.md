# Admin seed (seguro)

O endpoint `/v1/admin/seed` foi adicionado para recriar ou restaurar o usuário `admin` com senha conhecida `123`.

Por motivos de segurança o endpoint NÃO é público por padrão. Ele está protegido de duas formas:

- removida a permissão `permitAll` no `SecurityConfig`;
- o próprio endpoint só executa se a variável de ambiente `ADMIN_SEED_ENABLED` estiver ativa (true).

Como usar (passo a passo):

1. Habilitar temporariamente o seed em `docker-compose.yml` (ou passar variável no ambiente do container):

```yaml
services:
  api:
    environment:
      - ADMIN_SEED_ENABLED=true
      # ... outras variáveis
```

2. Reiniciar a stack para aplicar a variável (no diretório raiz do projeto):

```bash
docker compose up --build -d
```

3. Executar o seed (apenas uma vez):

```bash
curl -X POST http://localhost:8080/v1/admin/seed
```

4. Verificar login:

```bash
curl -H "Content-Type: application/json" -d '{"login":"admin","senha":"123"}' http://localhost:8080/v1/auth/login
```

5. Desabilitar a variável `ADMIN_SEED_ENABLED` (remover da `docker-compose.yml` ou configurar para `false`) e reiniciar a stack — NUNCA deixe esse flag `true` em produção.

Alternativa rápida (sem editar `docker-compose.yml`):

- Você pode executar apenas o container `api` com a variável no CLI para rodar o seed manualmente, por exemplo:

```bash
# executa em foreground (útil para debug)
ADMIN_SEED_ENABLED=true docker compose up --build api
# ou, para executar em um container temporário e chamar o seed via curl:
docker compose run -e ADMIN_SEED_ENABLED=true api sh -c "curl -s -X POST http://localhost:8080/v1/admin/seed"
```

Se você definiu `ADMIN_SEED_SECRET` (recomendado), inclua o header `X-ADMIN-SEED-SECRET` na requisição:

```bash
ADMIN_SEED_ENABLED=true ADMIN_SEED_SECRET=MinhaSenhaSecreta docker compose up --build api
curl -X POST http://localhost:8080/v1/admin/seed -H "X-ADMIN-SEED-SECRET: MinhaSenhaSecreta"
```

Notas de segurança:
- Habilite o seed somente em ambiente de desenvolvimento/local.
- Após uso, volte a variável para `false`/remova e reinicie os serviços.
- Considere proteger esse endpoint via rede interna, chave administrativa ou provisionamento seguro se precisar usá-lo em staging.
