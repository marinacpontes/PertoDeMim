import pool from '../config/db.js';
import multer from 'multer';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import fs from 'fs';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// =========================================
// CONFIGURACAO DO MULTER
// Define onde e como salvar as imagens
// =========================================

// Pasta onde as imagens serao salvas
const pastaUploads = path.join(__dirname, '../../uploads');

// Cria a pasta se nao existir
if (!fs.existsSync(pastaUploads)) {
  fs.mkdirSync(pastaUploads, { recursive: true });
}

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, pastaUploads);
  },
  filename: (req, file, cb) => {
    // Gera nome unico: timestamp + nome original
    const nomeUnico = `${Date.now()}-${file.originalname.replace(/\s/g, '_')}`;
    cb(null, nomeUnico);
  },
});

// Valida que so aceita imagens
const fileFilter = (req, file, cb) => {
  const tiposPermitidos = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'];
  if (tiposPermitidos.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('Apenas imagens JPG, PNG ou WEBP sao permitidas.'), false);
  }
};

export const upload = multer({
  storage,
  fileFilter,
  limits: { fileSize: 5 * 1024 * 1024 }, // 5MB maximo
});

// =========================================
// ADICIONAR IMAGEM AO PORTFOLIO
// Apenas fornecedor logado
// =========================================
export const adicionarImagem = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'fornecedor') {
      // Remove o arquivo se nao for fornecedor
      if (req.file) fs.unlinkSync(req.file.path);
      return res.status(403).json({ erro: 'Apenas fornecedores podem adicionar imagens.' });
    }

    if (!req.file) {
      return res.status(400).json({ erro: 'Nenhuma imagem enviada.' });
    }

    const { servico_id } = req.body;

    if (!servico_id) {
      fs.unlinkSync(req.file.path);
      return res.status(400).json({ erros: ['ID do servico e obrigatorio.'] });
    }

    // Verifica se o servico pertence ao fornecedor
    const servicoResult = await pool.query(
      `SELECT s.id FROM servicos s
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       WHERE s.id = $1 AND f.usuario_id = $2`,
      [servico_id, usuario_id]
    );

    if (servicoResult.rows.length === 0) {
      fs.unlinkSync(req.file.path);
      return res.status(403).json({ erro: 'Servico nao encontrado ou nao pertence a voce.' });
    }

    // Salva a URL da imagem no banco
    const url_imagem = `/uploads/${req.file.filename}`;

    const result = await pool.query(
      `INSERT INTO portifolio (servico_id, url_imagem)
       VALUES ($1, $2)
       RETURNING *`,
      [servico_id, url_imagem]
    );

    return res.status(201).json({
      mensagem: 'Imagem adicionada ao portfolio!',
      imagem: result.rows[0],
    });

  } catch (error) {
    if (req.file) fs.unlinkSync(req.file.path);
    console.error('Erro ao adicionar imagem:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// REMOVER IMAGEM DO PORTFOLIO
// =========================================
export const removerImagem = async (req, res) => {
  try {
    const { id: usuario_id, tipo } = req.usuario;

    if (tipo !== 'fornecedor') {
      return res.status(403).json({ erro: 'Apenas fornecedores podem remover imagens.' });
    }

    const { id } = req.params;

    // Verifica se a imagem pertence ao fornecedor
    const imagemResult = await pool.query(
      `SELECT p.id, p.url_imagem FROM portifolio p
       INNER JOIN servicos s ON s.id = p.servico_id
       INNER JOIN fornecedores f ON f.id = s.fornecedor_id
       WHERE p.id = $1 AND f.usuario_id = $2`,
      [id, usuario_id]
    );

    if (imagemResult.rows.length === 0) {
      return res.status(404).json({ erro: 'Imagem nao encontrada ou nao pertence a voce.' });
    }

    const imagem = imagemResult.rows[0];

    // Remove o arquivo fisico
    const caminhoArquivo = path.join(__dirname, '../..', imagem.url_imagem);
    if (fs.existsSync(caminhoArquivo)) {
      fs.unlinkSync(caminhoArquivo);
    }

    // Remove do banco
    await pool.query('DELETE FROM portifolio WHERE id = $1', [id]);

    return res.json({ mensagem: 'Imagem removida do portfolio!' });

  } catch (error) {
    console.error('Erro ao remover imagem:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR IMAGENS DE UM SERVICO
// Rota publica
// =========================================
export const listarImagensPorServico = async (req, res) => {
  try {
    const { id } = req.params;

    const result = await pool.query(
      `SELECT id, url_imagem, servico_id
       FROM portifolio
       WHERE servico_id = $1
       ORDER BY id`,
      [id]
    );

    return res.json(result.rows);

  } catch (error) {
    console.error('Erro ao listar imagens:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};