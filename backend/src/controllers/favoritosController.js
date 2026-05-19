import pool from '../config/db.js';

// =========================================
// ADICIONAR FAVORITO
// =========================================
export const adicionarFavorito = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'cliente') {
      return res.status(403).json({ erro: 'Apenas clientes podem adicionar favoritos.' });
    }

    const { servico_id } = req.body;

    if (!servico_id) {
      return res.status(400).json({ erros: ['ID do servico e obrigatorio.'] });
    }

    // Verifica se o servico existe
    const servicoResult = await pool.query(
      'SELECT id FROM servicos WHERE id = $1',
      [servico_id]
    );

    if (servicoResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Servico nao encontrado.' });
    }

    // Busca o cliente_id
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

    // Tenta inserir o favorito
    const result = await pool.query(
      `INSERT INTO favoritos (cliente_id, servico_id)
       VALUES ($1, $2)
       RETURNING *`,
      [cliente_id, servico_id]
    );

    return res.status(201).json({
      mensagem: 'Servico adicionado aos favoritos!',
      favorito: result.rows[0],
    });

  } catch (error) {
    // Erro de duplicidade — ja e favorito
    if (error.code === '23505') {
      return res.status(409).json({ erro: 'Servico ja esta nos favoritos.' });
    }
    console.error('Erro ao adicionar favorito:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// REMOVER FAVORITO
// =========================================
export const removerFavorito = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'cliente') {
      return res.status(403).json({ erro: 'Apenas clientes podem remover favoritos.' });
    }

    const { servico_id } = req.params;

    // Busca o cliente_id
    const clienteResult = await pool.query(
      'SELECT id FROM clientes WHERE usuario_id = $1',
      [usuario_id]
    );

    if (clienteResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Favorito nao encontrado.' });
    }

    const cliente_id = clienteResult.rows[0].id;

    const result = await pool.query(
      'DELETE FROM favoritos WHERE cliente_id = $1 AND servico_id = $2 RETURNING *',
      [cliente_id, servico_id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Favorito nao encontrado.' });
    }

    return res.json({ mensagem: 'Servico removido dos favoritos!' });

  } catch (error) {
    console.error('Erro ao remover favorito:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR FAVORITOS DO CLIENTE
// =========================================
export const listarFavoritos = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'cliente') {
      return res.status(403).json({ erro: 'Apenas clientes podem ver favoritos.' });
    }

    const clienteResult = await pool.query(
      'SELECT id FROM clientes WHERE usuario_id = $1',
      [usuario_id]
    );

    if (clienteResult.rows.length === 0) {
      return res.json([]);
    }

    const cliente_id = clienteResult.rows[0].id;

    const result = await pool.query(
      `SELECT
        f.id AS favorito_id,
        s.id AS servico_id,
        s.nome AS servico,
        s.descricao,
        s.preco,
        c.nome AS categoria,
        fo.nome_loja,
        u.nome AS nome_fornecedor,
        u.cidade,
        u.estado
       FROM favoritos f
       INNER JOIN servicos s ON s.id = f.servico_id
       INNER JOIN categorias c ON c.id = s.categoria_id
       INNER JOIN fornecedores fo ON fo.id = s.fornecedor_id
       INNER JOIN usuarios u ON u.id = fo.usuario_id
       WHERE f.cliente_id = $1
       ORDER BY f.id DESC`,
      [cliente_id]
    );

    return res.json(result.rows);

  } catch (error) {
    console.error('Erro ao listar favoritos:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};