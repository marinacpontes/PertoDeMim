package com.app.pertodemim.network;

import com.app.pertodemim.model.CadastroFornecedorRequest;
import com.app.pertodemim.model.CadastroRequest;
import com.app.pertodemim.model.LoginRequest;
import com.app.pertodemim.model.PedidoRequest;
import com.app.pertodemim.model.PedidoResponse;
import com.app.pertodemim.model.ServicoResponse;
import com.app.pertodemim.model.User;
import com.app.pertodemim.model.UserResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

// Interface para as chamadas da API
public interface ApiService {

    // Login
    @POST("auth/login")
    Call<UserResponse> login(@Body LoginRequest body);

    // Cadastro de cliente
    @POST("usuarios")
    Call<UserResponse> cadastrarCliente(@Body CadastroRequest body);

    // Cadastro genérico (usado pelas Activities)
    @POST("usuarios")
    Call<UserResponse> createUser(@Body User user);

    // Cadastro de fornecedor
    @POST("fornecedores")
    Call<UserResponse> cadastrarFornecedor(@Body CadastroFornecedorRequest body);

    // Listar servicos (publico)
    @GET("servicos")
    Call<List<ServicoResponse>> listarServicos();

    // Criar pedido (protegida)
    @POST("pedidos")
    Call<PedidoResponse> criarPedido(@Header("Authorization") String token, @Body PedidoRequest body);
}
