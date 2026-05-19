import pool from '../config/db.js';
import multer from 'multer';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import fs from 'fs';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// =========================================
// CONFIGURACAO DO MULTER PARA CHAT
// =========================================
const pastaChat = path.join(__dirname, '../../uploads/chat');

if (!fs.existsSync(pastaChat)) {
  fs.mkdirSync(pastaChat, { recursive: true });
}

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, pastaChat);
  },
  filename: (req, file, cb) => {
    const nomeUnico = `${Date.now()}-${file.originalname.replace(/\s/g, '_')}`;
    cb(null, nomeUnico);
  },
});

const fileFilter = (req, file, cb) => {
  const tiposPermitidos = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'];
  if (tiposPermitidos.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('Apenas imagens JPG, PNG ou WEBP sao permitidas.'), false);
  }
};

export const uploadChat = multer({
  storage,
  fileFilter,
  limits: { fileSize: 200 * 1024 * 1024 }, // 200MB maximo
});

// =========================================
// ENVIAR MENSAGEM
// =========================================
export const enviarMensagem = async (req, res) => {
  try {
    const { id: remetente_id } = req.usuario;
    const { destinatario_id, mensagem } = req.body;

    if (!destinatario_id) {
      if (req.file) fs.unlinkSync(req.file.path);
      return res.status(400).json({ erros: ['ID do destinatario e obrigatorio.'] });
    }

    // Precisa ter texto ou imagem
    if (!mensagem && !req.file) {
      return res.status(400).json({ erros: ['Envie uma mensagem de texto ou uma imagem.'] });
    }

    // Verifica se o destinatario existe
    const destinatarioResult = await pool.query(
      'SELECT id FROM usuarios WHERE id = $1',
      [destinatario_id]
    );

    if (destinatarioResult.rows.length === 0) {
      if (req.file) fs.unlinkSync(req.file.path);
      return res.status(404).json({ erro: 'Destinatario nao encontrado.' });
    }

    // Nao pode mandar mensagem para si mesmo
    if (parseInt(destinatario_id) === remetente_id) {
      if (req.file) fs.unlinkSync(req.file.path);
      return res.status(400).json({ erro: 'Voce nao pode enviar mensagem para si mesmo.' });
    }

    // Monta o conteudo da mensagem
    let conteudo = mensagem || null;
    let url_imagem = null;

    if (req.file) {
      url_imagem = `/uploads/chat/${req.file.filename}`;
    }

    // Salva a mensagem — se tiver imagem, salva a URL como mensagem
    const textoFinal = conteudo || url_imagem;

    const result = await pool.query(
      `INSERT INTO mensagens (remetente_id, destinatario_id, mensagem)
       VALUES ($1, $2, $3)
       RETURNING *`,
      [remetente_id, destinatario_id, textoFinal]
    );

    return res.status(201).json({
      mensagem_enviada: result.rows[0],
      tipo: req.file ? 'imagem' : 'texto',
    });

  } catch (error) {
    if (req.file) fs.unlinkSync(req.file.path);
    console.error('Erro ao enviar mensagem:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR CONVERSA COM UM USUARIO
// =========================================
export const listarConversa = async (req, res) => {
  try {
    const { id: usuario_id } = req.usuario;
    const { outro_usuario_id } = req.params;

    const result = await pool.query(
      `SELECT
        m.id, m.mensagem, m.data_envio,
        m.remetente_id, m.destinatario_id,
        u.nome AS nome_remetente,
        CASE
          WHEN m.mensagem LIKE '/uploads/chat/%' THEN 'imagem'
          ELSE 'texto'
        END AS tipo
       FROM mensagens m
       INNER JOIN usuarios u ON u.id = m.remetente_id
       WHERE (m.remetente_id = $1 AND m.destinatario_id = $2)
          OR (m.remetente_id = $2 AND m.destinatario_id = $1)
       ORDER BY m.data_envio ASC`,
      [usuario_id, outro_usuario_id]
    );

    return res.json(result.rows);

  } catch (error) {
    console.error('Erro ao listar conversa:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR TODAS AS CONVERSAS DO USUARIO
// =========================================
export const listarConversas = async (req, res) => {
  try {
    const { id: usuario_id } = req.usuario;

    // Busca a ultima mensagem de cada conversa
    const result = await pool.query(
      `SELECT DISTINCT ON (outro_usuario)
        outro_usuario,
        u.nome AS nome_outro_usuario,
        u.tipo AS tipo_outro_usuario,
        ultima_mensagem,
        data_envio
       FROM (
         SELECT
           CASE
             WHEN remetente_id = $1 THEN destinatario_id
             ELSE remetente_id
           END AS outro_usuario,
           mensagem AS ultima_mensagem,
           data_envio
         FROM mensagens
         WHERE remetente_id = $1 OR destinatario_id = $1
       ) conversas
       INNER JOIN usuarios u ON u.id = conversas.outro_usuario
       ORDER BY outro_usuario, data_envio DESC`,
      [usuario_id]
    );

    return res.json(result.rows);

  } catch (error) {
    console.error('Erro ao listar conversas:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};