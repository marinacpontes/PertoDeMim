import pool from '../config/db.js';
import bcrypt from 'bcrypt';

// =========================================
// VALIDACOES
// =========================================

function validarEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.(com|com\.br|net|org|edu\.br|gov\.br)$/i;
  return regex.test(email);
}

function validarTelefone(telefone) {
  const numeros = telefone.replace(/\D/g, '');
  if (numeros.length !== 10 && numeros.length !== 11) return false;

  const ddd = parseInt(numeros.substring(0, 2), 10);
  const dddsValidos = [
    11,12,13,14,15,16,17,18,19,
    21,22,24,27,28,
    31,32,33,34,35,37,38,
    41,42,43,44,45,46,47,48,49,
    51,53,54,55,
    61,62,63,64,65,66,67,68,69,
    71,73,74,75,77,79,
    81,82,83,84,85,86,87,88,89,
    91,92,93,94,95,96,97,98,99
  ];
  if (!dddsValidos.includes(ddd)) return false;
  if (numeros.length === 11 && numeros[2] !== '9') return false;

  return true;
}

function validarCPF(cpf) {
  const n = cpf.replace(/\D/g, '');
  if (n.length !== 11) return false;
  if (/^(\d)\1{10}$/.test(n)) return false;

  let soma = 0;
  for (let i = 0; i < 9; i++) soma += parseInt(n[i]) * (10 - i);
  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(n[9])) return false;

  soma = 0;
  for (let i = 0; i < 10; i++) soma += parseInt(n[i]) * (11 - i);
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(n[10])) return false;

  return true;
}

function validarCNPJ(cnpj) {
  const n = cnpj.replace(/\D/g, '');
  if (n.length !== 14) return false;
  if (/^(\d)\1{13}$/.test(n)) return false;

  const calc = (base, pesos) =>
    base.split('').reduce((acc, d, i) => acc + parseInt(d) * pesos[i], 0);

  const p1 = [5,4,3,2,9,8,7,6,5,4,3,2];
  const p2 = [6,5,4,3,2,9,8,7,6,5,4,3,2];

  const r1 = calc(n.slice(0,12), p1) % 11;
  if ((r1 < 2 ? 0 : 11 - r1) !== parseInt(n[12])) return false;

  const r2 = calc(n.slice(0,13), p2) % 11;
  if ((r2 < 2 ? 0 : 11 - r2) !== parseInt(n[13])) return false;

  return true;
}

function validarCpfCnpj(valor) {
  const n = valor.replace(/\D/g, '');
  if (n.length === 11) return validarCPF(valor);
  if (n.length === 14) return validarCNPJ(valor);
  return false;
}

function validarCEP(cep) {
  return cep.replace(/\D/g, '').length === 8;
}

function validarDataNascimento(data, tipo) {
  const regex = /^\d{4}-\d{2}-\d{2}$/;
  if (!regex.test(data)) return { valido: false, mensagem: 'Data de nascimento invalida. Use o formato YYYY-MM-DD.' };

  const dataObj = new Date(data);
  if (isNaN(dataObj.getTime())) return { valido: false, mensagem: 'Data de nascimento invalida.' };

  if (dataObj > new Date()) return { valido: false, mensagem: 'Data de nascimento nao pode ser uma data futura.' };

  const hoje = new Date();
  const idadeMinima = tipo === 'fornecedor' ? 18 : 16;
  const limiteIdadeMinima = new Date(hoje.getFullYear() - idadeMinima, hoje.getMonth(), hoje.getDate());
  if (dataObj > limiteIdadeMinima) {
    return {
      valido: false,
      mensagem: `Fornecedor deve ter no minimo ${idadeMinima} anos.`
    };
  }

  const limiteIdadeMaxima = new Date(hoje.getFullYear() - 120, hoje.getMonth(), hoje.getDate());
  if (dataObj < limiteIdadeMaxima) return { valido: false, mensagem: 'Data de nascimento invalida.' };

  return { valido: true };
}

// Categorias permitidas
const CATEGORIAS_VALIDAS = [
  'Beleza e Estetica',
  'Saude',
  'Alimentacao',
  'Manutencao',
  'Tecnologia',
  'Outros',
];

