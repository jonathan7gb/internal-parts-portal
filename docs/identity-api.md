# Identity Module — API Reference

**Base URL:** `http://localhost:8080`  
**Issues:** ISSUE-008 · ISSUE-009 · ISSUE-010 · ISSUE-011  
**RFs:** RF01, RF02, RF03, RF04, RF05

---

## Autenticação

Todos os endpoints (exceto `POST /auth/login`) exigem o header:

```
Authorization: Bearer <token>
```

O token JWT contém no payload: `sub` (userId), `role`, `exp`.

---

## Endpoints

### 1. Login

**`POST /auth/login`**  
Público — sem autenticação prévia.

**Request body**
```json
{
  "email": "pablo@centroweg.com",
  "password": "senha123"
}
```

| Campo | Tipo | Obrigatório | Validação |
|---|---|---|---|
| `email` | string | sim | formato e-mail válido |
| `password` | string | sim | não vazio |

**Response `200 OK`**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresAt": "2026-05-25T14:00:00Z"
}
```

**Respostas de erro**

| Status | Quando |
|---|---|
| `400` | Body inválido (campo ausente ou mal formatado) |
| `401` | Credenciais incorretas ou usuário inativo (`active = false`) |

---

### 2. Listar usuários

**`GET /users`**  
Roles permitidas: `ADMIN`

**Response `200 OK`**
```json
[
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "name": "Pablo Tzeliks",
    "email": "pablo@centroweg.com",
    "role": "EMPLOYEE",
    "active": true,
    "createdAt": "2026-05-25T10:00:00Z"
  }
]
```

**Respostas de erro**

| Status | Quando |
|---|---|
| `401` | Token ausente ou expirado |
| `403` | Role diferente de `ADMIN` |

---

### 3. Criar usuário

**`POST /users`**  
Roles permitidas: `ADMIN`

**Request body**
```json
{
  "name": "João Silva",
  "email": "joao@centroweg.com",
  "password": "senhaSegura@123",
  "role": "EMPLOYEE"
}
```

| Campo | Tipo | Obrigatório | Validação |
|---|---|---|---|
| `name` | string | sim | 2–100 caracteres |
| `email` | string | sim | formato e-mail válido, único no sistema |
| `password` | string | sim | mínimo 8 caracteres |
| `role` | string (enum) | sim | `EMPLOYEE`, `APPROVER`, `STOREKEEPER`, `ADMIN` |

**Response `201 Created`**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "João Silva",
  "email": "joao@centroweg.com",
  "role": "EMPLOYEE",
  "active": true,
  "createdAt": "2026-05-25T10:00:00Z"
}
```

**Respostas de erro**

| Status | Quando |
|---|---|
| `400` | Body inválido |
| `401` | Token ausente ou expirado |
| `403` | Role diferente de `ADMIN` |
| `409` | Email já cadastrado |

---

### 4. Editar usuário

**`PUT /users/{id}`**  
Roles permitidas: `ADMIN`

**Path parameter**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `id` | UUID | ID do usuário a editar |

**Request body**
```json
{
  "name": "João Silva Editado",
  "email": "joao.novo@centroweg.com",
  "role": "APPROVER"
}
```

| Campo | Tipo | Obrigatório | Observação |
|---|---|---|---|
| `name` | string | sim | 2–100 caracteres |
| `email` | string | sim | deve ser único |
| `role` | string (enum) | sim | altera o papel do usuário |

> Troca de senha não passa por este endpoint — use `PUT /users/me` para o próprio usuário.

**Response `200 OK`** — mesmo schema de `UserResponse`

**Respostas de erro**

| Status | Quando |
|---|---|
| `400` | Body inválido |
| `401` | Token ausente ou expirado |
| `403` | Role diferente de `ADMIN` |
| `404` | Usuário não encontrado |
| `409` | Email já em uso por outro usuário |

---

### 5. Desativar usuário

**`PATCH /users/{id}/deactivate`**  
Roles permitidas: `ADMIN`

**Path parameter**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `id` | UUID | ID do usuário a desativar |

Seta `active = false`. Usuário desativado não consegue mais autenticar. A operação é idempotente — desativar um usuário já inativo retorna `204`.

**Response `204 No Content`**

**Respostas de erro**

| Status | Quando |
|---|---|
| `400` | Tentativa de auto-desativação (ADMIN tentando desativar a si mesmo) |
| `401` | Token ausente ou expirado |
| `403` | Role diferente de `ADMIN` |
| `404` | Usuário não encontrado |

---

### 6. Perfil próprio — consulta

**`GET /users/me`**  
Roles permitidas: qualquer usuário autenticado

Retorna os dados do usuário dono do token JWT.

**Response `200 OK`**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "Pablo Tzeliks",
  "email": "pablo@centroweg.com",
  "role": "EMPLOYEE",
  "active": true,
  "createdAt": "2026-05-25T10:00:00Z"
}
```

**Respostas de erro**

| Status | Quando |
|---|---|
| `401` | Token ausente ou expirado |

---

### 7. Perfil próprio — edição

**`PUT /users/me`**  
Roles permitidas: qualquer usuário autenticado

Permite editar nome e senha. Role e email não são alteráveis por este endpoint — role só o ADMIN altera via `PUT /users/{id}`.

**Request body**
```json
{
  "name": "Pablo T. Atualizado",
  "currentPassword": "senhaAtual123",
  "newPassword": "novaSenha@456"
}
```

| Campo | Tipo | Obrigatório | Observação |
|---|---|---|---|
| `name` | string | sim | 2–100 caracteres |
| `currentPassword` | string | não | obrigatório somente se `newPassword` for enviado |
| `newPassword` | string | não | mínimo 8 caracteres; exige `currentPassword` |

**Response `200 OK`** — mesmo schema de `UserResponse`

**Respostas de erro**

| Status | Quando |
|---|---|
| `400` | Body inválido; `newPassword` enviado sem `currentPassword`; `currentPassword` incorreta |
| `401` | Token ausente ou expirado |

---

## Schemas de referência

### `UserResponse`

```json
{
  "id":        "UUID",
  "name":      "string",
  "email":     "string",
  "role":      "EMPLOYEE | APPROVER | STOREKEEPER | ADMIN",
  "active":    "boolean",
  "createdAt": "ISO-8601 datetime"
}
```

> `password` **nunca** é retornado em nenhum endpoint.

### Roles (enum `Role`)

| Valor | Papel |
|---|---|
| `EMPLOYEE` | Colaborador — cria e acompanha pedidos |
| `APPROVER` | Aprovador — avalia pedidos pendentes |
| `STOREKEEPER` | Almoxarife — separa peças e gerencia estoque |
| `ADMIN` | Administrador — gerencia usuários e catálogo |

---

## Regras de segurança

- Senha armazenada exclusivamente como hash **bcrypt** — nunca em plain text.
- Token JWT com expiração configurável via `jwt.expiration` no `application.yml`.
- Roles aplicadas via `@PreAuthorize` nos controllers — nunca dentro de services.
- Filtro JWT (`JwtAuthFilter`) valida o token e popula o `SecurityContext` antes de qualquer controller.
- Usuário com `active = false` recebe `401` no login.
