import pkg from 'pg';
import dotenv from 'dotenv';

dotenv.config();
const { Client } = pkg;

// Configuração para conectar ao Postgres padrão
const configBase = {
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
  database: 'postgres' // Conecta no banco padrão primeiro
};

async function init() {
  const client = new Client(configBase);
  try {
    await client.connect();
    console.log("Conectado ao PostgreSQL!");

    // 1. Cria o banco de dados
    try {
      await client.query(`CREATE DATABASE ${process.env.DB_NAME}`);
      console.log(`Banco de dados "${process.env.DB_NAME}" criado com sucesso!`);
    } catch (err) {
      if (err.code === '42P04') {
        console.log(`O banco de dados "${process.env.DB_NAME}" já existe.`);
      } else { throw err; }
    }
    await client.end();

    // 2. Conecta no banco novo para criar a tabela
    const clientNovo = new Client({ ...configBase, database: process.env.DB_NAME });
    await clientNovo.connect();

    const createTableQuery = `
      CREATE TABLE IF NOT EXISTS usuarios (
        id SERIAL PRIMARY KEY,
        nome VARCHAR(40) NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        senha VARCHAR(255) NOT NULL,
        telefone VARCHAR(11) NOT NULL,
        cpf_cnpj VARCHAR(14) UNIQUE NOT NULL,
        tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('cliente', 'fornecedor')),
        logradouro VARCHAR(50) NOT NULL,
        cep CHAR(8) NOT NULL,
        numero VARCHAR(20) NOT NULL,
        bairro VARCHAR(100) NOT NULL,
        complemento VARCHAR(100),
        cidade VARCHAR(100) NOT NULL,
        estado CHAR(2) NOT NULL,
        data_nascimento DATE NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    `;

    await clientNovo.query(createTableQuery);
    console.log("Tabela 'usuarios' criada ou já existente!");
    await clientNovo.end();

    console.log("\n>>> TUDO PRONTO! Agora você pode rodar 'node src/app.js'");

  } catch (err) {
    console.error("ERRO AO CONFIGURAR BANCO:", err.message);
    process.exit(1);
  }
}

init();