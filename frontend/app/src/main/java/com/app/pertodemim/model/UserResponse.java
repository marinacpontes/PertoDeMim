package com.app.pertodemim.model;

import java.util.List;

public class UserResponse {
    private String mensagem, token, erro;
    private User usuario;
    private List<String> erros;

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
