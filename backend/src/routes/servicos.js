import express from 'express';
import {
  criarServico,
  listarServicos,
  buscarServicoPorId,
  listarServicosPorFornecedor,
} from '../controllers/servicosController.js';
import { autenticar } from '../middlewares/authMiddleware.js';

const router = express.Router();

router.post('/', autenticar, criarServico);
router.get('/', listarServicos);
router.get('/fornecedor/:id', listarServicosPorFornecedor);
router.get('/:id', buscarServicoPorId);

export default router;