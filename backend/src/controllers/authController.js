import pool from '../config/db.js';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';

// Login 
// O que acontece aqui:
// 1. Recebe email e senha do body
// 2. Busca o usuario no banco pelo email
// 3. Compara a senha com o hash salvo (bcrypt)
// 4. Se ok, gera um token JWT e devolve para o app

export const login = async (req, res ) => {
    try {
        const { email, senha } = req.body;

        //validação basica 
        
        if (!email || ! senha ){
            return res.status (400).json ({erro: 'E-mail e senha são obrigatórios.'});
        }

        // Busca o usuario pelo email 
        const result = await pool.query(
            'SELECT * FROM usuarios  WHERE email = $1',
            [email.toLowerCase().trim()]
        );

        // Se não encontrar o email
        if (result.rows.length == 0) {
            return res.status(401).json({erro: 'E-mail ou senha inválidos'});
        }

        const usuario = result.rows[0];

        //Compara a senha digitada com o hash do banco 
        //bcrypt.compare retorna true ou false 

            const senhaCorreta = await bcrypt.compare(senha, usuario.senha);        if (!senhaCorreta){
            return res.status(401).json({erro: 'E-mail ou senha inválidos'});
        }

        //Gerar o token JWT
        // O token carrega: id, nome, email e tipo do usuario
        // Expira em 7 dias - depois disso o usuario precisa fazer login de novo 

        const token =jwt.sign(
            {
                id: usuario.id,
                nome:usuario.nome,
                email:usuario.email,
                tipo:usuario.tipo,
            },
            process.env.JWT_SECRET, //chave secreta definida no .env
            {expiresIn: '7d' }
        );
        return res.status(200).json({
            mensagem: 'Login realizado com sucesso!',
            token, 
            usuario: {
                id: usuario.id,
                nome: usuario.nome,
                email: usuario.email,
                tipo: usuario.tipo,
             
            },
        });
    } catch (error){
        console.error ( 'Erro no login:', error);
        return res.status(500).json ({ erro: 'Erro interno no servidor.'});
    
    }

};