import pool from '../config/db.js';


// CRIAR SERVICO
// Rota protegida — so fornecedor logado
export const criarServico = async (req, res) => {
  try {
    // req.usuario vem do middleware autenticar
    // ele le o token JWT e coloca os dados do usuario aqui
    const { id: usuario_id, tipo } = req.usuario;

    // Garante que so fornecedor pode cadastrar servico
    if (tipo !== 'fornecedor') {
      return res.status(403).json({ erro: 'Apenas fornecedores podem cadastrar servicos.' });
    }

    const { nome, descricao, preco, categoria_id } = req.body;

    const erros = [];

    if (!nome || nome.trim().length === 0) {
      erros.push('Nome do servico e obrigatorio.');
    }

    if (!descricao || descricao.trim().length === 0) {
      erros.push('Descricao e obrigatoria.');
    }

    if (!preco && preco !== 0) {
      erros.push('Preco e obrigatorio.');
    } else if (isNaN(preco) || Number(preco) < 0) {
      erros.push('Preco deve ser um numero positivo.');
    }

    if (!categoria_id) {
      erros.push('Categoria e obrigatoria.');
    }

    if (erros.length > 0) {
      return res.status(400).json({ erros });
    }

    // Verifica se a categoria existe no banco
    const categoriaResult = await pool.query(
      'SELECT id FROM categorias WHERE id = $1',
      [categoria_id]
    );
    if (categoriaResult.rows.length === 0) {
      return res.status(400).json({ erros: ['Categoria invalida.'] });
    }

    // Busca o fornecedor_id a partir do usuario_id
    // Lembra que a tabela fornecedores tem usuario_id ligado a usuarios
    const fornecedorResult = await pool.query(
      'SELECT id FROM fornecedores WHERE usuario_id = $1',
      [usuario_id]
    );
    if (fornecedorResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Perfil de fornecedor nao encontrado.' });
    }

    const fornecedor_id = fornecedorResult.rows[0].id;

    const result = await pool.query(
      `INSERT INTO servicos (fornecedor_id, categoria_id, nome, descricao, preco)
       VALUES ($1, $2, $3, $4, $5)
       RETURNING *`,
      [
        fornecedor_id,
        categoria_id,
        nome.trim(),
        descricao.trim(),
        Number(preco),
      ]
    );

    return res.status(201).json({
      mensagem: 'Servico cadastrado com sucesso!',
      servico: result.rows[0],
    });

  } catch (error) {
    console.error('Erro ao criar servico:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// LISTAR TODOS OS SERVICOS
// Rota publica
export const listarServicos = async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT
        s.id, s.nome, s.descricao, s.preco, s.created_at,
        c.nome AS categoria,
        f.nome_loja, f.preco_medio,
        u.nome AS nome_fornecedor, u.cidade, u.estado
       FROM servicos s
       INNER JOIN categorias c ON c.id = s.categoria_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       INNER JOIN usuarios u ON u.id = f.usuario_id
       ORDER BY s.created_at DESC`
    );
    return res.json(result.rows);
  } catch (error) {
    console.error('Erro ao listar servicos:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// BUSCAR SERVICO POR ID
// Rota publica
export const buscarServicoPorId = async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query(
      `SELECT
        s.id, s.nome, s.descricao, s.preco, s.created_at,
        c.nome AS categoria,
        f.id AS fornecedor_id, f.nome_loja, f.descricao AS descricao_loja,
        f.preco_medio, f.categoria AS categoria_loja,
        u.nome AS nome_fornecedor, u.telefone, u.cidade, u.estado
       FROM servicos s
       INNER JOIN categorias c ON c.id = s.categoria_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       INNER JOIN usuarios u ON u.id = f.usuario_id
       WHERE s.id = $1`,
      [id]
    );
    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Servico nao encontrado.' });
    }
    return res.json(result.rows[0]);
  } catch (error) {
    console.error('Erro ao buscar servico:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// LISTAR SERVICOS DE UM FORNECEDOR
// Rota publica
export const listarServicosPorFornecedor = async (req, res) => {
  try {
    const { id } = req.params; // id do usuario do fornecedor

    const result = await pool.query(
      `SELECT
        s.id, s.nome, s.descricao, s.preco, s.created_at,
        c.nome AS categoria,
        f.nome_loja
       FROM servicos s
       INNER JOIN categorias c ON c.id = s.categoria_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       INNER JOIN usuarios u ON u.id = f.usuario_id
       WHERE u.id = $1
       ORDER BY s.created_at DESC`,
      [id]
    );

    return res.json(result.rows);
  } catch (error) {
    console.error('Erro ao listar servicos do fornecedor:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};