# Warmify - Plano de Controllers e Endpoints para Front-end

## Objetivo
Este documento define os controllers e funcoes/endpoints necessarios para a API do Warmify ficar completa para consumo pelo front-end, com prioridade de implementacao.

## Escopo atual (mapeado no projeto)
Controllers existentes em `src/main/java/com/Guilherme/Warmify/controllers`:
- `AuthenticationController` (`/auth`)
- `UserController` (`/manager`)
- `DomainController` (`/domain`)
- `FacebookController` (`/facebook`)
- `InstagramController` (`/instagram`)

## API alvo recomendada (REST)

### 1) Autenticacao e sessao
Controller: `AuthController`
Base path: `/auth`

Endpoints:
- `POST /auth/login` - autenticar usuario e retornar JWT
- `POST /auth/register` - cadastrar usuario (ideal: apenas admin)
- `POST /auth/refresh` - renovar token (opcional no MVP)
- `POST /auth/logout` - invalidar sessao/token (opcional no MVP)

### 2) Usuario logado
Controller: `MeController`
Base path: `/me`

Endpoints:
- `GET /me` - dados do usuario autenticado
- `PUT /me/profile` - atualizar nome/email
- `PUT /me/password` - trocar senha

### 3) Gestao de usuarios
Controller: `UserManagementController`
Base path: `/users`

Endpoints:
- `GET /users` - listar usuarios (com pagina/filtro)
- `GET /users/{id}` - detalhar usuario
- `PUT /users/{id}` - editar dados do usuario
- `PATCH /users/{id}/role` - alterar role
- `PATCH /users/{id}/status` - ativar/desativar usuario

### 4) Dominios
Controller: `DomainController`
Base path: `/domains`

Endpoints:
- `GET /domains` - listar dominios
- `GET /domains/{id}` - buscar por id
- `GET /domains/by-url?url=` - buscar por URL
- `POST /domains` - criar dominio
- `PUT /domains/{id}` - editar dominio
- `DELETE /domains/{id}` - remover dominio

### 5) Contas Facebook
Controller: `FacebookAccountController`
Base path: `/facebook-accounts`

Endpoints:
- `GET /facebook-accounts`
- `GET /facebook-accounts/{id}`
- `POST /facebook-accounts`
- `PUT /facebook-accounts/{id}`
- `PATCH /facebook-accounts/{id}/status`
- `DELETE /facebook-accounts/{id}`

### 6) Contas Instagram
Controller: `InstagramAccountController`
Base path: `/instagram-accounts`

Endpoints:
- `GET /instagram-accounts`
- `GET /instagram-accounts/{id}`
- `POST /instagram-accounts`
- `PUT /instagram-accounts/{id}`
- `PATCH /instagram-accounts/{id}/status`
- `DELETE /instagram-accounts/{id}`

### 7) Portfolio de negocios
Controller: `BusinessPortfolioController`
Base path: `/business-portfolios`

Endpoints:
- `GET /business-portfolios`
- `GET /business-portfolios/{id}`
- `POST /business-portfolios`
- `PUT /business-portfolios/{id}`
- `PATCH /business-portfolios/{id}/status`
- `DELETE /business-portfolios/{id}`
- `GET /business-portfolios/{id}/summary` - retorno agregado (contas/paginas/numeros)

### 8) Paginas Facebook
Controller: `FacebookPageController`
Base path: `/facebook-pages`

Endpoints:
- `GET /facebook-pages`
- `GET /facebook-pages/{id}`
- `GET /facebook-pages/by-portfolio/{portfolioId}`
- `POST /facebook-pages`
- `PUT /facebook-pages/{id}`
- `DELETE /facebook-pages/{id}`

### 9) Numeros do portfolio
Controller: `NumberPortfolioController`
Base path: `/number-portfolios`

Endpoints:
- `GET /number-portfolios`
- `GET /number-portfolios/{id}`
- `GET /number-portfolios/by-portfolio/{portfolioId}`
- `POST /number-portfolios`
- `PUT /number-portfolios/{id}`
- `PATCH /number-portfolios/{id}/status`
- `DELETE /number-portfolios/{id}`

