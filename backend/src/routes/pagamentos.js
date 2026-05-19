import express from 'express';
import {
  registrarPagamento,
  consultarPagamento,
  atualizarStatusPagamento,
} from '../controllers/pagamentosController.js';
import { autenticar } from '../middlewares/authMiddleware.js';

const router = express.Router();

// POST   /pagamentos/:pedido_id         → registra pagamento (cliente)
router.post('/:pedido_id', autenticar, registrarPagamento);

// GET    /pagamentos/:pedido_id         → consulta pagamento
router.get('/:pedido_id', autenticar, consultarPagamento);

// PATCH  /pagamentos/:pedido_id/status  → atualiza status
router.patch('/:pedido_id/status', autenticar, atualizarStatusPagamento);

export default router;