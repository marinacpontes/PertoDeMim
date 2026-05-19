import express from 'express';
import {
  upload,
  adicionarImagem,
  removerImagem,
  listarImagensPorServico,
} from '../controllers/portfolioController.js';
import { autenticar } from '../middlewares/authMiddleware.js';

const router = express.Router();

// POST   /portfolio              → adiciona imagem (protegida)
// upload.single('imagem') processa o arquivo antes de chegar no controller
router.post('/', autenticar, upload.single('imagem'), adicionarImagem);

// DELETE /portfolio/:id          → remove imagem (protegida)
router.delete('/:id', autenticar, removerImagem);

// GET    /portfolio/servico/:id  → lista imagens de um servico (publica)
router.get('/servico/:id', listarImagensPorServico);

export default router;