### 10) Chaves de recuperacao Facebook
Controller: `FacebookRecoveryKeyController`
Base path: `/facebook-recovery-keys`

Endpoints:
- `GET /facebook-recovery-keys/by-account/{facebookAccountId}`
- `POST /facebook-recovery-keys`
- `PUT /facebook-recovery-keys/{id}`
- `DELETE /facebook-recovery-keys/{id}`

### 11) Chaves de recuperacao Instagram
Controller: `InstagramRecoveryKeyController`
Base path: `/instagram-recovery-keys`

Endpoints:
- `GET /instagram-recovery-keys/by-account/{instagramAccountId}`
- `POST /instagram-recovery-keys`
- `PUT /instagram-recovery-keys/{id}`
- `PATCH /instagram-recovery-keys/{id}/status`
- `DELETE /instagram-recovery-keys/{id}`

## Priorizacao sugerida

### MVP (primeiro)
- `AuthController`
- `MeController`
- `UserManagementController` (basico)
- `DomainController` (CRUD completo)
- `FacebookAccountController` (CRUD completo)
- `InstagramAccountController` (CRUD completo)
- Ajustes de seguranca/autorizacao

### Avancado (segunda fase)
- `BusinessPortfolioController`
- `FacebookPageController`
- `NumberPortfolioController`
- `FacebookRecoveryKeyController`
- `InstagramRecoveryKeyController`
- endpoints de resumo/agregacao para dashboard

## Lacunas importantes encontradas no estado atual
- Em `src/main/java/com/Guilherme/Warmify/infra/SecurityConfigurations.java`, existem endpoints sensiveis com `permitAll`, incluindo rotas de gestao.
- Em `src/main/java/com/Guilherme/Warmify/controllers/InstagramController.java`, a validacao do delete esta invertida no uso de `existsById`.
- Em `src/main/java/com/Guilherme/Warmify/controllers/DomainController.java`, a validacao do delete por dominio tambem esta invertida.
- As migrations `V7`, `V8`, `V9`, `V3` e `V5` indicam modulos de dominio ainda sem exposicao completa via controllers/repositories/DTOs.

## Convencoes para o front-end
- Adotar padrao REST com recurso no plural e `{id}` no path.
- Evitar nomes de endpoint orientados a acao (`/register`, `/edit`, `/delete`) quando houver equivalente REST.
- Padronizar retornos de erro (ex.: objeto com `code`, `message`, `details`).
- Padronizar paginacao para listas (`page`, `size`, `sort`).

## Proximo passo recomendado
Implementar primeiro os ajustes de seguranca e a convergencia de rotas para REST, depois fechar os modulos do banco que ja existem em migration (portfolio, paginas, numeros e recovery keys).

## Especificacao funcional por endpoint

Padrao geral de resposta de erro (recomendado):
- `code`: identificador curto do erro (ex.: `USER_NOT_FOUND`)
- `message`: mensagem amigavel
- `details`: lista opcional de campos invalidos

---

### AuthController (`/auth`)

#### `POST /auth/login`
- Faz: autentica por email/senha e retorna JWT.
- Regras: usuario deve existir, senha deve bater, usuario precisa estar ativo.
- Auth: publico.
- Entrada: `email`, `password`.
- Saida: `token`, opcionalmente `expiresAt` e dados basicos do usuario.
- Sucesso: `200`.
- Erros comuns: `400` (payload invalido), `401` (credencial invalida), `403` (usuario inativo).

#### `POST /auth/register`
- Faz: cria novo usuario.
- Regras: email unico; senha com politica minima; role inicial controlada pelo backend.
- Auth: recomendado `ADMIN` (estado atual pode estar mais permissivo).
- Entrada: `name`, `email`, `password`.
- Saida: usuario criado (sem senha) ou `201` sem corpo.
- Sucesso: `201`.
- Erros comuns: `400` (dados invalidos), `409` (email ja existe), `403` (sem permissao).

#### `POST /auth/refresh`
- Faz: emite novo access token com base em refresh token valido.
- Regras: refresh token nao expirado e nao revogado.
- Auth: publico com refresh token.
- Entrada: `refreshToken`.
- Saida: novo `accessToken` (+ `refreshToken` rotacionado, opcional).
- Sucesso: `200`.
- Erros comuns: `401` (token invalido/expirado), `403` (token revogado).

