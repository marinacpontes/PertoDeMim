import express from 'express';
import {
  solicitarCodigo,
  validarCodigo,
  redefinirSenha,
} from '../controllers/recuperacaoSenhaController.js';

const router = express.Router();

// POST /recuperacao/solicitar  → envia codigo por email
router.post('/solicitar', solicitarCodigo);

// POST /recuperacao/validar    → valida o codigo
router.post('/validar', validarCodigo);

// POST /recuperacao/redefinir  → redefine a senha
router.post('/redefinir', redefinirSenha);

export default router;