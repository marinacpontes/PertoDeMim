package com.app.pertodemim.model;

public class PedidoRequest {
    private int servico_id;
    public PedidoRequest(int servico_id) { this.servico_id = servico_id; }
    public int getServico_id() { return servico_id; }
    public void setServico_id(int servico_id) { this.servico_id = servico_id; }
}
