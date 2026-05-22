# Perto de Mim — API Backend

API REST do aplicativo Perto de Mim, desenvolvida em Node.js com Express e PostgreSQL.

---

## Ambientes

| Ambiente | URL |
|---|---|
| Local | `http://localhost:3000` |
| Producao | `https://backend-production-b962.up.railway.app` |

---

## Tecnologias

- Node.js com ES Modules
- Express
- PostgreSQL
- bcrypt (senhas)
- JWT (autenticacao)
- dotenv (variaveis de ambiente)
- multer (upload de imagens)
- nodemailer (envio de email)
- Railway (deploy e hospedagem)

---

## Como rodar o projeto localmente

### Pre-requisitos
- Node.js instalado
- PostgreSQL instalado e rodando

### Configuracao

1. Clone o repositorio:
```bash
git clone https://github.com/marinacpontes/pertodemim.git
cd pertodemim/backend
```

2. Instale as dependencias:
```bash
npm install
```

3. Crie o arquivo `.env` na pasta `backend/`:
```
DB_USER=seu_usuario_postgres
DB_HOST=localhost
DB_NAME=perto_de_mim
DB_PASSWORD=sua_senha
DB_PORT=5432
JWT_SECRET=uma_chave_secreta_longa
EMAIL_USER=seu_email@gmail.com
EMAIL_PASS=sua_app_password_gmail
```

> **Mac:** o usuario geralmente e o nome do sistema (ex: `juliaduran`). **Windows:** geralmente e `postgres`.
> **EMAIL_PASS:** usar App Password do Gmail (nao a senha normal). Gerar em: Minha Conta Google → Seguranca → Senhas de app.

4. Crie o banco de dados e as tabelas:
```bash
node init_db.js
```

5. Rode o servidor:
```bash
node src/app.js
```

Servidor em `http://localhost:3000`

---

## Estrutura do projeto

```
pertodemim/
├── backend/
│   ├── src/
│   │   ├── config/
│   │   │   └── db.js
│   │   ├── controllers/
│   │   │   ├── usuariosController.js
│   │   │   ├── fornecedoresController.js
│   │   │   ├── servicosController.js
│   │   │   ├── pedidosController.js
│   │   │   ├── avaliacoesController.js
│   │   │   ├── favoritosController.js
│   │   │   ├── portfolioController.js
│   │   │   ├── pagamentosController.js
│   │   │   ├── mensagensController.js
│   │   │   ├── recuperacaoSenhaController.js
│   │   │   └── authController.js
│   │   ├── middlewares/
│   │   │   └── authMiddleware.js
│   │   ├── routes/
│   │   │   ├── usuarios.js
│   │   │   ├── fornecedores.js
│   │   │   ├── servicos.js
│   │   │   ├── pedidos.js
│   │   │   ├── avaliacoes.js
│   │   │   ├── favoritos.js
│   │   │   ├── portfolio.js
│   │   │   ├── pagamentos.js
│   │   │   ├── mensagens.js
│   │   │   ├── recuperacao.js
│   │   │   └── auth.js
│   │   └── app.js
│   ├── uploads/              # Imagens de portfolio e chat
│   ├── .env                  # NAO sobe no Git
│   ├── .env.example
│   ├── .gitignore
│   ├── init_db.js
│   └── package.json
└── frontend/                 # App Android (Marina)
```

---

## Autenticacao

Rotas protegidas exigem token JWT no header:

```
Authorization: Bearer <token>
```

Token obtido no login, expira em 7 dias.

---

## Rotas

### 1. Usuarios

#### POST `/usuarios` — Criar usuario (cliente ou fornecedor basico)

> Para cadastrar fornecedor completo (com dados da loja), usar `POST /fornecedores`.

**Body:**
```json
{
  "nome": "Julia",
  "email": "julia@email.com",
  "senha": "123456",
  "tipo": "cliente",
  "telefone": "85999999999",
  "cpf_cnpj": "52998224725",
  "data_nascimento": "1999-05-15",
  "logradouro": "Rua das Flores",
  "cep": "60000000",
  "numero": "123",
  "bairro": "Meireles",
  "complemento": "Apto 42",
  "cidade": "Fortaleza",
  "estado": "CE"
}
```

