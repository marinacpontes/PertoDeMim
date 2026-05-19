package com.app.pertodemim.model;

import java.util.List;

// Modelo para a resposta da API
public class UserResponse {
    private String mensagem;
    private User usuario;
    private String token;
    private List<String> erros;
    private String erro;

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public List<String> getErros() { return erros; }
    public void setErros(List<String> erros) { this.erros = erros; }

    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }
}
