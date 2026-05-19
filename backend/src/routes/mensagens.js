import express from 'express';
import {
  uploadChat,
  enviarMensagem,
  listarConversa,
  listarConversas,
} from '../controllers/mensagensController.js';
import { autenticar } from '../middlewares/authMiddleware.js';

const router = express.Router();

// POST   /mensagens                        → envia mensagem (texto ou imagem)
router.post('/', autenticar, uploadChat.single('imagem'), enviarMensagem);

// GET    /mensagens/conversas              → lista todas as conversas
router.get('/conversas', autenticar, listarConversas);

// GET    /mensagens/:outro_usuario_id      → lista conversa com um usuario
router.get('/:outro_usuario_id', autenticar, listarConversa);

export default router;