| Campo | Regra |
|---|---|
| nome | Obrigatorio, max 40 caracteres |
| email | Obrigatorio, formato valido, unico |
| senha | Obrigatorio, min 6 caracteres |
| tipo | `"cliente"` ou `"fornecedor"` |
| telefone | DDD valido + numero, so digitos |
| cpf_cnpj | CPF (11) ou CNPJ (14) digitos, unico |
| data_nascimento | `YYYY-MM-DD`. Cliente: min 16 anos. Fornecedor: min 18 anos |
| logradouro | Obrigatorio, max 50 caracteres |
| cep | 8 digitos |
| complemento | Opcional |

---

#### GET `/usuarios` — Listar todos
#### GET `/usuarios/:id` — Buscar por ID

---

### 2. Fornecedores

#### POST `/fornecedores` — Cadastrar fornecedor completo

Cria registro nas tabelas `usuarios` e `fornecedores` em transacao atomica.

**Body:**
```json
{
  "nome": "Marina Silva",
  "email": "marina@email.com",
  "senha": "123456",
  "telefone": "85988887777",
  "cpf_cnpj": "11144477735",
  "data_nascimento": "1995-03-20",
  "logradouro": "Av. Beira Mar",
  "cep": "60165121",
  "numero": "100",
  "bairro": "Meireles",
  "cidade": "Fortaleza",
  "estado": "CE",
  "nome_loja": "Studio Marina",
  "nome_responsavel": "Marina Silva",
  "categoria": "Beleza e Estetica",
  "descricao": "Especialista em cabelo e maquiagem",
  "preco_medio": 80
}
```

**Categorias validas:** `Beleza e Estetica` | `Saude` | `Alimentacao` | `Manutencao` | `Tecnologia` | `Outros`

> Quando `"Outros"`, adicionar `"categoria_outro": "descricao"`.

---

#### GET `/fornecedores` — Listar todos
#### GET `/fornecedores/:id` — Buscar por ID

---

### 3. Servicos

#### POST `/servicos` — Cadastrar servico (protegida — fornecedor)

**Body:**
```json
{
  "nome": "Corte de cabelo",
  "descricao": "Corte feminino com lavagem e escova",
  "preco": 80,
  "categoria_id": 1
}
```

**Categorias:** 1-Beleza e Estetica | 2-Saude | 3-Alimentacao | 4-Manutencao | 5-Tecnologia | 6-Outros

---

#### GET `/servicos` — Listar todos (publico)
#### GET `/servicos/:id` — Buscar por ID (publico)
#### GET `/servicos/fornecedor/:id` — Servicos de um fornecedor (publico)

---

### 4. Pedidos

#### POST `/pedidos` — Criar pedido (protegida — cliente)

**Body:** `{ "servico_id": 1 }`

**Status por tipo:**
| Tipo | Permitidos |
|---|---|
| fornecedor | `aceito`, `recusado`, `concluido` |
| cliente | `cancelado` (ate 2h apos aceito) |

> **Politica de cancelamento:** cliente so pode cancelar ate 2 horas apos o fornecedor aceitar.

---

#### GET `/pedidos/meus` — Meus pedidos (protegida)
#### GET `/pedidos/:id` — Buscar por ID (protegida)
#### PATCH `/pedidos/:id/status` — Atualizar status (protegida)

**Body:** `{ "status": "aceito" }`

---

### 5. Pagamentos

#### POST `/pagamentos/:pedido_id` — Registrar pagamento (protegida — cliente)

So para pedidos com status `aceito`.

**Formas aceitas:** `pix`, `cartao_credito`, `cartao_debito`

**Body:** `{ "forma_pagamento": "pix" }`

---

#### GET `/pagamentos/:pedido_id` — Consultar pagamento (protegida)

---

#### PATCH `/pagamentos/:pedido_id/status` — Atualizar status (protegida)

| Tipo | Permitido |
|---|---|
| fornecedor | `pago` |
| cliente | `cancelado` |

**Body:** `{ "status": "pago" }`

---

### 6. Avaliacoes

#### POST `/avaliacoes` — Avaliar (protegida — cliente)

So apos pedido `concluido`. 1 avaliacao por pedido. Nota 1-5.

**Body:**
```json
{
  "pedido_id": 1,
  "nota": 5,
  "comentario": "Excelente servico!"
}
```

---

#### GET `/avaliacoes/servico/:id` — Por servico (publico)
#### GET `/avaliacoes/fornecedor/:id` — Por fornecedor com media (publico)

---

### 7. Favoritos

#### POST `/favoritos` — Adicionar (protegida — cliente)

**Body:** `{ "servico_id": 1 }`

---