#### `POST /auth/logout`
- Faz: invalida refresh token/sessao.
- Regras: token informado deve pertencer ao usuario autenticado.
- Auth: autenticado.
- Entrada: refresh token (cookie ou body).
- Saida: confirmacao.
- Sucesso: `204`.
- Erros comuns: `401` (nao autenticado), `404` (sessao nao encontrada).

---

### MeController (`/me`)

#### `GET /me`
- Faz: retorna perfil do usuario logado.
- Regras: usar `sub` do JWT para resolver usuario.
- Auth: autenticado.
- Entrada: sem body.
- Saida: `id`, `name`, `email`, `role`, `active`.
- Sucesso: `200`.
- Erros comuns: `401`, `404` (usuario nao encontrado).

#### `PUT /me/profile`
- Faz: atualiza nome/email do proprio usuario.
- Regras: email unico; normalizar email (lowercase/trim).
- Auth: autenticado.
- Entrada: `name`, `email`.
- Saida: perfil atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `409` (email em uso), `401`.

#### `PUT /me/password`
- Faz: troca senha do proprio usuario.
- Regras: validar senha atual; aplicar hash BCrypt; invalidar sessoes antigas (recomendado).
- Auth: autenticado.
- Entrada: `currentPassword`, `newPassword`.
- Saida: confirmacao.
- Sucesso: `204`.
- Erros comuns: `400` (politica de senha), `401` (senha atual invalida).

---

### UserManagementController (`/users`)

#### `GET /users`
- Faz: lista usuarios para tela administrativa.
- Regras: suportar paginacao/filtro (`page`, `size`, `sort`, `q`, `role`, `active`).
- Auth: `ADMIN`.
- Entrada: query params.
- Saida: lista paginada de usuarios (sem senha).
- Sucesso: `200`.
- Erros comuns: `401`, `403`.

#### `GET /users/{id}`
- Faz: detalha usuario por id.
- Regras: UUID valido.
- Auth: `ADMIN`.
- Entrada: `id`.
- Saida: dados do usuario.
- Sucesso: `200`.
- Erros comuns: `400` (id invalido), `404`.

#### `PUT /users/{id}`
- Faz: edita dados cadastrais do usuario.
- Regras: email unico; nao retornar senha.
- Auth: `ADMIN`.
- Entrada: `name`, `email` (e outros campos permitidos).
- Saida: usuario atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `PATCH /users/{id}/role`
- Faz: altera role (`USER`/`ADMIN`).
- Regras: impedir downgrade do ultimo admin (recomendado).
- Auth: `ADMIN`.
- Entrada: `role`.
- Saida: usuario com role atualizada.
- Sucesso: `200`.
- Erros comuns: `400` (role invalida), `404`, `409` (regra de negocio).

#### `PATCH /users/{id}/status`
- Faz: ativa/desativa usuario.
- Regras: impedir desativar o proprio usuario em sessao ativa (recomendado).
- Auth: `ADMIN`.
- Entrada: `active`.
- Saida: status atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

---

### DomainController (`/domains`)

#### `GET /domains`
- Faz: lista dominios.
- Regras: suporte a paginacao e busca por `domUrl`.
- Auth: autenticado.
- Entrada: query params opcionais.
- Saida: lista/pagina de dominios.
- Sucesso: `200`.
- Erros comuns: `401`.

#### `GET /domains/{id}`
- Faz: busca dominio por id.
- Regras: UUID valido.
- Auth: autenticado.
- Entrada: `id`.
- Saida: dominio.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `GET /domains/by-url?url=`
- Faz: busca dominio por URL exata.
- Regras: normalizar URL (trim/lowercase).
- Auth: autenticado.
- Entrada: `url`.
- Saida: dominio.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `POST /domains`
- Faz: cria dominio.
- Regras: `domUrl` unico.
- Auth: `ADMIN`.
- Entrada: `domUrl`.
- Saida: dominio criado.
- Sucesso: `201`.
- Erros comuns: `400`, `409`.

