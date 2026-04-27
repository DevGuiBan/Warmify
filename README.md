# Warmify

API backend e front-end para **aquecimento de perfis e paginas do Facebook e Instagram**, com controle de usuarios, autenticacao JWT e base pronta para evolucao de automacoes sociais.

## Objetivo do projeto

O Warmify nasce para organizar e automatizar o processo de aquecimento de perfis e paginas do Facebook de forma segura e escalavel.

Na pratica, a aplicacao busca:
- centralizar contas e dados de aquecimento;
- controlar acesso de operadores e administradores;
- registrar e preparar estruturas para rotinas de aquecimento;
- permitir evolucao futura para multiplos dominios sociais;
- oferecer um painel web unico para gerenciar Facebook, Instagram, portfolios, domínios e recuperações.

## Stack utilizada

- Java 21
- Spring Boot (Web, Security, Data JPA)
- PostgreSQL
- Flyway (migrations)
- JWT (`java-jwt`)
- Swagger/OpenAPI (`springdoc-openapi`)
- Front-end estático em `src/main/resources/static`

## Requisitos

- Java 21+
- Maven (ou usar `./mvnw`)
- PostgreSQL rodando localmente

## Configuracao

Arquivo: `src/main/resources/application.properties`

Configuracoes atuais:
- `spring.datasource.url=jdbc:postgresql://localhost:5432/warmify`
- `spring.datasource.username=postgres`
- `spring.datasource.password=161003`
- `api.security.token.secret=${JWT_SECRET:my-secret-key}`

> Recomendado: definir `JWT_SECRET` via variavel de ambiente em vez de usar o valor padrao.

## Como executar

```sh
cd /home/guiban/Documentos/git/Warmify
./mvnw spring-boot:run
```

A API sobe por padrao em `http://localhost:8080`.

## Front-end

O projeto agora inclui uma SPA estática servida pelo próprio Spring Boot.

### Acesso

- Interface principal: `http://localhost:8080/`
- Se a API estiver em outra origem, a tela de login permite informar a base da API.

### Funcionalidades da interface

- login e cadastro de acesso;
- dashboard com visão do aquecimento;
- gestão de usuários;
- gestão de domínios;
- gestão de contas Facebook e Instagram;
- gestão de portfólios de negócio;
- gestão de páginas Facebook;
- gestão de números de portfólio;
- gestão de chaves de recuperação Facebook e Instagram;
- visualização de vínculos entre contas, portfólios e ativos relacionados.

### Observação importante

O front-end foi montado para o contrato atual dos controllers existentes, incluindo rotas legadas como:

- `/auth/login`
- `/auth/register`
- `/manager/users`
- `/domain/**`
- `/facebook/**`
- `/instagram/**`
- `/business-portfolios/**`
- `/facebook-pages/**`
- `/number-portfolios/**`
- `/facebook-recovery-keys/**`
- `/instagram-recovery-keys/**`

## Documentacao da API

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Autenticacao e autorizacao

A API usa token JWT no cabecalho:

```text
Authorization: Bearer <seu_token>
```

### Fluxo basico

1. Fazer login em `POST /auth/login` para receber o token.
2. Enviar o token no cabecalho `Authorization` para endpoints protegidos.

Exemplo de login:

```sh
curl -X POST 'http://localhost:8080/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "gui@mail.com",
    "password": "123"
  }'
```

Se voce chamar `GET /manager/users` sem token valido (ou sem permissao de `ADMIN`), o retorno esperado e **403**.

## Endpoints principais

### Autenticacao

- `POST /auth/login` - publico, retorna JWT.
- `POST /auth/register` - exige perfil `ADMIN`.

### Gerenciamento de usuarios

- `GET /manager/users` - exige perfil `ADMIN`.
- `PUT /manager/users/changeRole` - exige perfil `ADMIN`.
- `PUT /manager/users/deactivate` - endpoint protegido (requer autenticacao).

## Banco de dados e migrations

As migrations estao em `src/main/resources/db/migration`.

Estruturas ja modeladas:
- usuarios;
- contas Facebook e chaves de recuperacao;
- estruturas Instagram;
- dominios e portfolio de negocios.

Exemplo: `V3__create_table_recovery_keys_fb.sql` cria a tabela de chaves de recuperacao para contas Facebook.

## Status do projeto

Projeto em evolucao, com foco atual na base de seguranca, gestao de usuarios e fundacao de dados para aquecimento de Facebook.
