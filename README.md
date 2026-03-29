# Warmify

API backend para **aquecimento de perfis e paginas do Facebook**, com controle de usuarios, autenticacao JWT e base pronta para evolucao de automacoes sociais.

## Objetivo do projeto

O Warmify nasce para organizar e automatizar o processo de aquecimento de perfis e paginas do Facebook de forma segura e escalavel.

Na pratica, a aplicacao busca:
- centralizar contas e dados de aquecimento;
- controlar acesso de operadores e administradores;
- registrar e preparar estruturas para rotinas de aquecimento;
- permitir evolucao futura para multiplos dominios sociais.

## Stack utilizada

- Java 21
- Spring Boot (Web, Security, Data JPA)
- PostgreSQL
- Flyway (migrations)
- JWT (`java-jwt`)
- Swagger/OpenAPI (`springdoc-openapi`)

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