#### `PUT /domains/{id}`
- Faz: altera URL de dominio.
- Regras: nova URL nao pode colidir com existente.
- Auth: `ADMIN`.
- Entrada: `domUrl`.
- Saida: dominio atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `DELETE /domains/{id}`
- Faz: remove dominio.
- Regras: bloquear exclusao se houver portfolio dependente (ou fazer regra de cascata explicita).
- Auth: `ADMIN`.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`, `409` (integridade referencial).

---

### FacebookAccountController (`/facebook-accounts`)

#### `GET /facebook-accounts`
- Faz: lista contas Facebook.
- Regras: filtros por `statusAccount`, `email`, `profileName`.
- Auth: autenticado.
- Entrada: query params opcionais.
- Saida: lista paginada.
- Sucesso: `200`.
- Erros comuns: `401`.

#### `GET /facebook-accounts/{id}`
- Faz: detalha conta.
- Regras: UUID valido.
- Auth: autenticado.
- Entrada: `id`.
- Saida: conta (nunca retornar senha em claro).
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `POST /facebook-accounts`
- Faz: cria conta Facebook.
- Regras: email unico; status inicial padrao (`AVAILABLE`).
- Auth: autenticado (ideal `ADMIN` ou `OPERATOR`).
- Entrada: `profileName`, `email`, `password`, `url`.
- Saida: conta criada.
- Sucesso: `201`.
- Erros comuns: `400`, `409`.

#### `PUT /facebook-accounts/{id}`
- Faz: atualiza dados da conta.
- Regras: validar email unico ao editar.
- Auth: autenticado.
- Entrada: campos editaveis da conta.
- Saida: conta atualizada.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `PATCH /facebook-accounts/{id}/status`
- Faz: altera status operacional da conta.
- Regras: aceitar somente valores de `StatusAccount`.
- Auth: autenticado.
- Entrada: `statusAccount`.
- Saida: conta com status atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `DELETE /facebook-accounts/{id}`
- Faz: remove conta.
- Regras: bloquear exclusao com dependencias (`business_portfolio`, `pgfacebook`) ou aplicar regra documentada.
- Auth: `ADMIN`.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`, `409`.

---

### InstagramAccountController (`/instagram-accounts`)

#### `GET /instagram-accounts`
- Faz: lista contas Instagram.
- Regras: filtros por `statusAccount`, `email`, `username`.
- Auth: autenticado.
- Entrada: query params opcionais.
- Saida: lista paginada.
- Sucesso: `200`.
- Erros comuns: `401`.

#### `GET /instagram-accounts/{id}`
- Faz: detalha conta por id.
- Regras: UUID valido.
- Auth: autenticado.
- Entrada: `id`.
- Saida: conta (sem senha em claro).
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `POST /instagram-accounts`
- Faz: cria conta Instagram.
- Regras: email unico; status inicial padrao.
- Auth: autenticado.
- Entrada: `username`, `email`, `password`, `googleAuthenticatorEmail`.
- Saida: conta criada.
- Sucesso: `201`.
- Erros comuns: `400`, `409`.

