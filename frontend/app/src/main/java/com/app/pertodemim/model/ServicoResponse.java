package com.app.pertodemim.model;

public class ServicoResponse {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int categoria_id;
    private int fornecedor_id;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getCategoria_id() { return categoria_id; }
    public void setCategoria_id(int categoria_id) { this.categoria_id = categoria_id; }
    public int getFornecedor_id() { return fornecedor_id; }
    public void setFornecedor_id(int fornecedor_id) { this.fornecedor_id = fornecedor_id; }
}
