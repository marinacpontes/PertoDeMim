import pool from '../config/db.js';
import bcrypt from 'bcrypt';
import nodemailer from 'nodemailer';

// =========================================
// CONFIGURACAO DO EMAIL
// =========================================
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS,
  },
});

// Gera codigo de 6 digitos
function gerarCodigo() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

// =========================================
// PASSO 1 — SOLICITAR CODIGO
// Usuario informa o email
// =========================================
export const solicitarCodigo = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ erro: 'E-mail e obrigatorio.' });
    }

    // Verifica se o email existe
    const usuarioResult = await pool.query(
      'SELECT id, nome FROM usuarios WHERE email = $1',
      [email.toLowerCase().trim()]
    );

    // Por seguranca, nao informamos se o email existe ou nao
    if (usuarioResult.rows.length === 0) {
      return res.json({
        mensagem: 'Se este e-mail estiver cadastrado, voce recebera um codigo em breve.'
      });
    }

    const usuario = usuarioResult.rows[0];

    // Invalida codigos anteriores
    await pool.query(
      'UPDATE codigos_recuperacao SET usado = TRUE WHERE usuario_id = $1 AND usado = FALSE',
      [usuario.id]
    );

    // Gera novo codigo com validade de 15 minutos
    const codigo = gerarCodigo();
    const expiraEm = new Date(Date.now() + 15 * 60 * 1000);

    await pool.query(
      `INSERT INTO codigos_recuperacao (usuario_id, codigo, expira_em)
       VALUES ($1, $2, $3)`,
      [usuario.id, codigo, expiraEm]
    );

    // Envia o email
    await transporter.sendMail({
      from: `"Perto de Mim" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: 'Codigo de recuperacao de senha',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto;">
          <h2 style="color: #333;">Recuperacao de senha</h2>
          <p>Ola, <strong>${usuario.nome}</strong>!</p>
          <p>Seu codigo de recuperacao e:</p>
          <div style="background: #f4f4f4; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0;">
            <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #333;">${codigo}</span>
          </div>
          <p>Este codigo expira em <strong>15 minutos</strong>.</p>
          <p>Se voce nao solicitou a recuperacao de senha, ignore este email.</p>
        </div>
      `,
    });

    return res.json({
      mensagem: 'Se este e-mail estiver cadastrado, voce recebera um codigo em breve.'
    });

  } catch (error) {
    console.error('Erro ao solicitar codigo:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// PASSO 2 — VALIDAR CODIGO
// Usuario informa o codigo recebido
// =========================================
export const validarCodigo = async (req, res) => {
  try {
    const { email, codigo } = req.body;

    if (!email || !codigo) {
      return res.status(400).json({ erro: 'E-mail e codigo sao obrigatorios.' });
    }

    // Busca o usuario
    const usuarioResult = await pool.query(
      'SELECT id FROM usuarios WHERE email = $1',
      [email.toLowerCase().trim()]
    );

    if (usuarioResult.rows.length === 0) {
      return res.status(400).json({ erro: 'Codigo invalido ou expirado.' });
    }

    const usuario_id = usuarioResult.rows[0].id;

    // Verifica o codigo
    const codigoResult = await pool.query(
      `SELECT id FROM codigos_recuperacao
       WHERE usuario_id = $1
         AND codigo = $2
         AND usado = FALSE
         AND expira_em > NOW()`,
      [usuario_id, codigo]
    );

    if (codigoResult.rows.length === 0) {
      return res.status(400).json({ erro: 'Codigo invalido ou expirado.' });
    }

    return res.json({ mensagem: 'Codigo validado com sucesso! Agora redefina sua senha.' });

  } catch (error) {
    console.error('Erro ao validar codigo:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// PASSO 3 — REDEFINIR SENHA
// Usuario informa nova senha
// =========================================
export const redefinirSenha = async (req, res) => {
  try {
    const { email, codigo, nova_senha } = req.body;

    if (!email || !codigo || !nova_senha) {
      return res.status(400).json({ erro: 'E-mail, codigo e nova senha sao obrigatorios.' });
    }

    if (nova_senha.length < 6) {
      return res.status(400).json({ erro: 'A nova senha deve ter no minimo 6 caracteres.' });
    }

    // Busca o usuario
    const usuarioResult = await pool.query(
      'SELECT id FROM usuarios WHERE email = $1',
      [email.toLowerCase().trim()]
    );

    if (usuarioResult.rows.length === 0) {
      return res.status(400).json({ erro: 'Codigo invalido ou expirado.' });
    }

    const usuario_id = usuarioResult.rows[0].id;

    // Verifica o codigo novamente
    const codigoResult = await pool.query(
      `SELECT id FROM codigos_recuperacao
       WHERE usuario_id = $1
         AND codigo = $2
         AND usado = FALSE
         AND expira_em > NOW()`,
      [usuario_id, codigo]
    );

    if (codigoResult.rows.length === 0) {
      return res.status(400).json({ erro: 'Codigo invalido ou expirado.' });
    }

    // Atualiza a senha
    const senhaHash = await bcrypt.hash(nova_senha, 10);
    await pool.query(
      'UPDATE usuarios SET senha = $1 WHERE id = $2',
      [senhaHash, usuario_id]
    );

    // Marca o codigo como usado
    await pool.query(
      'UPDATE codigos_recuperacao SET usado = TRUE WHERE id = $1',
      [codigoResult.rows[0].id]
    );

    return res.json({ mensagem: 'Senha redefinida com sucesso!' });

  } catch (error) {
    console.error('Erro ao redefinir senha:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};