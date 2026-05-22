package com.app.pertodemim.model;

public class CadastroFornecedorRequest {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cpf_cnpj;
    private String data_nascimento;
    private String logradouro;
    private String cep;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String nome_loja;
    private String nome_responsavel;
    private String categoria;
    private String descricao;
    private double preco_medio;

    public CadastroFornecedorRequest(String nome, String email, String senha, String telefone, String cpf_cnpj, String data_nascimento, String logradouro, String cep, String numero, String bairro, String cidade, String estado, String nome_loja, String nome_responsavel, String categoria, String descricao, double preco_medio) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpf_cnpj = cpf_cnpj;
        this.data_nascimento = data_nascimento;
        this.logradouro = logradouro;
        this.cep = cep;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.nome_loja = nome_loja;
        this.nome_responsavel = nome_responsavel;
        this.categoria = categoria;
        this.descricao = descricao;
        this.preco_medio = preco_medio;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCpf_cnpj() { return cpf_cnpj; }
    public void setCpf_cnpj(String cpf_cnpj) { this.cpf_cnpj = cpf_cnpj; }
    public String getData_nascimento() { return data_nascimento; }
    public void setData_nascimento(String data_nascimento) { this.data_nascimento = data_nascimento; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNome_loja() { return nome_loja; }
    public void setNome_loja(String nome_loja) { this.nome_loja = nome_loja; }
    public String getNome_responsavel() { return nome_responsavel; }
    public void setNome_responsavel(String nome_responsavel) { this.nome_responsavel = nome_responsavel; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getPreco_medio() { return preco_medio; }
    public void setPreco_medio(double preco_medio) { this.preco_medio = preco_medio; }
}
