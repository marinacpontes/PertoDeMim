<h1 align="center">
  <br>
  Perto de Mim
  <br>
</h1>

<p align="center">
  Aplicativo para encrontrar prestadores de serviço perto de você.
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img alt="Java" src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img alt="Node.js" src="https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white"/>
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white"/>
</p>

---

## Sobre o app

O **Perto de Mim** é um marketplace mobile que conecta clientes a prestadores de serviços locais. Em vez de pesquisar em vários lugares, o usuário abre o app, vê no mapa quem está por perto, confere avaliações, contrata e paga, sem sair da plataforma.

Do outro lado, prestadores têm um espaço próprio para divulgar seus serviços, gerenciar pedidos e se comunicar com clientes.

---

## Funcionalidades

### Para clientes

| | |
|---|---|
| **Mapa interativo** | Visualize prestadores e serviços próximos em tempo real |
| **Busca avançada** | Filtre por preço, distância, categoria e avaliação |
| **Chat integrado** | Converse com o prestador antes e durante o serviço |
| **Pagamento no app** | Pix, cartão de crédito ou débito |
| **Avaliações** | Avalie o serviço após a conclusão |
| **Favoritos** | Salve seus prestadores preferidos |

### Para prestadores

| | |
|---|---|
| **Perfil de negócio** | Apresente sua loja com descrição, categoria, preço médio e fotos |
| **Gestão de serviços** | Cadastre e gerencie os serviços oferecidos |
| **Gestão de pedidos** | Aceite, recuse ou conclua pedidos de clientes |
| **Chat com clientes** | Comunicação direta pelo app |
| 🌟 **Reputação** | Acompanhe avaliações e sua média de nota |

---

## 🗂️ Categorias

<p>
  <img src="https://img.shields.io/badge/💇 Beleza e Estética-f472b6?style=flat-square"/>
  <img src="https://img.shields.io/badge/🏥 Saúde-34d399?style=flat-square"/>
  <img src="https://img.shields.io/badge/🍽️ Alimentação-fb923c?style=flat-square"/>
  <img src="https://img.shields.io/badge/🔧 Manutenção-60a5fa?style=flat-square"/>
  <img src="https://img.shields.io/badge/💻 Tecnologia-a78bfa?style=flat-square"/>
  <img src="https://img.shields.io/badge/📚 Educação-facc15?style=flat-square"/>
  <img src="https://img.shields.io/badge/✳️ Outros-94a3b8?style=flat-square"/>
</p>

---

## Telas

<table align="center">
  <tr>
    <td align="center"><sub>Autenticação</sub><br/><img src="assets/autenticacao.jpeg" width="200"/></td>
    <td align="center"><sub>Mapa</sub><br/><img src="assets/mapa.jpeg" width="200"/></td>
    <td align="center"><sub>Vitrine</sub><br/><img src="assets/vitrine.jpeg" width="200"/></td>
  </tr>
  <tr>
    <td align="center"><sub>Perfil do Fornecedor</sub><br/><img src="assets/perfilfornecedor.jpeg" width="200"/></td>
    <td align="center"><sub>Tela de Pagamento</sub><br/><img src="assets/pagamento.jpeg" width="200"/></td>
    <td align="center"><sub>Perfil do Cliente</sub><br/><img src="assets/perfilcliente.jpeg" width="200"/></td>
  </tr>
</table>

---

## Tecnologias

### Frontend

| Tecnologia | Descrição |
|---|---|
| Java 11 | Linguagem principal do app Android |
| Android SDK 26–36 | Suporte ao Android 8.0 e versões mais recentes |
| Material Design 3 | Componentes visuais modernos (cards, sliders, inputs) |
| Google Maps SDK 19 | Mapa interativo com marcadores de prestadores |
| Retrofit 2 + Gson | Requisições HTTP e serialização JSON |
| SharedPreferences | Sessão do usuário e token JWT local |
| Gradle 9.4.1 | Build system com Version Catalogs |

### Backend

| Tecnologia | Descrição |
|---|---|
| Node.js (ES Modules) | Runtime do servidor |
| Express 5 | Framework web para a API REST |
| PostgreSQL | Banco de dados relacional |
| JWT | Autenticação com token (expira em 7 dias) |
| bcrypt | Hash seguro de senhas |
| multer | Upload de imagens (portfólio e chat) |
| Resend | Envio de e-mails transacionais |
| Railway | Hospedagem e deploy do backend |
