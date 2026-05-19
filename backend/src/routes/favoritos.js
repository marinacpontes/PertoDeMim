import express from 'express';
import {
  adicionarFavorito,
  removerFavorito,
  listarFavoritos,
} from '../controllers/favoritosController.js';
import { autenticar } from '../middlewares/authMiddleware.js';

const router = express.Router();

// Todas as rotas de favoritos sao protegidas
router.post('/', autenticar, adicionarFavorito);
router.delete('/:servico_id', autenticar, removerFavorito);
router.get('/', autenticar, listarFavoritos);

export default router;