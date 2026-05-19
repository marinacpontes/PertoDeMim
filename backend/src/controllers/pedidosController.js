import pool from '../config/db.js';

// =========================================
// CRIAR PEDIDO
// Apenas cliente logado pode criar
// =========================================
export const criarPedido = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'cliente') {
      return res.status(403).json({ erro: 'Apenas clientes podem criar pedidos.' });
    }

    const { servico_id } = req.body;

    if (!servico_id) {
      return res.status(400).json({ erros: ['ID do servico e obrigatorio.'] });
    }

    const servicoResult = await pool.query(
      `SELECT s.id, s.nome, s.preco, f.usuario_id AS fornecedor_usuario_id
       FROM servicos s
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       WHERE s.id = $1`,
      [servico_id]
    );

    if (servicoResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Servico nao encontrado.' });
    }

    const servico = servicoResult.rows[0];

    if (servico.fornecedor_usuario_id === usuario_id) {
      return res.status(400).json({ erro: 'Voce nao pode contratar seu proprio servico.' });
    }

    const clienteResult = await pool.query(
      'SELECT id FROM clientes WHERE usuario_id = $1',
      [usuario_id]
    );

    let cliente_id;
    if (clienteResult.rows.length === 0) {
      const novoCliente = await pool.query(
        'INSERT INTO clientes (usuario_id) VALUES ($1) RETURNING id',
        [usuario_id]
      );
      cliente_id = novoCliente.rows[0].id;
    } else {
      cliente_id = clienteResult.rows[0].id;
    }

    const result = await pool.query(
      `INSERT INTO pedidos (cliente_id, servico_id, status, valor)
       VALUES ($1, $2, 'pendente', $3)
       RETURNING *`,
      [cliente_id, servico_id, servico.preco]
    );

    return res.status(201).json({
      mensagem: 'Pedido criado com sucesso!',
      pedido: result.rows[0],
    });

  } catch (error) {
    console.error('Erro ao criar pedido:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR PEDIDOS DO USUARIO LOGADO
// =========================================
export const listarMeusPedidos = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    let result;

    if (tipo === 'cliente') {
      result = await pool.query(
        `SELECT
          p.id, p.status, p.valor, p.data, p.data_aceito,
          s.nome AS servico,
          u.nome AS nome_fornecedor,
          f.nome_loja
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id
         INNER JOIN servicos s ON s.id = p.servico_id
         INNER JOIN fornecedores f ON f.id = s.fornecedor_id
         INNER JOIN usuarios u ON u.id = f.usuario_id
         WHERE c.usuario_id = $1
         ORDER BY p.data DESC`,
        [usuario_id]
      );
    } else {
      result = await pool.query(
        `SELECT
          p.id, p.status, p.valor, p.data, p.data_aceito,
          s.nome AS servico,
          u.nome AS nome_cliente,
          u.telefone AS telefone_cliente
         FROM pedidos p
         INNER JOIN servicos s ON s.id = p.servico_id
         INNER JOIN fornecedores f ON f.id = s.fornecedor_id
         INNER JOIN clientes c ON c.id = p.cliente_id
         INNER JOIN usuarios u ON u.id = c.usuario_id
         WHERE f.usuario_id = $1
         ORDER BY p.data DESC`,
        [usuario_id]
      );
    }

    return res.json(result.rows);

  } catch (error) {
    console.error('Erro ao listar pedidos:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// BUSCAR PEDIDO POR ID
// =========================================
export const buscarPedidoPorId = async (req, res) => {
  try {
    const { id } = req.params;

    const result = await pool.query(
      `SELECT
        p.id, p.status, p.valor, p.data, p.data_aceito,
        s.nome AS servico, s.descricao AS descricao_servico,
        f.nome_loja,
        uf.nome AS nome_fornecedor,
        uc.nome AS nome_cliente
       FROM pedidos p
       INNER JOIN servicos s ON s.id = p.servico_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       INNER JOIN usuarios uf ON uf.id = f.usuario_id
       INNER JOIN clientes c ON c.id = p.cliente_id
       INNER JOIN usuarios uc ON uc.id = c.usuario_id
       WHERE p.id = $1`,
      [id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Pedido nao encontrado.' });
    }

    return res.json(result.rows[0]);

  } catch (error) {
    console.error('Erro ao buscar pedido:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// ATUALIZAR STATUS DO PEDIDO
// Fornecedor: aceito, recusado, concluido
// Cliente: cancelado (apenas ate 2 horas apos aceito)
// =========================================
export const atualizarStatusPedido = async (req, res) => {
  try {
    const { id } = req.params;
    const { status } = req.body;
    const { id: usuario_id, tipo } = req.usuario;

    const statusPermitidosFornecedor = ['aceito', 'recusado', 'concluido'];
    const statusPermitidosCliente = ['cancelado'];

    if (tipo === 'fornecedor' && !statusPermitidosFornecedor.includes(status)) {
      return res.status(400).json({
        erro: `Fornecedor so pode mudar para: ${statusPermitidosFornecedor.join(', ')}.`
      });
    }

    if (tipo === 'cliente' && !statusPermitidosCliente.includes(status)) {
      return res.status(400).json({
        erro: 'Cliente so pode cancelar o pedido.'
      });
    }

    const pedidoResult = await pool.query(
      `SELECT p.id, p.status, p.data_aceito,
              c.usuario_id AS cliente_usuario_id,
              f.usuario_id AS fornecedor_usuario_id
       FROM pedidos p
       INNER JOIN clientes c ON c.id = p.cliente_id
       INNER JOIN servicos s ON s.id = p.servico_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       WHERE p.id = $1`,
      [id]
    );

    if (pedidoResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Pedido nao encontrado.' });
    }

    const pedido = pedidoResult.rows[0];

    if (tipo === 'cliente' && pedido.cliente_usuario_id !== usuario_id) {
      return res.status(403).json({ erro: 'Voce nao tem permissao para alterar este pedido.' });
    }

    if (tipo === 'fornecedor' && pedido.fornecedor_usuario_id !== usuario_id) {
      return res.status(403).json({ erro: 'Voce nao tem permissao para alterar este pedido.' });
    }

    if (['concluido', 'cancelado', 'recusado'].includes(pedido.status)) {
      return res.status(400).json({
        erro: `Pedido ja esta ${pedido.status} e nao pode ser alterado.`
      });
    }

    // Valida prazo de cancelamento — 2 horas apos aceito
    if (tipo === 'cliente' && status === 'cancelado') {
      if (!pedido.data_aceito) {
        return res.status(400).json({
          erro: 'Nao e possivel cancelar um pedido que ainda nao foi aceito pelo fornecedor.'
        });
      }

      const dataAceito = new Date(pedido.data_aceito);
      const agora = new Date();
      const diferencaHoras = (agora - dataAceito) / (1000 * 60 * 60);

      if (diferencaHoras > 2) {
        return res.status(400).json({
          erro: 'Prazo de cancelamento expirado. O pedido so pode ser cancelado ate 2 horas apos ser aceito.'
        });
      }
    }

    // Se fornecedor aceitar, salva a data de aceite
    const novaDataAceito = (tipo === 'fornecedor' && status === 'aceito')
      ? new Date()
      : pedido.data_aceito;

    const result = await pool.query(
      `UPDATE pedidos SET status = $1, data_aceito = $2 WHERE id = $3 RETURNING *`,
      [status, novaDataAceito, id]
    );

    return res.json({
      mensagem: 'Status atualizado com sucesso!',
      pedido: result.rows[0],
    });

  } catch (error) {
    console.error('Erro ao atualizar pedido:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};