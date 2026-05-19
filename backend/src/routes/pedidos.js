import express from 'express';
import {
  criarPedido,
  listarMeusPedidos,
  buscarPedidoPorId,
  atualizarStatusPedido,
} from '../controllers/pedidosController.js';
import { autenticar } from '../middlewares/authMiddleware.js';

const router = express.Router();

// Todas as rotas de pedidos sao protegidas — precisam de token
router.post('/', autenticar, criarPedido);
router.get('/meus', autenticar, listarMeusPedidos);
router.get('/:id', autenticar, buscarPedidoPorId);
router.patch('/:id/status', autenticar, atualizarStatusPedido);

export default router;