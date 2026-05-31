package com.app.pertodemim.model;

public class PedidoResponse {
    private int id, servico_id, cliente_id, fornecedor_id;
    private String status, data_pedido;
    private double valor;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getServico_id() { return servico_id; }
    public void setServico_id(int servico_id) { this.servico_id = servico_id; }
    public int getCliente_id() { return cliente_id; }
    public void setCliente_id(int cliente_id) { this.cliente_id = cliente_id; }
    public int getFornecedor_id() { return fornecedor_id; }
    public void setFornecedor_id(int fornecedor_id) { this.fornecedor_id = fornecedor_id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getData_pedido() { return data_pedido; }
    public void setData_pedido(String data_pedido) { this.data_pedido = data_pedido; }
}
