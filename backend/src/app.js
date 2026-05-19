import express from 'express';
import cors from 'cors';
import usuariosRoutes from './routes/usuarios.js';
import authRoutes from './routes/auth.js';
import fornecedoresRoutes from './routes/fornecedores.js';
import servicosRoutes from './routes/servicos.js';
import pedidosRoutes from './routes/pedidos.js';
import avaliacoesRoutes from './routes/avaliacoes.js';
import favoritosRoutes from './routes/favoritos.js';
import portfolioRoutes from './routes/portfolio.js';
import pagamentosRoutes from './routes/pagamentos.js';
import mensagensRoutes from './routes/mensagens.js';
import recuperacaoRoutes from './routes/recuperacao.js';

const app = express();

app.use(cors());
app.use(express.json());
app.use('/usuarios', usuariosRoutes);
app.use('/auth', authRoutes);
app.use('/fornecedores', fornecedoresRoutes);
app.use('/servicos', servicosRoutes);
app.use('/pedidos', pedidosRoutes);
app.use('/avaliacoes', avaliacoesRoutes);
app.use('/favoritos', favoritosRoutes);
app.use('/uploads', express.static('uploads')); // serve as imagens estaticamente
app.use('/portfolio', portfolioRoutes);
app.use('/pagamentos', pagamentosRoutes);
app.use('/mensagens', mensagensRoutes);
app.use('/recuperacao', recuperacaoRoutes);


app.get('/', (req, res) => {
  res.send('API rodando');
});

// para 
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Servidor rodando na porta ${PORT}`);
});