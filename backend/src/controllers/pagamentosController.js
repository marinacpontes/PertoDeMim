import pool from '../config/db.js';

const FORMAS_PAGAMENTO = ['pix', 'cartao_credito', 'cartao_debito'];

// =========================================
// REGISTRAR PAGAMENTO
// Cliente informa a forma de pagamento
// =========================================
export const registrarPagamento = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'cliente') {
      return res.status(403).json({ erro: 'Apenas clientes podem registrar pagamentos.' });
    }

    const { pedido_id } = req.params;
    const { forma_pagamento } = req.body;

    if (!forma_pagamento) {
      return res.status(400).json({ erros: ['Forma de pagamento e obrigatoria.'] });
    }

    if (!FORMAS_PAGAMENTO.includes(forma_pagamento)) {
      return res.status(400).json({
        erros: [`Forma de pagamento invalida. Opcoes: ${FORMAS_PAGAMENTO.join(', ')}.`]
      });
    }

    // Verifica se o pedido existe e pertence ao cliente
    const pedidoResult = await pool.query(
      `SELECT p.id, p.status, p.valor, c.usuario_id AS cliente_usuario_id
       FROM pedidos p
       INNER JOIN clientes c ON c.id = p.cliente_id
       WHERE p.id = $1`,
      [pedido_id]
    );

    if (pedidoResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Pedido nao encontrado.' });
    }

    const pedido = pedidoResult.rows[0];

    if (pedido.cliente_usuario_id !== usuario_id) {
      return res.status(403).json({ erro: 'Voce nao tem permissao para pagar este pedido.' });
    }

    // So pode pagar pedido aceito
    if (pedido.status !== 'aceito') {
      return res.status(400).json({
        erro: `Nao e possivel pagar um pedido com status "${pedido.status}". O pedido precisa estar aceito.`
      });
    }

    // Verifica se ja existe pagamento para este pedido
    const pagamentoExistente = await pool.query(
      'SELECT id FROM pagamentos WHERE pedido_id = $1',
      [pedido_id]
    );

    if (pagamentoExistente.rows.length > 0) {
      return res.status(409).json({ erro: 'Este pedido ja possui um pagamento registrado.' });
    }

    // Registra o pagamento
    const result = await pool.query(
      `INSERT INTO pagamentos (pedido_id, forma_pagamento, status)
       VALUES ($1, $2, 'aguardando')
       RETURNING *`,
      [pedido_id, forma_pagamento]
    );

    return res.status(201).json({
      mensagem: 'Pagamento registrado com sucesso!',
      pagamento: result.rows[0],
    });

  } catch (error) {
    console.error('Erro ao registrar pagamento:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// CONSULTAR PAGAMENTO DE UM PEDIDO
// =========================================
export const consultarPagamento = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;
    const { pedido_id } = req.params;

    const result = await pool.query(
      `SELECT
        pg.id, pg.forma_pagamento, pg.status, pg.created_at,
        p.valor, p.status AS status_pedido,
        s.nome AS servico,
        uc.nome AS nome_cliente,
        uf.nome AS nome_fornecedor
       FROM pagamentos pg
       INNER JOIN pedidos p ON p.id = pg.pedido_id
       INNER JOIN servicos s ON s.id = p.servico_id
       INNER JOIN clientes c ON c.id = p.cliente_id
       INNER JOIN usuarios uc ON uc.id = c.usuario_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       INNER JOIN usuarios uf ON uf.id = f.usuario_id
       WHERE pg.pedido_id = $1`,
      [pedido_id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Pagamento nao encontrado.' });
    }

    return res.json(result.rows[0]);

  } catch (error) {
    console.error('Erro ao consultar pagamento:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// ATUALIZAR STATUS DO PAGAMENTO
// Fornecedor confirma que recebeu
// =========================================
export const atualizarStatusPagamento = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;
    const { pedido_id } = req.params;
    const { status } = req.body;

    const statusPermitidos = ['pago', 'cancelado'];

    if (!status || !statusPermitidos.includes(status)) {
      return res.status(400).json({
        erro: `Status invalido. Opcoes: ${statusPermitidos.join(', ')}.`
      });
    }

    // Verifica se o pagamento existe
    const pagamentoResult = await pool.query(
      `SELECT pg.id, pg.status,
              c.usuario_id AS cliente_usuario_id,
              f.usuario_id AS fornecedor_usuario_id
       FROM pagamentos pg
       INNER JOIN pedidos p ON p.id = pg.pedido_id
       INNER JOIN clientes c ON c.id = p.cliente_id
       INNER JOIN servicos s ON s.id = p.servico_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       WHERE pg.pedido_id = $1`,
      [pedido_id]
    );

    if (pagamentoResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Pagamento nao encontrado.' });
    }

    const pagamento = pagamentoResult.rows[0];

    // Fornecedor confirma recebimento (pago)
    // Cliente pode cancelar
    if (status === 'pago' && tipo !== 'fornecedor') {
      return res.status(403).json({ erro: 'Apenas o fornecedor pode confirmar o recebimento.' });
    }

    if (status === 'cancelado' && tipo !== 'cliente') {
      return res.status(403).json({ erro: 'Apenas o cliente pode cancelar o pagamento.' });
    }

    if (pagamento.status !== 'aguardando') {
      return res.status(400).json({
        erro: `Pagamento ja esta ${pagamento.status} e nao pode ser alterado.`
      });
    }

    const result = await pool.query(
      'UPDATE pagamentos SET status = $1 WHERE pedido_id = $2 RETURNING *',
      [status, pedido_id]
    );

    return res.json({
      mensagem: 'Status do pagamento atualizado!',
      pagamento: result.rows[0],
    });

  } catch (error) {
    console.error('Erro ao atualizar pagamento:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};