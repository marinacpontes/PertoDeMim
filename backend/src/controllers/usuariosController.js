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

// Valida data de nascimento com idade minima por tipo
// cliente: minimo 16 anos
// fornecedor: minimo 18 anos
function validarDataNascimento(data, tipo) {
  const regex = /^\d{4}-\d{2}-\d{2}$/;
  if (!regex.test(data)) return { valido: false, mensagem: 'Data de nascimento invalida. Use o formato YYYY-MM-DD.' };

  const dataObj = new Date(data);
  if (isNaN(dataObj.getTime())) return { valido: false, mensagem: 'Data de nascimento invalida.' };

  // Nao pode ser data futura
  if (dataObj > new Date()) return { valido: false, mensagem: 'Data de nascimento nao pode ser uma data futura.' };

  const hoje = new Date();

  // Idade minima conforme tipo
  const idadeMinima = tipo === 'fornecedor' ? 18 : 16;
  const limiteIdadeMinima = new Date(hoje.getFullYear() - idadeMinima, hoje.getMonth(), hoje.getDate());
  if (dataObj > limiteIdadeMinima) {
    return {
      valido: false,
      mensagem: `${tipo === 'fornecedor' ? 'Fornecedor' : 'Cliente'} deve ter no minimo ${idadeMinima} anos.`
    };
  }

  // Idade maxima de 120 anos
  const limiteIdadeMaxima = new Date(hoje.getFullYear() - 120, hoje.getMonth(), hoje.getDate());
  if (dataObj < limiteIdadeMaxima) return { valido: false, mensagem: 'Data de nascimento invalida.' };

  return { valido: true };
}

// =========================================
// CRIAR USUARIO
// =========================================
export const criarUsuario = async (req, res) => {
  try {
    const {
      nome, email, senha, tipo, telefone, cpf_cnpj,
      logradouro, cep, numero, bairro, complemento, cidade, estado,
      data_nascimento,
    } = req.body;

    const erros = [];

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

    if (!tipo || !['cliente', 'fornecedor'].includes(tipo)) {
      erros.push('Tipo deve ser "cliente" ou "fornecedor".');
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

    // Valida data de nascimento — regra depende do tipo
    if (!data_nascimento) {
      erros.push('Data de nascimento e obrigatoria.');
    } else if (tipo && ['cliente', 'fornecedor'].includes(tipo)) {
      const resultadoData = validarDataNascimento(data_nascimento, tipo);
      if (!resultadoData.valido) {
        erros.push(resultadoData.mensagem);
      }
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

    if (erros.length > 0) {
      return res.status(400).json({ erros });
    }

    const senhaHash = await bcrypt.hash(senha, 10);

    const result = await pool.query(
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
        tipo,
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

    return res.status(201).json({
      mensagem: 'Usuario criado com sucesso!',
      usuario: result.rows[0],
    });

 } catch (error) {
    if (error.code === '23505') {
      if (error.constraint === 'usuarios_cpf_cnpj_unique') {
        return res.status(409).json({ erro: 'CPF ou CNPJ ja cadastrado.' });
      }
      return res.status(409).json({ erro: 'E-mail ja cadastrado.' });
    }
    console.error('Erro ao criar usuario:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// LISTAR USUARIOS
// =========================================
export const listarUsuarios = async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT id, nome, email, telefone, cpf_cnpj, tipo,
              logradouro, cep, numero, bairro, complemento, cidade, estado,
              data_nascimento, created_at
       FROM usuarios ORDER BY id`
    );
    return res.json(result.rows);
  } catch (error) {
    console.error('Erro ao listar usuarios:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};

// =========================================
// BUSCAR USUARIO POR ID
// =========================================
export const buscarUsuarioPorId = async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query(
      `SELECT id, nome, email, telefone, cpf_cnpj, tipo,
              logradouro, cep, numero, bairro, complemento, cidade, estado,
              data_nascimento, created_at
       FROM usuarios WHERE id = $1`,
      [id]
    );
    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Usuario nao encontrado.' });
    }
    return res.json(result.rows[0]);
  } catch (error) {
    console.error('Erro ao buscar usuario:', error);
    return res.status(500).json({ erro: 'Erro interno no servidor.' });
  }
};