#### DELETE `/favoritos/:servico_id` — Remover (protegida — cliente)
#### GET `/favoritos` — Listar favoritos (protegida — cliente)

---

### 8. Portfolio

#### POST `/portfolio` — Adicionar imagem (protegida — fornecedor)
**Content-Type: multipart/form-data**

| Campo | Tipo | Detalhe |
|---|---|---|
| imagem | File | JPG, PNG ou WEBP, max 5MB |
| servico_id | Text | ID do servico |

> Imagem acessivel em `https://backend-production-b962.up.railway.app/uploads/nome-arquivo.jpeg`

---

#### DELETE `/portfolio/:id` — Remover imagem (protegida — fornecedor)
#### GET `/portfolio/servico/:id` — Listar por servico (publico)

---

### 9. Mensagens

#### POST `/mensagens` — Enviar mensagem (protegida)

**Texto (raw JSON):**
```json
{
  "destinatario_id": 1,
  "mensagem": "Ola! Gostaria de saber mais sobre o servico."
}
```

**Imagem (form-data):**
| Campo | Tipo | Detalhe |
|---|---|---|
| imagem | File | JPG, PNG ou WEBP, max 200MB |
| destinatario_id | Text | ID do destinatario |

---

#### GET `/mensagens/conversas` — Listar todas as conversas (protegida)
#### GET `/mensagens/:outro_usuario_id` — Listar conversa com um usuario (protegida)

---

### 10. Recuperacao de senha

Fluxo em 3 passos: solicitar codigo → validar codigo → redefinir senha.

> **Observacao:** em ambiente de producao (Railway), o envio de email pode estar bloqueado por restricoes de SMTP. Funciona normalmente em ambiente local.

#### POST `/recuperacao/solicitar` — Solicitar codigo por email

**Body:** `{ "email": "julia@email.com" }`

**Resposta:**
```json
{
  "mensagem": "Se este e-mail estiver cadastrado, voce recebera um codigo em breve."
}
```

> O codigo de 6 digitos e enviado por email e expira em **15 minutos**.

---

#### POST `/recuperacao/validar` — Validar codigo

**Body:**
```json
{
  "email": "julia@email.com",
  "codigo": "304171"
}
```

---

#### POST `/recuperacao/redefinir` — Redefinir senha

**Body:**
```json
{
  "email": "julia@email.com",
  "codigo": "304171",
  "nova_senha": "novaSenha123"
}
```

**Resposta:**
```json
{
  "mensagem": "Senha redefinida com sucesso!"
}
```

---

### 11. Autenticacao

#### POST `/auth/login` — Login

**Body:** `{ "email": "julia@email.com", "senha": "123456" }`

**Resposta:**
```json
{
  "mensagem": "Login realizado com sucesso!",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "nome": "Julia",
    "email": "julia@email.com",
    "tipo": "cliente"
  }
}
```

---

## Observacoes para o front

1. **Mascaras:** remover antes de enviar para a API.
2. **Data de nascimento:** formato `YYYY-MM-DD`.
3. **Tipo:** usar `tipo` do login para redirecionar cliente/fornecedor.
4. **Token expirado:** `401` em rota protegida → redirecionar para login.
5. **Erros:** retornados todos de uma vez no array `erros`.
6. **Idade minima:** cliente 16 anos, fornecedor 18 anos.
7. **Categoria Outros:** exibir campo de texto, enviar `categoria_outro`.
8. **Pedidos:** valor copiado automaticamente do servico.
9. **Cancelamento:** so ate 2h apos aceito pelo fornecedor.
10. **Pagamentos:** formas aceitas: `pix`, `cartao_credito`, `cartao_debito`.
11. **Avaliacoes:** so apos `concluido`. Mapear estrelas (1-5) para numeros.
12. **Portfolio:** enviar como `multipart/form-data`. Max 5MB.
13. **Mensagens texto:** enviar como `raw JSON`. Mensagens imagem: `form-data`. Max 200MB.
14. **Recuperacao de senha:** fluxo em 3 etapas. Codigo expira em 15 minutos.
15. **Datas:** retornadas em UTC. Converter para `America/Sao_Paulo` ao exibir.
16. **URL da API:** usar `https://backend-production-b962.up.railway.app` no app Android. Para testes locais, usar `http://10.0.2.2:3000` no emulador.
17. **Cadastro de fornecedor:** usar `POST /fornecedores` (nao `/usuarios`) para criar perfil completo com dados da loja.