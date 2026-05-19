package com.app.pertodemim.model;

import com.google.gson.annotations.SerializedName;

// Modelo de dados para o Usuário
public class User {
    private Integer id;
    private String nome;
    private String email;
    private String senha;
    private String tipo; // "cliente" ou "fornecedor"
    private String telefone;
    private String cpf_cnpj;
    private String logradouro;
    private String cep;
    private String numero;
    private String bairro;
    private String complemento;
    private String cidade;
    private String estado;
    
    @SerializedName("data_nascimento")
    private String dataNascimento;
    
    @SerializedName("created_at")
    private String createdAt;

    // Construtor vazio
    public User() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public String getCpfCnpj() { return cpf_cnpj; }
    public void setCpfCnpj(String cpf_cnpj) { this.cpf_cnpj = cpf_cnpj; }

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

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
