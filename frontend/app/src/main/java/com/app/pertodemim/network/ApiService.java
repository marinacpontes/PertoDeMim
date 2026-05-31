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

public interface ApiService {
    @POST("auth/login") Call<UserResponse> login(@Body LoginRequest body);
    @POST("usuarios") Call<UserResponse> cadastrarCliente(@Body CadastroRequest body);
    @POST("usuarios") Call<UserResponse> createUser(@Body User user);
    @POST("fornecedores") Call<UserResponse> cadastrarFornecedor(@Body CadastroFornecedorRequest body);
    @GET("servicos") Call<List<ServicoResponse>> listarServicos();
    @POST("pedidos") Call<PedidoResponse> criarPedido(@Header("Authorization") String token, @Body PedidoRequest body);
}
