import express from 'express';
import {
  criarAvaliacao,
  listarAvaliacoesPorServico,
  listarAvaliacoesPorFornecedor,
} from '../controllers/avaliacoesController.js';
import { autenticar } from '../middlewares/authMiddleware.js';

const router = express.Router();

// POST   /avaliacoes                      → cria avaliacao (protegida)
router.post('/', autenticar, criarAvaliacao);

// GET    /avaliacoes/servico/:id          → avaliacoes de um servico (publica)
router.get('/servico/:id', listarAvaliacoesPorServico);

// GET    /avaliacoes/fornecedor/:id       → avaliacoes de um fornecedor (publica)
router.get('/fornecedor/:id', listarAvaliacoesPorFornecedor);

export default router;