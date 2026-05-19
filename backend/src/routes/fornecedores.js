import express from 'express';
import {
  criarFornecedor,
  listarFornecedores,
  buscarFornecedorPorId,
} from '../controllers/fornecedoresController.js';

const router = express.Router();

// POST   /fornecedores        → cadastra fornecedor
router.post('/', criarFornecedor);

// GET    /fornecedores        → lista todos
router.get('/', listarFornecedores);

// GET    /fornecedores/:id    → busca por ID
router.get('/:id', buscarFornecedorPorId);

export default router;