import jwt from 'jsonwebtoken';

// =========================================
// MIDDLEWARE DE AUTENTICACAO
// =========================================
// O que e um middleware?
// E uma funcao que roda ANTES da rota chegar no controller.
// Exemplo: GET /pedidos → passa pelo middleware → chega no controller
//
// Este middleware verifica se o token JWT e valido.
// Se for, deixa passar. Se nao for, bloqueia com 401.
//
// Como o app envia o token:
// Header da requisicao: Authorization: Bearer <token>
// =========================================

export const autenticar = (req, res, next) => {
  // Pega o header Authorization
  const authHeader = req.headers['authorization'];

  // Verifica se o header existe e tem o formato "Bearer <token>"
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ erro: 'Token nao fornecido.' });
  }

  // Extrai apenas o token (remove o "Bearer ")
  const token = authHeader.split(' ')[1];

  try {
    // Verifica e decodifica o token usando a chave secreta
    const decoded = jwt.verify(token, process.env.JWT_SECRET);

    // Adiciona os dados do usuario na requisicao
    // Assim qualquer controller que vier depois pode usar req.usuario
    req.usuario = decoded;

    // Chama o proximo passo (o controller)
    next();

  } catch (error) {
    return res.status(401).json({ erro: 'Token invalido ou expirado.' });
  }
};