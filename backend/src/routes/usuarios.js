import express from 'express';
import {
  criarUsuario,
  listarUsuarios,
  buscarUsuarioPorId,
} from '../controllers/usuariosController.js';

const router = express.Router();

router.post('/', criarUsuario);
router.get('/', listarUsuarios);
router.get('/:id', buscarUsuarioPorId);

export default router;