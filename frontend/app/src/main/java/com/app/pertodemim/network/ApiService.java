package com.app.pertodemim.network;

import com.app.pertodemim.model.LoginRequest;
import com.app.pertodemim.model.User;
import com.app.pertodemim.model.UserResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// Interface para as chamadas da API
public interface ApiService {

    @POST("usuarios")
    Call<UserResponse> createUser(@Body User user);

    @GET("usuarios")
    Call<List<User>> getUsers();

    @GET("usuarios/{id}")
    Call<User> getUserById(@Path("id") int id);

    @POST("auth/login")
    Call<UserResponse> login(@Body LoginRequest loginRequest);
}
