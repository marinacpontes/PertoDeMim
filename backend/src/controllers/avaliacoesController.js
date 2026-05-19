import pool from '../config/db.js';

// =========================================
// CRIAR AVALIACAO
// Apenas cliente logado, pedido concluido
// =========================================
export const criarAvaliacao = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'cliente') {
      return res.status(403).json({ erro: 'Apenas clientes podem avaliar servicos.' });
    }

    const { pedido_id, nota, comentario } = req.body;

    const erros = [];

    if (!pedido_id) {
      erros.push('ID do pedido e obrigatorio.');
    }

    if (!nota) {
      erros.push('Nota e obrigatoria.');
    } else if (!Number.isInteger(Number(nota)) || Number(nota) < 1 || Number(nota) > 5) {
      erros.push('Nota deve ser um numero inteiro entre 1 e 5.');
    }

    if (erros.length > 0) {
      return res.status(400).json({ erros });
    }

    // Busca o pedido e verifica se pertence ao cliente
    const pedidoResult = await pool.query(
      `SELECT p.id, p.status, p.servico_id, c.usuario_id AS cliente_usuario_id
       FROM pedidos p
       INNER JOIN clientes c ON c.id = p.cliente_id
       WHERE p.id = $1`,
      [pedido_id]
    );

    if (pedidoResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Pedido nao encontrado.' });
    }

    const pedido = pedidoResult.rows[0];

    // Verifica se o pedido pertence ao cliente logado
    if (pedido.cliente_usuario_id !== usuario_id) {
      return res.status(403).json({ erro: 'Voce nao tem permissao para avaliar este pedido.' });
    }

    // Verifica se o pedido esta concluido
    if (pedido.status !== 'concluido') {
      return res.status(400).json({ erro: 'So e possivel avaliar pedidos com status "concluido".' });
    }

    // Verifica se ja existe avaliacao para este pedido
    const avaliacaoExistente = await pool.query(
      'SELECT id FROM avaliacoes WHERE pedido_id = $1',
      [pedido_id]
    );

    if (avaliacaoExistente.rows.length > 0) {
      return res.status(409).json({ erro: 'Este pedido ja foi avaliado.' });
    }

    // Busca o cliente_id
    const clienteResult = await pool.query(
      'SELECT id FROM clientes WHERE usuario_id = $1',
      [usuario_id]
    );
    const cliente_id = clienteResult.rows[0].id;

    // Cria a avaliacao
    const result = await pool.query(
      `INSERT INTO avaliacoes (cliente_id, servico_id, pedido_id, nota, comentario)
       VALUES ($1, $2, $3, $4, $5)
       RETURNING *`,
      [
        cliente_id,
        pedido.servico_id,
        pedido_id,
        Number(nota),
        comentario ? comentario.trim() : null,
      ]
    );

    return res.status(201).json({
      mensagem: 'Avaliacao registrada com sucesso!',
      avaliacao: result.rows[0],
    });

  } catch (error) {
    console.error('Erro ao criar avaliacao:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR AVALIACOES DE UM SERVICO
// Rota publica
// =========================================
export const listarAvaliacoesPorServico = async (req, res) => {
  try {
    const { id } = req.params;

    const result = await pool.query(
      `SELECT
        a.id, a.nota, a.comentario, a.created_at,
        u.nome AS nome_cliente,
        s.nome AS servico
       FROM avaliacoes a
       INNER JOIN clientes c ON c.id = a.cliente_id
       INNER JOIN usuarios u ON u.id = c.usuario_id
       INNER JOIN servicos s ON s.id = a.servico_id
       WHERE a.servico_id = $1
       ORDER BY a.created_at DESC`,
      [id]
    );

    // Calcula a media das notas
    const media = result.rows.length > 0
      ? (result.rows.reduce((acc, r) => acc + Number(r.nota), 0) / result.rows.length).toFixed(1)
      : null;

    return res.json({
      servico_id: Number(id),
      total_avaliacoes: result.rows.length,
      media_nota: media ? Number(media) : null,
      avaliacoes: result.rows,
    });

  } catch (error) {
    console.error('Erro ao listar avaliacoes:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR AVALIACOES DE UM FORNECEDOR
// Rota publica — retorna media geral
// =========================================
export const listarAvaliacoesPorFornecedor = async (req, res) => {
  try {
    const { id } = req.params; // usuario_id do fornecedor

    const result = await pool.query(
      `SELECT
        a.id, a.nota, a.comentario, a.created_at,
        u.nome AS nome_cliente,
        s.nome AS servico
       FROM avaliacoes a
       INNER JOIN clientes c ON c.id = a.cliente_id
       INNER JOIN usuarios u ON u.id = c.usuario_id
       INNER JOIN servicos s ON s.id = a.servico_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       WHERE f.usuario_id = $1
       ORDER BY a.created_at DESC`,
      [id]
    );

    const media = result.rows.length > 0
      ? (result.rows.reduce((acc, r) => acc + Number(r.nota), 0) / result.rows.length).toFixed(1)
      : null;

    return res.json({
      fornecedor_usuario_id: Number(id),
      total_avaliacoes: result.rows.length,
      media_geral: media ? Number(media) : null,
      avaliacoes: result.rows,
    });

  } catch (error) {
    console.error('Erro ao listar avaliacoes do fornecedor:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};