// =========================================
// CRIAR FORNECEDOR
// =========================================
export const criarFornecedor = async (req, res) => {
  const client = await pool.connect();

  try {
    const {
      // Dados pessoais (vao para usuarios)
      nome, email, senha, telefone, cpf_cnpj, data_nascimento,
      logradouro, cep, numero, bairro, complemento, cidade, estado,
      // Dados da loja (vao para fornecedores)
      nome_loja, nome_responsavel, categoria, categoria_outro,
      descricao, preco_medio,
    } = req.body;

    const erros = [];

    // --- Validacoes dos dados pessoais ---
    if (!nome || nome.trim().length === 0) {
      erros.push('Nome e obrigatorio.');
    } else if (nome.trim().length > 40) {
      erros.push('Nome deve ter no maximo 40 caracteres.');
    }

    if (!email) {
      erros.push('E-mail e obrigatorio.');
    } else if (!validarEmail(email)) {
      erros.push('E-mail invalido. Use um formato como usuario@dominio.com ou .com.br');
    }

    if (!senha || senha.length < 6) {
      erros.push('Senha obrigatoria e deve ter no minimo 6 caracteres.');
    }

    if (!telefone) {
      erros.push('Telefone e obrigatorio.');
    } else if (!validarTelefone(telefone)) {
      erros.push('Telefone invalido. Informe DDD + numero (ex: 85999999999).');
    }

    if (!cpf_cnpj) {
      erros.push('CPF ou CNPJ e obrigatorio.');
    } else if (!validarCpfCnpj(cpf_cnpj)) {
      erros.push('CPF (11 digitos) ou CNPJ (14 digitos) invalido.');
    }

    if (!data_nascimento) {
      erros.push('Data de nascimento e obrigatoria.');
    } else {
      const resultadoData = validarDataNascimento(data_nascimento, 'fornecedor');
      if (!resultadoData.valido) erros.push(resultadoData.mensagem);
    }

    if (!logradouro || logradouro.trim().length === 0) {
      erros.push('Logradouro e obrigatorio.');
    } else if (logradouro.trim().length > 50) {
      erros.push('Logradouro deve ter no maximo 50 caracteres.');
    }

    if (!cep) {
      erros.push('CEP e obrigatorio.');
    } else if (!validarCEP(cep)) {
      erros.push('CEP invalido. Informe 8 digitos (ex: 60000000).');
    }

    if (!numero || numero.toString().trim().length === 0) {
      erros.push('Numero do endereco e obrigatorio.');
    }

    if (!bairro || bairro.trim().length === 0) {
      erros.push('Bairro e obrigatorio.');
    }

    if (!cidade || cidade.trim().length === 0) {
      erros.push('Cidade e obrigatoria.');
    }

    if (!estado || estado.trim().length === 0) {
      erros.push('Estado e obrigatorio.');
    }

    // --- Validacoes dos dados da loja ---
    if (!nome_loja || nome_loja.trim().length === 0) {
      erros.push('Nome da loja e obrigatorio.');
    }

    if (!nome_responsavel || nome_responsavel.trim().length === 0) {
      erros.push('Nome do responsavel e obrigatorio.');
    }

    if (!categoria) {
      erros.push('Categoria e obrigatoria.');
    } else if (!CATEGORIAS_VALIDAS.includes(categoria)) {
      erros.push(`Categoria invalida. Opcoes: ${CATEGORIAS_VALIDAS.join(', ')}.`);
    } else if (categoria === 'Outros') {
      // Se escolheu Outros, precisa preencher a caixa de texto
      if (!categoria_outro || categoria_outro.trim().length === 0) {
        erros.push('Ao selecionar "Outros", descreva a categoria.');
      }
    }

    if (!descricao || descricao.trim().length === 0) {
      erros.push('Descricao e obrigatoria.');
    }

    if (!preco_medio && preco_medio !== 0) {
      erros.push('Preco medio e obrigatorio.');
    } else if (isNaN(preco_medio) || Number(preco_medio) < 0) {
      erros.push('Preco medio deve ser um numero positivo.');
    }

    if (erros.length > 0) {
      return res.status(400).json({ erros });
    }

    // =========================================
    // TRANSACAO — salva nas duas tabelas
    // =========================================
    // O que e uma transacao?
    // E uma operacao que ou salva tudo ou nao salva nada.
    // Se salvar no usuarios mas falhar no fornecedores,
    // o banco desfaz tudo automaticamente (ROLLBACK).
    // Assim nao fica dados incompletos no banco.
    // =========================================

    await client.query('BEGIN');

    const senhaHash = await bcrypt.hash(senha, 10);

    // 1. Insere na tabela usuarios
    const resultUsuario = await client.query(
      `INSERT INTO usuarios
        (nome, email, senha, telefone, cpf_cnpj, tipo,
         logradouro, cep, numero, bairro, complemento, cidade, estado,
         data_nascimento)
       VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14)
       RETURNING id, nome, email, telefone, cpf_cnpj, tipo,
                 logradouro, cep, numero, bairro, complemento, cidade, estado,
                 data_nascimento, created_at`,
      [
        nome.trim(),
        email.toLowerCase().trim(),
        senhaHash,
        telefone.replace(/\D/g, ''),
        cpf_cnpj.replace(/\D/g, ''),
        'fornecedor',             // tipo sempre "fornecedor" nesta rota
        logradouro.trim(),
        cep.replace(/\D/g, ''),
        numero.toString().trim(),
        bairro.trim(),
        complemento ? complemento.trim() : null,
        cidade.trim(),
        estado.trim().toUpperCase(),
        data_nascimento,
      ]
    );

    const usuario = resultUsuario.rows[0];

    // 2. Insere na tabela fornecedores usando o id gerado acima
    const resultFornecedor = await client.query(
      `INSERT INTO fornecedores
        (usuario_id, nome_loja, nome_responsavel, categoria, categoria_outro,
         descricao, preco_medio)
       VALUES ($1,$2,$3,$4,$5,$6,$7)
       RETURNING *`,
      [
        usuario.id,
        nome_loja.trim(),
        nome_responsavel.trim(),
        categoria,
        categoria === 'Outros' ? categoria_outro.trim() : null,
        descricao.trim(),
        Number(preco_medio),
      ]
    );

    // Confirma a transacao — tudo certo, salva de vez
    await client.query('COMMIT');

    return res.status(201).json({
      mensagem: 'Fornecedor cadastrado com sucesso!',
      usuario,
      loja: resultFornecedor.rows[0],
    });

  } catch (error) {
    // Se algo deu errado, desfaz tudo
    await client.query('ROLLBACK');

    if (error.code === '23505') {
      if (error.constraint === 'usuarios_cpf_cnpj_unique') {
        return res.status(409).json({ erro: 'CPF ou CNPJ ja cadastrado.' });
      }
      return res.status(409).json({ erro: 'E-mail ja cadastrado.' });
    }

    console.error('Erro ao cadastrar fornecedor:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  } finally {
    // Libera a conexao de volta para o pool
    client.release();
  }
};

// =========================================
// LISTAR FORNECEDORES
// =========================================
export const listarFornecedores = async (req, res) => {
  try {
    // JOIN entre as duas tabelas para retornar tudo junto
    const result = await pool.query(
      `SELECT
        u.id, u.nome, u.email, u.telefone, u.tipo,
        u.logradouro, u.cidade, u.estado,
        f.id AS fornecedor_id, f.nome_loja, f.nome_responsavel,
        f.categoria, f.categoria_outro, f.descricao, f.preco_medio
       FROM usuarios u
       INNER JOIN fornecedores f ON f.usuario_id = u.id
       WHERE u.tipo = 'fornecedor'
       ORDER BY u.id`
    );
    return res.json(result.rows);
  } catch (error) {
    console.error('Erro ao listar fornecedores:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// BUSCAR FORNECEDOR POR ID
// =========================================
export const buscarFornecedorPorId = async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query(
      `SELECT
        u.id, u.nome, u.email, u.telefone, u.tipo,
        u.logradouro, u.cep, u.numero, u.bairro, u.complemento, u.cidade, u.estado,
        u.data_nascimento, u.created_at,
        f.id AS fornecedor_id, f.nome_loja, f.nome_responsavel,
        f.categoria, f.categoria_outro, f.descricao, f.preco_medio
       FROM usuarios u
       INNER JOIN fornecedores f ON f.usuario_id = u.id
       WHERE u.id = $1 AND u.tipo = 'fornecedor'`,
      [id]
    );
    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Fornecedor nao encontrado.' });
    }
    return res.json(result.rows[0]);
  } catch (error) {
    console.error('Erro ao buscar fornecedor:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};