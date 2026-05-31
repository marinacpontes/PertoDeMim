package com.app.pertodemim.model;

public class CadastroRequest {
    private String nome, email, senha, tipo, telefone, cpf_cnpj, data_nascimento, logradouro, cep, numero, bairro, complemento, cidade, estado;

    public CadastroRequest(String nome, String email, String senha, String tipo, String telefone, String cpf_cnpj, String data_nascimento, String logradouro, String cep, String numero, String bairro, String complemento, String cidade, String estado) {
        this.nome = nome; this.email = email; this.senha = senha; this.tipo = tipo; this.telefone = telefone; this.cpf_cnpj = cpf_cnpj;
        this.data_nascimento = data_nascimento; this.logradouro = logradouro; this.cep = cep; this.numero = numero; this.bairro = bairro;
        this.complemento = complemento; this.cidade = cidade; this.estado = estado;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
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
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
