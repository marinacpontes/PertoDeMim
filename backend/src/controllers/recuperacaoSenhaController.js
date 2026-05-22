import pool from '../config/db.js';
import bcrypt from 'bcrypt';
import { Resend } from 'resend';

const resend = new Resend(process.env.RESEND_API_KEY);

// Gera codigo de 6 digitos
function gerarCodigo() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

// Template do email estilizado
function gerarEmailHtml(nome, codigo) {
  return `
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Recuperacao de senha</title>
</head>
<body style="margin: 0; padding: 0; background-color: #f5f0e8; font-family: Arial, sans-serif;">
  <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f5f0e8; padding: 40px 20px;">
    <tr>
      <td align="center">
        <table width="100%" cellpadding="0" cellspacing="0" style="max-width: 500px; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(27,76,90,0.10);">
          
          <!-- Header -->
          <tr>
            <td style="background-color: #1B4C5A; padding: 36px 40px 28px 40px; text-align: center;">
              <p style="margin: 0; font-size: 28px; font-weight: bold; color: #D7C8A9; letter-spacing: 2px;">PERTO DE MIM</p>
              <p style="margin: 8px 0 0 0; font-size: 13px; color: #7AB8D3; letter-spacing: 1px;">Servicos proximos a voce</p>
            </td>
          </tr>

          <!-- Divisor decorativo -->
          <tr>
            <td style="background: linear-gradient(to right, #C66F53, #7AB8D3); height: 4px;"></td>
          </tr>

          <!-- Corpo -->
          <tr>
            <td style="padding: 40px 40px 20px 40px;">
              <p style="margin: 0 0 8px 0; font-size: 22px; font-weight: bold; color: #1B4C5A;">Recuperacao de senha</p>
              <p style="margin: 0 0 24px 0; font-size: 15px; color: #555; line-height: 1.6;">
                Ola, <strong style="color: #1B4C5A;">${nome}</strong>! Recebemos uma solicitacao para redefinir a senha da sua conta.
              </p>
              <p style="margin: 0 0 16px 0; font-size: 14px; color: #777;">Use o codigo abaixo para continuar:</p>
            </td>
          </tr>

          <!-- Codigo -->
          <tr>
            <td style="padding: 0 40px 32px 40px;">
              <div style="background-color: #f5f0e8; border: 2px solid #D7C8A9; border-radius: 12px; padding: 28px; text-align: center;">
                <p style="margin: 0 0 8px 0; font-size: 12px; color: #A64B3B; letter-spacing: 2px; text-transform: uppercase; font-weight: bold;">Seu codigo</p>
                <p style="margin: 0; font-size: 42px; font-weight: bold; letter-spacing: 12px; color: #1B4C5A;">${codigo}</p>
              </div>
            </td>
          </tr>

          <!-- Aviso de expiracao -->
          <tr>
            <td style="padding: 0 40px 32px 40px;">
              <table width="100%" cellpadding="0" cellspacing="0">
                <tr>
                  <td style="background-color: #fff5f3; border-left: 4px solid #C66F53; border-radius: 4px; padding: 14px 16px;">
                    <p style="margin: 0; font-size: 13px; color: #A64B3B;">
                      ⏱ Este codigo expira em <strong>15 minutos</strong>.
                    </p>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Aviso de seguranca -->
          <tr>
            <td style="padding: 0 40px 36px 40px;">
              <p style="margin: 0; font-size: 12px; color: #999; line-height: 1.6; text-align: center;">
                Se voce nao solicitou a recuperacao de senha, ignore este e-mail.<br>
                Sua senha permanecera a mesma.
              </p>
            </td>
          </tr>

          <!-- Footer -->
          <tr>
            <td style="background-color: #1B4C5A; padding: 20px 40px; text-align: center;">
              <p style="margin: 0; font-size: 12px; color: #7AB8D3;">
                © 2026 Perto de Mim — Todos os direitos reservados
              </p>
            </td>
          </tr>

        </table>
      </td>
    </tr>
  </table>
</body>
</html>
  `;
}

// =========================================
// PASSO 1 — SOLICITAR CODIGO
// =========================================
export const solicitarCodigo = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ erro: 'E-mail e obrigatorio.' });
    }

    const usuarioResult = await pool.query(
      'SELECT id, nome FROM usuarios WHERE email = $1',
      [email.toLowerCase().trim()]
    );

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

    // Envia o email via Resend
    await resend.emails.send({
      from: 'Perto de Mim <onboarding@resend.dev>',
      to: email,
      subject: 'Codigo de recuperacao de senha — Perto de Mim',
      html: gerarEmailHtml(usuario.nome, codigo),
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
// =========================================
export const validarCodigo = async (req, res) => {
  try {
    const { email, codigo } = req.body;

    if (!email || !codigo) {
      return res.status(400).json({ erro: 'E-mail e codigo sao obrigatorios.' });
    }

    const usuarioResult = await pool.query(
      'SELECT id FROM usuarios WHERE email = $1',
      [email.toLowerCase().trim()]
    );

    if (usuarioResult.rows.length === 0) {
      return res.status(400).json({ erro: 'Codigo invalido ou expirado.' });
    }

    const usuario_id = usuarioResult.rows[0].id;

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

    const usuarioResult = await pool.query(
      'SELECT id FROM usuarios WHERE email = $1',
      [email.toLowerCase().trim()]
    );

    if (usuarioResult.rows.length === 0) {
      return res.status(400).json({ erro: 'Codigo invalido ou expirado.' });
    }

    const usuario_id = usuarioResult.rows[0].id;

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

    const senhaHash = await bcrypt.hash(nova_senha, 10);
    await pool.query(
      'UPDATE usuarios SET senha = $1 WHERE id = $2',
      [senhaHash, usuario_id]
    );

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