#### `PUT /instagram-accounts/{id}`
- Faz: atualiza dados da conta.
- Regras: validar unicidade de email.
- Auth: autenticado.
- Entrada: campos editaveis da conta.
- Saida: conta atualizada.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `PATCH /instagram-accounts/{id}/status`
- Faz: altera status operacional da conta.
- Regras: validar enum de status.
- Auth: autenticado.
- Entrada: `statusAccount`.
- Saida: conta atualizada.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `DELETE /instagram-accounts/{id}`
- Faz: remove conta.
- Regras: respeitar integridade com `business_portfolio` e `recovery_keys_ig`.
- Auth: `ADMIN`.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`, `409`.

---

### BusinessPortfolioController (`/business-portfolios`)

#### `GET /business-portfolios`
- Faz: lista portfolios.
- Regras: filtros por `status`, `bmName`, `cnpj`.
- Auth: autenticado.
- Entrada: query params.
- Saida: lista paginada.
- Sucesso: `200`.
- Erros comuns: `401`.

#### `GET /business-portfolios/{id}`
- Faz: detalha portfolio.
- Regras: UUID valido.
- Auth: autenticado.
- Entrada: `id`.
- Saida: portfolio + referencias (`domId`, `facebookAccountId`, `instagramAccountId`).
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `POST /business-portfolios`
- Faz: cria portfolio de negocio.
- Regras: validar existencia das referencias FK; validar formato de CNPJ.
- Auth: autenticado.
- Entrada: `bmName`, `cnpj`, `cnpjPdf`, `status`, `domId`, `facebookAccountId`, `instagramAccountId`.
- Saida: portfolio criado.
- Sucesso: `201`.
- Erros comuns: `400`, `404` (FK), `409`.

#### `PUT /business-portfolios/{id}`
- Faz: atualiza dados do portfolio.
- Regras: validar FKs e campos obrigatorios.
- Auth: autenticado.
- Entrada: campos editaveis.
- Saida: portfolio atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `PATCH /business-portfolios/{id}/status`
- Faz: altera status do portfolio.
- Regras: aceitar somente enum de status permitido.
- Auth: autenticado.
- Entrada: `status`.
- Saida: portfolio atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `DELETE /business-portfolios/{id}`
- Faz: remove portfolio.
- Regras: verificar dependencias (`pgfacebook`, `number_portfolio`).
- Auth: `ADMIN`.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`, `409`.

#### `GET /business-portfolios/{id}/summary`
- Faz: retorna visao consolidada para dashboard.
- Regras: compor portfolio + paginas + numeros + status de contas vinculadas.
- Auth: autenticado.
- Entrada: `id`.
- Saida: objeto agregado para cards/tabela do front.
- Sucesso: `200`.
- Erros comuns: `404`.

---

### FacebookPageController (`/facebook-pages`)

#### `GET /facebook-pages`
- Faz: lista paginas Facebook.
- Regras: filtrar por portfolio e conta.
- Auth: autenticado.
- Entrada: query params opcionais.
- Saida: lista paginada.
- Sucesso: `200`.
- Erros comuns: `401`.

#### `GET /facebook-pages/{id}`
- Faz: detalha pagina.
- Regras: UUID valido.
- Auth: autenticado.
- Entrada: `id`.
- Saida: pagina + ids relacionados.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `GET /facebook-pages/by-portfolio/{portfolioId}`
- Faz: lista paginas de um portfolio.
- Regras: portfolio deve existir.
- Auth: autenticado.
- Entrada: `portfolioId`.
- Saida: lista de paginas.
- Sucesso: `200`.
- Erros comuns: `404`.

#### `POST /facebook-pages`
- Faz: cria pagina vinculada a conta e portfolio.
- Regras: validar FKs; evitar duplicidade de `pageName` no mesmo portfolio (recomendado).
- Auth: autenticado.
- Entrada: `pageName`, `facebookAccountId`, `businessPortfolioId`.
- Saida: pagina criada.
- Sucesso: `201`.
- Erros comuns: `400`, `404`, `409`.

#### `PUT /facebook-pages/{id}`
- Faz: atualiza dados da pagina.
- Regras: validar FKs quando alteradas.
- Auth: autenticado.
- Entrada: campos editaveis.
- Saida: pagina atualizada.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `DELETE /facebook-pages/{id}`
- Faz: remove pagina.
- Regras: exclusao fisica ou logica conforme politica do projeto.
- Auth: autenticado.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`.

---

### NumberPortfolioController (`/number-portfolios`)

#### `GET /number-portfolios`
- Faz: lista numeros.
- Regras: filtro por `status`, `name`, `businessPortfolioId`.
- Auth: autenticado.
- Entrada: query params.
- Saida: lista paginada.
- Sucesso: `200`.
- Erros comuns: `401`.

#### `GET /number-portfolios/{id}`
- Faz: detalha numero.
- Regras: UUID valido.
- Auth: autenticado.
- Entrada: `id`.
- Saida: numero do portfolio.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `GET /number-portfolios/by-portfolio/{portfolioId}`
- Faz: lista numeros de um portfolio.
- Regras: portfolio deve existir.
- Auth: autenticado.
- Entrada: `portfolioId`.
- Saida: lista de numeros.
- Sucesso: `200`.
- Erros comuns: `404`.

#### `POST /number-portfolios`
- Faz: cria numero vinculado ao portfolio.
- Regras: validar formato (E.164 recomendado), evitar duplicidade por portfolio.
- Auth: autenticado.
- Entrada: `name`, `number`, `status`, `businessPortfolioId`.
- Saida: numero criado.
- Sucesso: `201`.
- Erros comuns: `400`, `404`, `409`.

#### `PUT /number-portfolios/{id}`
- Faz: atualiza dados do numero.
- Regras: validar formato de numero e FK.
- Auth: autenticado.
- Entrada: campos editaveis.
- Saida: numero atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `PATCH /number-portfolios/{id}/status`
- Faz: altera status do numero.
- Regras: validar enum de status.
- Auth: autenticado.
- Entrada: `status`.
- Saida: numero atualizado.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `DELETE /number-portfolios/{id}`
- Faz: remove numero do portfolio.
- Regras: respeitar regras de auditoria (se houver).
- Auth: autenticado.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`.

---

### FacebookRecoveryKeyController (`/facebook-recovery-keys`)

#### `GET /facebook-recovery-keys/by-account/{facebookAccountId}`
- Faz: lista chaves de recuperacao da conta Facebook.
- Regras: conta deve existir.
- Auth: autenticado.
- Entrada: `facebookAccountId`.
- Saida: lista de chaves (mascarar valor quando necessario).
- Sucesso: `200`.
- Erros comuns: `404`.

#### `POST /facebook-recovery-keys`
- Faz: cadastra nova chave de recuperacao.
- Regras: associar a conta; evitar duplicidade de chave ativa.
- Auth: autenticado.
- Entrada: `recoveryKey`, `facebookAccountId`.
- Saida: chave criada.
- Sucesso: `201`.
- Erros comuns: `400`, `404`, `409`.

#### `PUT /facebook-recovery-keys/{id}`
- Faz: atualiza chave de recuperacao.
- Regras: registrar historico (recomendado).
- Auth: autenticado.
- Entrada: campos editaveis.
- Saida: chave atualizada.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `DELETE /facebook-recovery-keys/{id}`
- Faz: remove chave.
- Regras: permitir somente quando nao estiver em uso.
- Auth: autenticado.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`, `409`.

---

### InstagramRecoveryKeyController (`/instagram-recovery-keys`)

#### `GET /instagram-recovery-keys/by-account/{instagramAccountId}`
- Faz: lista chaves da conta Instagram.
- Regras: conta deve existir.
- Auth: autenticado.
- Entrada: `instagramAccountId`.
- Saida: lista de chaves e status.
- Sucesso: `200`.
- Erros comuns: `404`.

#### `POST /instagram-recovery-keys`
- Faz: cria chave de recuperacao Instagram.
- Regras: associar conta e status inicial valido.
- Auth: autenticado.
- Entrada: `recoveryKey`, `status`, `instagramAccountId`.
- Saida: chave criada.
- Sucesso: `201`.
- Erros comuns: `400`, `404`, `409`.

#### `PUT /instagram-recovery-keys/{id}`
- Faz: edita dados da chave.
- Regras: manter rastreabilidade de alteracoes (recomendado).
- Auth: autenticado.
- Entrada: campos editaveis.
- Saida: chave atualizada.
- Sucesso: `200`.
- Erros comuns: `400`, `404`.

#### `PATCH /instagram-recovery-keys/{id}/status`
- Faz: altera status da chave (ex.: `AVAILABLE`, `USED`, `BLOCKED`).
- Regras: validar transicao de status.
- Auth: autenticado.
- Entrada: `status`.
- Saida: chave atualizada.
- Sucesso: `200`.
- Erros comuns: `400`, `404`, `409`.

#### `DELETE /instagram-recovery-keys/{id}`
- Faz: remove chave.
- Regras: bloquear exclusao de chave marcada como em uso, se aplicavel.
- Auth: autenticado.
- Entrada: `id`.
- Saida: sem corpo.
- Sucesso: `204`.
- Erros comuns: `404`, `409`.
